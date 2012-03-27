/**
 * Copyright (C) 2007-2008 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File SODeserializer.java
 * Date 2008-12-1
 */
package org.eclipse.jt.core.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.InvalidObjectException;
import java.io.StreamCorruptedException;
import java.io.UnsupportedEncodingException;
import java.io.WriteAbortedException;
import java.util.Arrays;
import java.util.HashMap;

import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.type.DataType;
import org.eclipse.jt.core.type.GUID;
import org.eclipse.jt.core.type.Undigester;


/**
 * 
 * @author Jeff Tang
 * @version 1.0
 */
// TODO 注释
final class SODeserializer implements StructuredObjectDeserializer {
    /** handle value representing null */
    private static final int NULL_HANDLE = -1;

    /** table mapping primitive type names to corresponding class objects */
    private static final HashMap<String, Class<?>> primClasses = new HashMap<String, Class<?>>(
            9, 1.0F);
    static {
        primClasses.put("boolean", boolean.class);
        primClasses.put("byte", byte.class);
        primClasses.put("char", char.class);
        primClasses.put("short", short.class);
        primClasses.put("int", int.class);
        primClasses.put("long", long.class);
        primClasses.put("float", float.class);
        primClasses.put("double", double.class);
        primClasses.put("void", void.class);
    }

    private final StructAdapterSet saContext;
    private final StructDefineProvider sdProvider;

    /** reader for handling block data conversion */
    private final DataDeserializer in;
    /** wire handle -> obj map */
    private final HandleTable handles;
    /** scratch field for passing handle values up/down call stack */
    private int passHandle = NULL_HANDLE;
    /** recursion depth */
    private int depth;

    /** whether stream is closed */
    private boolean closed;

    SODeserializer(StructAdapterSet structAdapterContext, DataDeserializer in)
            throws StreamCorruptedException, IOException {
        this(structAdapterContext, in, null);
    }

    SODeserializer(StructAdapterSet structAdapterContext, DataDeserializer in,
            StructDefineProvider structDefineProvider)
            throws StreamCorruptedException, IOException {
        if (structAdapterContext == null) {
            throw new NullArgumentException("structAdapterContext");
        }
        if (in == null) {
            throw new NullArgumentException("in");
        }
        this.saContext = structAdapterContext;
        this.in = in;
        this.sdProvider = structDefineProvider;
        this.readStreamHeader();
        this.handles = new HandleTable(10);
    }

    /**
     * The readStreamHeader method is provided to read and verify the stream
     * header. It reads and verifies the magic number and version number.
     * 
     * @throws IOException
     *             if there are I/O errors while reading from the underlying
     *             <code>InputStream</code>
     * @throws StreamCorruptedException
     *             if control information in the stream is inconsistent
     */
    private void readStreamHeader() throws StreamCorruptedException,
            IOException {
        if (this.in.readShort() != SerialStreamConstants.STREAM_MAGIC
                || this.in.readShort() != SerialStreamConstants.STREAM_VERSION) {
            throw new StreamCorruptedException("invalid stream header");
        }
    }

    private void clear() {
        this.handles.clear();
    }

    public void close() throws IOException {
        this.closed = true;
        this.clear();
        this.in.close();
    }

    private static interface CheckedReader<T> {
        boolean validTC(byte tc);

        T read(SODeserializer sod) throws IOException,
                StructDefineNotFoundException;
    }

    private static final CheckedReader<String> SR = new CheckedReader<String>() {
        public String read(SODeserializer sod) throws IOException {
            return sod.readNewString();
        }

        public boolean validTC(byte tc) {
            return (SerialStreamConstants.TC_STRING == tc || SerialStreamConstants.TC_LONGSTRING == tc);
        }
    };

    private static final CheckedReader<GUID> GR = new CheckedReader<GUID>() {
        public GUID read(SODeserializer sod) throws IOException {
            return sod.readNewGUID();
        }

        public boolean validTC(byte tc) {
            return SerialStreamConstants.TC_GUID == tc;
        }
    };

    private static final CheckedReader<Class<?>> CR = new CheckedReader<Class<?>>() {
        public Class<?> read(SODeserializer sod) throws IOException,
                StructDefineNotFoundException {
            return sod.readNewClass();
        }

        public boolean validTC(byte tc) {
            return SerialStreamConstants.TC_CLASS == tc;
        }
    };

    private static final CheckedReader<Enum<?>> ER = new CheckedReader<Enum<?>>() {
        public Enum<?> read(SODeserializer sod) throws IOException,
                StructDefineNotFoundException {
            return sod.readNewEnum();
        }

        public boolean validTC(byte tc) {
            return SerialStreamConstants.TC_ENUM == tc;
        }
    };

    private static final CheckedReader<Object> OR = new CheckedReader<Object>() {
        public Object read(SODeserializer sod) throws IOException,
                StructDefineNotFoundException {
            return sod.readNewObject();
        }

        public boolean validTC(byte tc) {
            return true; // ?
        }
    };

    @SuppressWarnings("unchecked")
    private static <T> T internalCheckRead(SODeserializer sod,
            CheckedReader<T> reader) throws IOException,
            StructDefineNotFoundException {
        byte tc = sod.in.peekByte();
        sod.depth++;
        try {
            switch (tc) {
            case SerialStreamConstants.TC_NULL:
                return (T) sod.readNull();
            case SerialStreamConstants.TC_REFERENCE:
                return (T) sod.readHandle();
            default:
                if (reader.validTC(tc)) {
                    return reader.read(sod);
                } else {
                    throw new StreamCorruptedException();
                }
            }
        } finally {
            sod.depth--;
        }
    }

    private static <T> T checkRead(SODeserializer sod, CheckedReader<T> reader)
            throws IOException, StructDefineNotFoundException {
        // if nested read, passHandle contains handle of enclosing object
        int outerHandle = sod.passHandle;
        try {
            T obj = internalCheckRead(sod, reader);
            sod.handles.markDependency(outerHandle, sod.passHandle);
            ClassNotFoundException ex = sod.handles
                    .lookupException(sod.passHandle);
            if (ex != null) {
                throw new StructDefineNotFoundException(ex);
            }
            return obj;
        } finally {
            sod.passHandle = outerHandle;
            if (sod.closed && sod.depth == 0) {
                sod.clear();
            }
        }
    }

    public final Object deserialize() throws StructDefineNotFoundException,
            IOException {
        return this.readObject();
    }

    // /////////////////////////////////////////////////////////////////////////
    /*----------------以下方法只提供给外部调用---------------------------------*/
    private final String readString() throws IOException,
            StructDefineNotFoundException {
        return checkRead(this, SR);
    }

    private final GUID readGUID() throws IOException,
            StructDefineNotFoundException {
        return checkRead(this, GR);
    }

    private final Enum<?> readEnum() throws IOException,
            StructDefineNotFoundException {
        return checkRead(this, ER);
    }

    private final Object readObject() throws IOException,
            StructDefineNotFoundException {
        return checkRead(this, OR);
    }

    /*----------------以上方法只提供给外部调用---------------------------------*/
    // /////////////////////////////////////////////////////////////////////////
    /*----------------以下方法只提供给内部调用---------------------------------*/
    private final String internalReadString() throws IOException,
            StructDefineNotFoundException {
        return internalCheckRead(this, SR);
    }

    private final GUID internalReadGUID() throws IOException,
            StructDefineNotFoundException {
        return internalCheckRead(this, GR);
    }

    private final Class<?> internalReadClass() throws IOException,
            StructDefineNotFoundException {
        return internalCheckRead(this, CR);
    }

    private final Enum<?> internalReadEnum() throws IOException,
            StructDefineNotFoundException {
        return internalCheckRead(this, ER);
    }

    /*----------------以上方法只提供给外部调用---------------------------------*/
    // /////////////////////////////////////////////////////////////////////////
    /**
     * Reads in null code, sets passHandle to NULL_HANDLE and returns null.
     */
    private Object readNull() throws IOException {
        if (this.in.readByte() != SerialStreamConstants.TC_NULL) {
            throw new StreamCorruptedException();
        }
        this.passHandle = NULL_HANDLE;
        return null;
    }

    /**
     * Reads in object handle, sets passHandle to the read handle, and returns
     * object associated with the handle.
     */
    private Object readHandle() throws IOException {
        if (this.in.readByte() != SerialStreamConstants.TC_REFERENCE) {
            throw new StreamCorruptedException();
        }
        this.passHandle = this.in.readInt()
                - SerialStreamConstants.baseWireHandle;
        if (this.passHandle < 0 || this.passHandle >= this.handles.size()) {
            throw new StreamCorruptedException("错误的句柄值：" + this.passHandle);
        }
        return this.handles.lookupObject(this.passHandle);
    }

    private String readNewString() throws IOException {
        String str = this.readUTF8();
        this.passHandle = this.handles.assign(str);
        this.handles.finish(this.passHandle);
        return str;
    }

    private GUID readNewGUID() throws IOException {
        if (this.in.readByte() != SerialStreamConstants.TC_GUID) {
            throw new StreamCorruptedException();
        }
        // (mostSigBits, leastSigBits)
        GUID id = GUID.valueOf(this.in.readLong(), this.in.readLong());
        this.passHandle = this.handles.assign(id);
        this.handles.finish(this.passHandle);
        return id;
    }

    /**
     * Reads in and returns class object. Sets passHandle to class object's
     * assigned handle. Returns null if class is unresolvable (in which case a
     * ClassNotFoundException will be associated with the class' handle in the
     * handle table).
     */
    private Class<?> readNewClass() throws IOException {
        try {
            return this.readClass0();
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    private Class<?> readClass0() throws ClassNotFoundException,
            StreamCorruptedException, IOException {
        if (this.in.readByte() != SerialStreamConstants.TC_CLASS) {
            throw new StreamCorruptedException();
        }
        this.passHandle = this.handles.assign(null);
        String className = this.readUTF8();
        Class<?> cl = null;
        ClassNotFoundException ex = null;
        try {
            cl = this.saContext.getLocalClass(className);
        } catch (ClassNotFoundException e) {
            cl = primClasses.get(className);
            if (cl == null) {
                ex = e;
            }
        }
        this.handles.setObject(this.passHandle, cl);

        if (ex != null) {
            this.handles.markException(this.passHandle, ex);
        }

        this.handles.finish(this.passHandle);

        if (ex != null) {
            throw ex;
        }
        return cl;
    }

    /**
     * Reads in and returns enum constant, or null if enum type is unresolvable.
     * Sets passHandle to enum constant's assigned handle.
     */
    @SuppressWarnings("unchecked")
    private Enum<?> readNewEnum() throws IOException {
        if (this.in.readByte() != SerialStreamConstants.TC_ENUM) {
            throw new StreamCorruptedException();
        }
        int enumHandle = this.passHandle = this.handles.assign(null);

        Class<? extends Enum> enumClass = null;
        ClassNotFoundException ex = null;
        try {
            enumClass = (Class) this.readClass0();
            if (!enumClass.isEnum()) {
                throw new InvalidClassException("非枚举类型：" + enumClass);
            }
        } catch (ClassNotFoundException e) {
            ex = e;
        }

        if (ex != null) {
            this.handles.markException(enumHandle, ex);
        }

        String name = this.readUTF8();
        Enum<?> en = null;
        if (enumClass != null) {
            try {
                en = Enum.valueOf(enumClass, name);
            } catch (IllegalArgumentException e) {
                throw (IOException) new InvalidObjectException("enum constant "
                        + name + " does not exist in " + enumClass)
                        .initCause(e);
            }
            this.handles.setObject(enumHandle, en);
        }

        this.handles.finish(enumHandle);
        this.passHandle = enumHandle;
        return en;
    }

    private Object readNewObject() throws IOException,
            StructDefineNotFoundException {
        byte tc = this.in.peekByte();
        switch (tc) {
        case SerialStreamConstants.TC_CLASS:
            return this.readNewClass();
        case SerialStreamConstants.TC_STRUCTDEF:
            return this.readNewStructDefine();
        case SerialStreamConstants.TC_STRING:
            return this.readNewString();
        case SerialStreamConstants.TC_GUID:
            return this.readNewGUID();
        case SerialStreamConstants.TC_ENUM:
            return this.readNewEnum();
        case SerialStreamConstants.TC_EXCEPTION:
            IOException ex = this.readFatalException();
            throw new WriteAbortedException("writing aborted", ex);
        case SerialStreamConstants.TC_OBJECT:
            return this.readOrdinaryObject();
        case SerialStreamConstants.TC_SPECIAL:
            return this.readSpecialObject();
        default:
            throw new StreamCorruptedException("错误的类型代码：" + tc);
        }
    }

    private final StructAdapter readNewStructDefine() throws IOException,
            StructDefineNotFoundException {
        if (this.in.readByte() != SerialStreamConstants.TC_STRUCTDEF) {
            throw new StreamCorruptedException();
        }
        // assign handle for adapter
        int handle = this.passHandle = this.handles.assign(null);
        StructSummary structSummary = this.readNewStructSummary();
        short size = this.in.readShort();
        StructAdapter adapter = StructAdapterSet.newAdapter(structSummary,
                size, this.saContext);
        this.handles.setObject(handle, adapter);

        // 这里需要保证和写的过程严格对称。
        for (short i = 0; i < size; i++) {
            this.passHandle = handle;
            adapter.addNewFieldAdapter(this.readUTF8()/* name */, this.in
                    .readBoolean()/* isStateField */,
                    this.in.readBoolean()/* isReadOnly */, this.in
                            .readBoolean()/* isKeepValid */, DataTypeHelper
                            .readDataType(this.FIELD_TYPE_UNDIGESTER));
        }

        this.handles.finish(handle);
        this.passHandle = handle;
        this.saContext.putAdapter(adapter);
        return adapter;
    }

    private StructSummary readNewStructSummary() throws IOException {
        if (this.in.readByte() != SerialStreamConstants.TC_STRUCTSUM) {
            throw new StreamCorruptedException();
        }
        int handle = this.handles.assign(null); // assign handle for summary
        String name = this.readUTF8();
        byte[] VUID = new byte[16];
        this.in.readFully(VUID);
        boolean isDynamic = this.in.readBoolean();
        StructSummary sum = new StructSummary(name, VUID, isDynamic);
        this.handles.setObject(handle, sum);
        this.handles.finish(handle);
        this.passHandle = handle;
        return sum;
    }

    /**
     * Reads and returns "ordinary" (i.e., not a String, Class,
     * ObjectStreamClass, array, or enum constant) object, or null if object's
     * class is unresolvable (in which case a ClassNotFoundException will be
     * associated with object's handle). Sets passHandle to object's assigned
     * handle.
     */
    private Object readOrdinaryObject() throws IOException,
            StructDefineNotFoundException {
        if (this.in.readByte() != SerialStreamConstants.TC_OBJECT) {
            throw new StreamCorruptedException();
        }
        int handle = this.passHandle = this.handles.assign(null);
        byte tc = this.in.peekByte();
        StructAdapter adapter = null;
        StructSummary summary = null;
        switch (tc) {
        case SerialStreamConstants.TC_REFERENCE:
            Object obj = this.readHandle();
            if (obj instanceof StructSummary) {
                summary = (StructSummary) obj;
                adapter = this.saContext.findAdapter(summary);
            } else {
                adapter = (StructAdapter) obj;
                summary = adapter.remoteStructSummary;
            }
            break;
        case SerialStreamConstants.TC_STRUCTSUM:
            summary = this.readNewStructSummary();
            adapter = this.saContext.findAdapter(summary);
            break;
        case SerialStreamConstants.TC_STRUCTDEF:
            adapter = this.readNewStructDefine();
            summary = adapter.remoteStructSummary;
            break;
        default:
            throw new InternalError();
        }

        Assertion.ASSERT(summary != null);
        if (adapter == null && this.sdProvider != null) {
            adapter = this.sdProvider.getStructDefine(summary);
            if (adapter != null) {
                this.saContext.putAdapter(adapter);
            }
        }

        Object obj = null;
        if (adapter != null) {
            obj = adapter.newEmptySO();
            this.handles.setObject(handle, obj);
        } else {
            this.handles.markException(handle, new ClassNotFoundException(
                    summary.defineName));
        }
        this.passHandle = handle;
        this.readSerialData(obj, adapter);
        this.handles.finish(handle);
        this.passHandle = handle;
        return obj;
    }

    private Object readSpecialObject() throws IOException,
            StructDefineNotFoundException {
        if (this.in.readByte() != SerialStreamConstants.TC_SPECIAL) {
            throw new StreamCorruptedException();
        }
        DataType dt = DataTypeHelper.readDataType(this.FIELD_TYPE_UNDIGESTER);
        if (!(dt instanceof ObjectDataTypeInternal)) {
            throw new StreamCorruptedException("不匹配的数据类型：" + dt);
        }
        ObjectDataTypeInternal assigner = (ObjectDataTypeInternal) dt;
        int handle = this.passHandle = this.handles.assign(null);
        Object data = assigner.readObjectData(this.ALL);
        this.handles.setObject(handle, data);
        this.handles.finish(handle);
        this.passHandle = handle;
        return data;
    }

    /**
     * Reads (or attempts to skip, if obj is null or is tagged with a
     * ClassNotFoundException) instance data for each serializable class of
     * object in stream, from superclass to subclass. Expects that passHandle is
     * set to obj's handle before this method is called.
     */
    private void readSerialData(Object obj, StructAdapter adapter)
            throws IOException, StructDefineNotFoundException {
        int objHandle = this.passHandle;
        StructFieldAdapter[] fields = adapter.fields;
        for (int i = 0, len = fields.length; i < len; i++) {
            this.passHandle = objHandle;
            fields[i].readValueFor(obj, this.ALL);
        }
        this.passHandle = objHandle;
    }

    private final InternalDeserializer ALL = new InternalDeserializer() {
        public int backupHandle() {
            return SODeserializer.this.passHandle;
        }

        public void restoreHandle(int backupHandle) {
            SODeserializer.this.passHandle = backupHandle;
        }

        public Enum<?> readEnum() throws IOException,
                StructDefineNotFoundException {
            return SODeserializer.this.readEnum();
        }

        public Object readObject() throws IOException,
                StructDefineNotFoundException {
            return SODeserializer.this.readObject();
        }

        public GUID readGUID() throws IOException,
                StructDefineNotFoundException {
            return SODeserializer.this.readGUID();
        }

        public String readString() throws IOException,
                StructDefineNotFoundException {
            return SODeserializer.this.readString();
        }

        public boolean readBoolean() throws IOException {
            return SODeserializer.this.in.readBoolean();
        }

        public byte readByte() throws IOException {
            return SODeserializer.this.in.readByte();
        }

        public char readChar() throws IOException {
            return SODeserializer.this.in.readChar();
        }

        public double readDouble() throws IOException {
            return SODeserializer.this.in.readDouble();
        }

        public float readFloat() throws IOException {
            return SODeserializer.this.in.readFloat();
        }

        public int readInt() throws IOException {
            return SODeserializer.this.in.readInt();
        }

        public long readLong() throws IOException {
            return SODeserializer.this.in.readLong();
        }

        public short readShort() throws IOException {
            return SODeserializer.this.in.readShort();
        }

        public void readFully(byte[] bytes) throws IOException {
            SODeserializer.this.in.readFully(bytes);
        }
    };

    /**
     * Reads in and returns IOException that caused serialization to abort. All
     * stream state is discarded prior to reading in fatal exception. Sets
     * passHandle to fatal exception's handle.
     * 
     * TODO 整改掉
     */
    private IOException readFatalException() throws IOException,
            StructDefineNotFoundException {
        if (this.in.readByte() != SerialStreamConstants.TC_EXCEPTION) {
            throw new StreamCorruptedException();
        }
        this.clear();
        Class<?> cl = this.internalReadClass();
        String msg = this.internalReadString();
        return new IOException(cl.getName() + ": " + msg); // XXX
    }

    final byte peekByte() throws IOException {
        return this.in.peekByte();
    }

    private final String readUTF8() throws IOException,
            UnsupportedEncodingException {
        final byte tc = this.in.peekByte();
        int len = 0;
        switch (tc) {
        case SerialStreamConstants.TC_NULL:
            return (String) this.readNull();
        case SerialStreamConstants.TC_STRING:
            this.in.readByte();
            len = this.in.readUnsignedByte();
            break;
        case SerialStreamConstants.TC_LONGSTRING:
            this.in.readByte();
            len = this.in.readInt();
            break;
        default:
            throw new InternalError();
        }
        if (len == 0) {
            return "";
        }
        byte[] b = new byte[len];
        this.in.readFully(b);
        return new String(b, "UTF-8");
    }

    final Undigester FIELD_TYPE_UNDIGESTER = new Undigester() {
        public boolean extractBoolean() throws IOException {
            return SODeserializer.this.in.readBoolean();
        }

        public byte extractByte() throws IOException {
            return SODeserializer.this.in.readByte();
        }

        public byte[] extractBytes() throws IOException {
            int len = SODeserializer.this.in.readInt();
            if (len == -1) {
                return null;
            }
            byte[] b = new byte[len];
            if (len == 0) {
                return b;
            }
            SODeserializer.this.in.readFully(b);
            return b;
        }

        public char extractChar() throws IOException {
            return SODeserializer.this.in.readChar();
        }

        @SuppressWarnings("unchecked")
        public Class extractClass() throws IOException,
                StructDefineNotFoundException {
            return SODeserializer.this.internalReadClass();
        }

        @SuppressWarnings("unchecked")
        public Enum extractEnum() throws IOException,
                StructDefineNotFoundException {
            return SODeserializer.this.internalReadEnum();
        }

        public float extractFloat() throws IOException {
            return SODeserializer.this.in.readFloat();
        }

        public GUID extractGUID() throws IOException,
                StructDefineNotFoundException {
            return SODeserializer.this.internalReadGUID();
        }

        public int extractInt() throws IOException {
            return SODeserializer.this.in.readInt();
        }

        public long extractLong() throws IOException {
            return SODeserializer.this.in.readLong();
        }

        public short extractShort() throws IOException {
            return SODeserializer.this.in.readShort();
        }

        public String extractString() throws IOException,
                StructDefineNotFoundException {
            return SODeserializer.this.internalReadString();
        }
    };

    // /////////////////////////////////////////////////////////////////////////
    // from java.io.ObjectInputStream
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Unsynchronized table which tracks wire handle to object mappings, as well
     * as ClassNotFoundExceptions associated with deserialized objects. This
     * class implements an exception-propagation algorithm for determining which
     * objects should have ClassNotFoundExceptions associated with them, taking
     * into account cycles and discontinuities (e.g., skipped fields) in the
     * object graph.
     * 
     * <p>
     * General use of the table is as follows: during deserialization, a given
     * object is first assigned a handle by calling the assign method. This
     * method leaves the assigned handle in an "open" state, wherein
     * dependencies on the exception status of other handles can be registered
     * by calling the markDependency method, or an exception can be directly
     * associated with the handle by calling markException. When a handle is
     * tagged with an exception, the HandleTable assumes responsibility for
     * propagating the exception to any other objects which depend
     * (transitively) on the exception-tagged object.
     * 
     * <p>
     * Once all exception information/dependencies for the handle have been
     * registered, the handle should be "closed" by calling the finish method on
     * it. The act of finishing a handle allows the exception propagation
     * algorithm to aggressively prune dependency links, lessening the
     * performance/memory impact of exception tracking.
     * 
     * <p>
     * Note that the exception propagation algorithm used depends on handles
     * being assigned/finished in LIFO order; however, for simplicity as well as
     * memory conservation, it does not enforce this constraint.
     */
    // REMIND: add full description of exception propagation algorithm?
    private static class HandleTable {

        /* status codes indicating whether object has associated exception */
        private static final byte STATUS_OK = 1;
        private static final byte STATUS_UNKNOWN = 2;
        private static final byte STATUS_EXCEPTION = 3;

        /** array mapping handle -> object status */
        byte[] status;
        /** array mapping handle -> object/exception (depending on status) */
        Object[] entries;
        /** array mapping handle -> list of dependent handles (if any) */
        HandleList[] deps;
        /** lowest unresolved dependency */
        int lowDep = -1;
        /** number of handles in table */
        int size = 0;

        /**
         * Creates handle table with the given initial capacity.
         */
        HandleTable(int initialCapacity) {
            this.status = new byte[initialCapacity];
            this.entries = new Object[initialCapacity];
            this.deps = new HandleList[initialCapacity];
        }

        /**
         * Assigns next available handle to given object, and returns assigned
         * handle. Once object has been completely deserialized (and all
         * dependencies on other objects identified), the handle should be
         * "closed" by passing it to finish().
         */
        int assign(Object obj) {
            if (this.size >= this.entries.length) {
                this.grow();
            }
            this.status[this.size] = STATUS_UNKNOWN;
            this.entries[this.size] = obj;
            return this.size++;
        }

        /**
         * Registers a dependency (in exception status) of one handle on
         * another. The dependent handle must be "open" (i.e., assigned, but not
         * finished yet). No action is taken if either dependent or target
         * handle is NULL_HANDLE.
         */
        void markDependency(int dependent, int target) {
            if (dependent == NULL_HANDLE || target == NULL_HANDLE) {
                return;
            }
            switch (this.status[dependent]) {

            case STATUS_UNKNOWN:
                switch (this.status[target]) {
                case STATUS_OK:
                    // ignore dependencies on objs with no exception
                    break;

                case STATUS_EXCEPTION:
                    // eagerly propagate exception
                    this.markException(dependent,
                            (ClassNotFoundException) this.entries[target]);
                    break;

                case STATUS_UNKNOWN:
                    // add to dependency list of target
                    if (this.deps[target] == null) {
                        this.deps[target] = new HandleList();
                    }
                    this.deps[target].add(dependent);

                    // remember lowest unresolved target seen
                    if (this.lowDep < 0 || this.lowDep > target) {
                        this.lowDep = target;
                    }
                    break;

                default:
                    throw new InternalError();
                }
                break;

            case STATUS_EXCEPTION:
                break;

            default:
                throw new InternalError();
            }
        }

        /**
         * Associates a ClassNotFoundException (if one not already associated)
         * with the currently active handle and propagates it to other
         * referencing objects as appropriate. The specified handle must be
         * "open" (i.e., assigned, but not finished yet).
         */
        void markException(int handle, ClassNotFoundException ex) {
            switch (this.status[handle]) {
            case STATUS_UNKNOWN:
                this.status[handle] = STATUS_EXCEPTION;
                this.entries[handle] = ex;

                // propagate exception to dependents
                HandleList dlist = this.deps[handle];
                if (dlist != null) {
                    int ndeps = dlist.size();
                    for (int i = 0; i < ndeps; i++) {
                        this.markException(dlist.get(i), ex);
                    }
                    this.deps[handle] = null;
                }
                break;

            case STATUS_EXCEPTION:
                break;

            default:
                throw new InternalError();
            }
        }

        /**
         * Marks given handle as finished, meaning that no new dependencies will
         * be marked for handle. Calls to the assign and finish methods must
         * occur in LIFO order.
         */
        void finish(int handle) {
            int end;
            if (this.lowDep < 0) {
                // no pending unknowns, only resolve current handle
                end = handle + 1;
            } else if (this.lowDep >= handle) {
                // pending unknowns now clearable, resolve all upward handles
                end = this.size;
                this.lowDep = -1;
            } else {
                // unresolved backrefs present, can't resolve anything yet
                return;
            }

            // change STATUS_UNKNOWN -> STATUS_OK in selected span of handles
            for (int i = handle; i < end; i++) {
                switch (this.status[i]) {
                case STATUS_UNKNOWN:
                    this.status[i] = STATUS_OK;
                    this.deps[i] = null;
                    break;

                case STATUS_OK:
                case STATUS_EXCEPTION:
                    break;

                default:
                    throw new InternalError();
                }
            }
        }

        /**
         * Assigns a new object to the given handle. The object previously
         * associated with the handle is forgotten. This method has no effect if
         * the given handle already has an exception associated with it. This
         * method may be called at any time after the handle is assigned.
         */
        void setObject(int handle, Object obj) {
            switch (this.status[handle]) {
            case STATUS_UNKNOWN:
            case STATUS_OK:
                this.entries[handle] = obj;
                break;

            case STATUS_EXCEPTION:
                break;

            default:
                throw new InternalError();
            }
        }

        /**
         * Looks up and returns object associated with the given handle. Returns
         * null if the given handle is NULL_HANDLE, or if it has an associated
         * ClassNotFoundException.
         */
        Object lookupObject(int handle) {
            return (handle != NULL_HANDLE && this.status[handle] != STATUS_EXCEPTION) ? this.entries[handle]
                    : null;
        }

        /**
         * Looks up and returns ClassNotFoundException associated with the given
         * handle. Returns null if the given handle is NULL_HANDLE, or if there
         * is no ClassNotFoundException associated with the handle.
         */
        ClassNotFoundException lookupException(int handle) {
            return (handle != NULL_HANDLE && this.status[handle] == STATUS_EXCEPTION) ? (ClassNotFoundException) this.entries[handle]
                    : null;
        }

        /**
         * Resets table to its initial state.
         */
        void clear() {
            Arrays.fill(this.status, 0, this.size, (byte) 0);
            Arrays.fill(this.entries, 0, this.size, null);
            Arrays.fill(this.deps, 0, this.size, null);
            this.lowDep = -1;
            this.size = 0;
        }

        /**
         * Returns number of handles registered in table.
         */
        int size() {
            return this.size;
        }

        /**
         * Expands capacity of internal arrays.
         */
        private void grow() {
            int newCapacity = (this.entries.length << 1) + 1;

            byte[] newStatus = new byte[newCapacity];
            Object[] newEntries = new Object[newCapacity];
            HandleList[] newDeps = new HandleList[newCapacity];

            System.arraycopy(this.status, 0, newStatus, 0, this.size);
            System.arraycopy(this.entries, 0, newEntries, 0, this.size);
            System.arraycopy(this.deps, 0, newDeps, 0, this.size);

            this.status = newStatus;
            this.entries = newEntries;
            this.deps = newDeps;
        }

        /**
         * Simple growable list of (integer) handles.
         */
        private static class HandleList {
            private int[] list = new int[4];
            private int size = 0;

            public HandleList() {
            }

            public void add(int handle) {
                if (this.size >= this.list.length) {
                    int[] newList = new int[this.list.length << 1];
                    System
                            .arraycopy(this.list, 0, newList, 0,
                                    this.list.length);
                    this.list = newList;
                }
                this.list[this.size++] = handle;
            }

            public int get(int index) {
                if (index >= this.size) {
                    throw new ArrayIndexOutOfBoundsException();
                }
                return this.list[index];
            }

            public int size() {
                return this.size;
            }
        }
    }

    // ////////////////////////////////////////////////////////////////////////
    // 下面的方法不推荐使用。仅限于某些测试用途。
    // ////////////////////////////////////////////////////////////////////////
    static Object fromBinary(StructAdapterSet structAdapterContext,
            Endianness endian, byte[] objBinary) throws IOException,
            StructDefineNotFoundException {
        if (objBinary == null || objBinary.length == 0) {
            return null;
        }
        ByteArrayInputStream in = new ByteArrayInputStream(objBinary);
        SODeserializer sod = new SODeserializer(structAdapterContext,
                new StreamBasedDataDeserializer(in, endian));
        Object obj = sod.deserialize();
        sod.close();
        return obj;
    }
}
