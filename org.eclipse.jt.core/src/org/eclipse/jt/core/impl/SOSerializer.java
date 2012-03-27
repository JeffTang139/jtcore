/**
 * Copyright (C) 2007-2008 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File SOSerializer.java
 * Date 2008-12-1
 */
package org.eclipse.jt.core.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.type.DataType;
import org.eclipse.jt.core.type.Digester;
import org.eclipse.jt.core.type.GUID;


/**
 * A SOSerializer writes primitive data types and graphs of structured objects
 * to an OutputStream. The objects can be read (reconstituted) using a
 * SODeserializer. Persistent storage of objects can be accomplished by using a
 * file for the stream. If the stream is a network socket stream, the objects
 * can be reconstituted on another host or in another process.
 * 
 * @author Jeff Tang
 * @version 1.0
 */
// TODO 注释
final class SOSerializer implements StructuredObjectSerializer {

	/** writer for handling block data conversion */
	private final DataSerializer out;
	/** obj -> wire handle map */
	private final HandleTable handles;
	/** recursion depth */
	private int depth;

	SOSerializer(DataSerializer out) throws IOException {
		if (out == null) {
			throw new NullArgumentException("out");
		}
		this.out = out;
		this.writeStreamHeader();
		this.handles = new HandleTable(10, (float) 3.00);
	}

	/**
	 * The writeStreamHeader method is provided to append or prepend header to
	 * the stream. It writes the magic number and version to the stream.
	 */
	private void writeStreamHeader() throws IOException {
		this.out.writeShort(SerialStreamConstants.STREAM_MAGIC);
		this.out.writeShort(SerialStreamConstants.STREAM_VERSION);
	}

	private void clear() {
		this.handles.clear();
	}

	public final void close() throws IOException {
		this.clear();
		this.out.close();
	}

	public final void flush() throws IOException {
		this.out.flush();
	}

	/**
	 * 非递归的第一层次被调用的writeXXX方法（可公开成public），调用此过程。
	 * 在递归层次中被调用的writeXXX方法（同包访问级别），调用checkWrite()过程。
	 */
	private static <T> void tryCheckWrite(SOSerializer sos,
			CheckedWriter<T> writer, T obj) throws IOException,
			StructDefineNotFoundException {
		try {
			checkWrite(sos, writer, obj);
		} catch (IOException e) {
			if (sos.depth == 0) {
				sos.writeFatalException(e);
			}
			throw e;
		} catch (StructDefineNotFoundException e) {
			if (sos.depth == 0) {
				sos.writeFatalException(new IOException(e.getMessage()));
			}
			throw e;
		}
	}

	/**
	 * 见tryCehckWrite()过程说明。
	 */
	private static <T> void checkWrite(SOSerializer sos,
			CheckedWriter<T> writer, T obj) throws IOException,
			StructDefineNotFoundException {
		sos.depth++;
		try {
			int h;
			if (obj == null) {
				sos.writeNull();
			} else if ((h = sos.handles.lookup(obj)) != -1) {
				sos.writeHandle(h);
			} else {
				writer.write(sos, obj);
			}
		} finally {
			sos.depth--;
		}
	}

	public final void serialize(Object obj) throws IOException,
			StructDefineNotFoundException {
		if (obj == null) {
			this.writeNull();
			return;
		}
		tryCheckWrite(this, OW, obj);
	}

	public final void writeDataOnly(Object obj) throws IOException,
			StructDefineNotFoundException {
		if (obj == null) {
			this.writeNull();
			return;
		}
		boolean old = this.setWriteDataOnly(true);
		try {
			tryCheckWrite(this, OW, obj);
		} finally {
			if (!old) {
				this.setWriteDataOnly(old);
			}
		}
	}

	private final void writeString(String str) throws IOException {
		try {
			checkWrite(this, SW, str);
		} catch (StructDefineNotFoundException e) {
			throw new IOException(e.getMessage());
		}
	}

	private final void writeGUID(GUID guid) throws IOException {
		try {
			checkWrite(this, GW, guid);
		} catch (StructDefineNotFoundException e) {
			throw new IOException(e.getMessage());
		}
	}

	private final void writeClass(Class<?> cl) throws IOException {
		try {
			checkWrite(this, CW, cl);
		} catch (StructDefineNotFoundException e) {
			throw new IOException(e.getMessage());
		}
	}

	private final void writeEnum(Enum<?> en) throws IOException {
		try {
			checkWrite(this, EW, en);
		} catch (StructDefineNotFoundException e) {
			throw new IOException(e.getMessage());
		}
	}

	private final void writeObject(Object obj) throws IOException,
			StructDefineNotFoundException {
		checkWrite(this, OW, obj);
	}

	/**
	 * Writes null code to stream.
	 */
	private void writeNull() throws IOException {
		this.out.writeByte(SerialStreamConstants.TC_NULL);
	}

	/**
	 * Writes given object handle to stream.
	 */
	private void writeHandle(int handle) throws IOException {
		this.out.writeByte(SerialStreamConstants.TC_REFERENCE);
		this.out.writeInt(SerialStreamConstants.baseWireHandle + handle);
	}

	/**
	 * Writes given string to stream, using UTF-8 format.
	 */
	private void writeNewString(String str) throws IOException {
		this.writeUTF8(str);
		this.handles.assign(str);
	}

	private void writeNewGUID(GUID guid) throws IOException {
		this.out.writeByte(SerialStreamConstants.TC_GUID);
		this.out.writeLong(guid.getMostSigBits());
		this.out.writeLong(guid.getLeastSigBits());
		this.handles.assign(guid);
	}

	/**
	 * Writes representation of given class to stream.
	 */
	private void writeNewClass(Class<?> cl) throws IOException {
		this.out.writeByte(SerialStreamConstants.TC_CLASS);
		this.handles.assign(cl);
		this.writeUTF8(cl.getName()); // XXX
	}

	/**
	 * Writes given enum constant to stream.
	 */
	private void writeNewEnum(Enum<?> en) throws IOException {
		this.out.writeByte(SerialStreamConstants.TC_ENUM);
		this.handles.assign(en);
		this.writeNewClass(en.getClass());
		this.writeUTF8(en.name());
	}

	@SuppressWarnings("unchecked")
	private void writeNewObject(Object obj) throws IOException,
			StructDefineNotFoundException {
		if (obj instanceof Class) {
			this.writeNewClass((Class<?>) obj);
		} else if (obj instanceof StructDefineImpl) {
			this.writeNewStructDefine((StructDefineImpl) obj);
		} else if (obj instanceof String) {
			this.writeNewString((String) obj);
		} else if (obj instanceof GUID) {
			this.writeNewGUID((GUID) obj);
		} else if (obj instanceof Enum) {
			this.writeNewEnum((Enum) obj);
		} else {
			final DataType objDataType = DataTypeBase.dataTypeOfJavaObj(obj);
			if (objDataType instanceof StructDefineImpl) {
				this.writeOrdinaryObject(obj, (StructDefineImpl) objDataType);
			} else if (objDataType instanceof ObjectDataTypeInternal) {
				this.writeSpecialObject((ObjectDataTypeInternal) objDataType,
						obj);
			} else {
				throw new StructDefineNotFoundException(obj.getClass()
						.getName());
			}
		}
	}

	private void writeSpecialObject(ObjectDataTypeInternal assigner, Object obj)
			throws IOException, StructDefineNotFoundException {
		this.out.writeByte(SerialStreamConstants.TC_SPECIAL);
		assigner.digestType(this.FIELD_TYPE_DIGESTER);
		this.handles.assign(obj);
		assigner.writeObjectData(this.ALL, obj);
	}

	/**
	 * Write a struct define to the underlying stream.
	 * 
	 * <pre>
	 * newStructDefine:
	 *     TC_STRUCTDEF newHandle newStructSummary structFields
	 *   structFields:
	 *       (short)&lt;count&gt; structFieldDefine[count]
	 *     structFieldDefine:
	 *         fieldauthor fieldname isStateField isReadonly isKeepValid fieldType
	 * </pre>
	 */
	private void writeNewStructDefine(StructDefineImpl define)
			throws IOException {
		this.out.writeByte(SerialStreamConstants.TC_STRUCTDEF);
		this.handles.assign(define);
		this.writeNewStructSummary(StructSummary.get(define));
		StructFieldDefineImpl[] fields = define.serializableFields();
		this.out.writeShort((short) fields.length);
		StructFieldDefineImpl field;
		for (int i = 0, len = fields.length; i < len; i++) {
			field = fields[i];
			this.writeUTF8(field.name);
			this.out.writeBoolean(field.isStateField());
			this.out.writeBoolean(field.isReadonly);
			this.out.writeBoolean(field.isKeepValid);
			if (field.type instanceof StructDefineImpl) {
				RefDataType.objectRefType.digestType(this.FIELD_TYPE_DIGESTER);
			} else {
				field.type.digestType(this.FIELD_TYPE_DIGESTER);
			}
		}
	}

	/**
	 * Write a summary of a struct define to the underlying stream.
	 * 
	 * <pre>
	 * StructSummary:
	 *     TC_STRUCTSUM newHandle structSummary
	 *   structSummary:
	 *       defineName VUID isDynamic {defineAuthor} // defineAuthor is conditional optional
	 * </pre>
	 */
	private void writeNewStructSummary(StructSummary summary)
			throws IOException {
		this.out.writeByte(SerialStreamConstants.TC_STRUCTSUM);
		this.handles.assign(summary);
		this.writeUTF8(summary.defineName);
		this.out.write(summary.serialVUID);
		this.out.writeBoolean(summary.isDynamic);
	}

	/* ------------------------------------------------------------------------ */
	/**
	 * Object Format
	 * 
	 * <pre>
	 * newObject:
	 *     TC_OBJECT newHandle structDesc structdata[]
	 *   structDesc:
	 *       prevObject
	 *       newStructDefine
	 *       newStructSummary
	 *     structdata:
	 * values   // object fields' values
	 * 
	 * <pre>
	 */
	/* ------------------------------------------------------------------------ */

	private boolean writeDataOnly = false;

	private boolean setWriteDataOnly(boolean writeDataOnly) {
		if (this.writeDataOnly == writeDataOnly) {
			return writeDataOnly;
		} else {
			boolean old = this.writeDataOnly;
			this.writeDataOnly = writeDataOnly;
			return old;
		}
	}

	/**
	 * Writes representation of a "ordinary" (i.e., not a String, Class,
	 * StructDefineImpl, array, or enum constant) serializable object to the
	 * stream.
	 */
	private void writeOrdinaryObject(Object obj, StructDefineImpl define)
			throws IOException, StructDefineNotFoundException {
		this.out.writeByte(SerialStreamConstants.TC_OBJECT);
		this.handles.assign(obj);
		int h;
		if (this.writeDataOnly) {
			StructSummary ss = StructSummary.get(define);
			if ((h = this.handles.lookup(ss)) != -1) {
				this.writeHandle(h);
			} else {
				this.writeNewStructSummary(ss);
			}
		} else {
			if ((h = this.handles.lookup(define)) != -1) {
				this.writeHandle(h);
			} else {
				this.writeNewStructDefine(define);
			}
		}
		this.writeObjectData(obj, define);
	}

	private void writeObjectData(Object obj, StructDefineImpl define)
			throws IOException, StructDefineNotFoundException {
		NamedDefineContainerImpl<? extends StructFieldDefineImpl> fields = define.fields;
		StructFieldDefineImpl field;
		for (int i = 0, size = fields.size(); i < size; i++) {
			field = fields.get(i);
			if (field.isStateField()) {
				field.writeOut(obj, this.ALL);
			}
		}
	}

	private final InternalSerializer ALL = new InternalSerializer() {

		public void writeSpecialObject(ObjectDataTypeInternal assigner,
				Object specialObj) throws IOException,
				StructDefineNotFoundException {
			SOSerializer.this.depth++;
			try {
				int h;
				if (specialObj == null) {
					SOSerializer.this.writeNull();
				} else if ((h = SOSerializer.this.handles.lookup(specialObj)) != -1) {
					SOSerializer.this.writeHandle(h);
				} else {
					SOSerializer.this.writeSpecialObject(assigner, specialObj);
				}
			} finally {
				SOSerializer.this.depth--;
			}
		}

		public void writeClass(Class<?> clazz) throws IOException {
			SOSerializer.this.writeClass(clazz);
		}

		public void writeEnum(Enum<?> en) throws IOException {
			SOSerializer.this.writeEnum(en);
		}

		public void writeGUID(GUID guid) throws IOException {
			SOSerializer.this.writeGUID(guid);
		}

		public void writeObject(Object obj) throws IOException,
				StructDefineNotFoundException {
			SOSerializer.this.writeObject(obj);
		}

		public void writeString(String str) throws IOException {
			SOSerializer.this.writeString(str);
		}

		public void writeBoolean(boolean v) throws IOException {
			SOSerializer.this.out.writeBoolean(v);
		}

		public void writeByte(byte v) throws IOException {
			SOSerializer.this.out.writeByte(v);
		}

		public void writeChar(char v) throws IOException {
			SOSerializer.this.out.writeChar(v);
		}

		public void writeDouble(double v) throws IOException {
			SOSerializer.this.out.writeDouble(v);
		}

		public void writeFloat(float v) throws IOException {
			SOSerializer.this.out.writeFloat(v);
		}

		public void writeInt(int v) throws IOException {
			SOSerializer.this.out.writeInt(v);
		}

		public void writeLong(long v) throws IOException {
			SOSerializer.this.out.writeLong(v);
		}

		public void writeShort(short v) throws IOException {
			SOSerializer.this.out.writeShort(v);
		}

		public void writeBytes(byte[] bytes) throws IOException {
			SOSerializer.this.out.write(bytes);
		}
	};

	/**
	 * Attempts to write to stream fatal IOException that has caused
	 * serialization to abort.
	 * 
	 * TODO 整改掉
	 */
	private void writeFatalException(IOException ex) throws IOException {
		/*
		 * Note: the serialization specification states that if a second
		 * IOException occurs while attempting to serialize the original fatal
		 * exception to the stream, then a StreamCorruptedException should be
		 * thrown (section 2.1). However, due to a bug in previous
		 * implementations of serialization, StreamCorruptedExceptions were
		 * rarely (if ever) actually thrown--the "root" exceptions from
		 * underlying streams were thrown instead. This historical behavior is
		 * followed here for consistency.
		 */
		this.clear();
		this.out.writeByte(SerialStreamConstants.TC_EXCEPTION);
		this.writeClass(ex.getClass());
		this.writeString(ex.getMessage());
		this.clear();
	}

	private final void writeUTF8(String str) throws IOException {
		if (str == null) {
			this.writeNull();
		} else {
			byte[] b = str.getBytes("UTF-8");
			if (b.length <= 0xFF) {
				this.out.writeByte(SerialStreamConstants.TC_STRING);
				this.out.writeByte((byte) b.length);
			} else {
				this.out.writeByte(SerialStreamConstants.TC_LONGSTRING);
				this.out.writeInt(b.length);
			}
			this.out.write(b);
		}
	}

	private static interface CheckedWriter<T> {
		void write(SOSerializer sos, T obj) throws IOException,
				StructDefineNotFoundException;
	}

	/** String Writer */
	private static final CheckedWriter<String> SW = new CheckedWriter<String>() {
		public void write(SOSerializer sos, String obj) throws IOException {
			sos.writeNewString(obj);
		}
	};

	/** GUID Writer */
	private static final CheckedWriter<GUID> GW = new CheckedWriter<GUID>() {
		public void write(SOSerializer sos, GUID obj) throws IOException {
			sos.writeNewGUID(obj);
		}
	};

	/** Class Writer */
	private static final CheckedWriter<Class<?>> CW = new CheckedWriter<Class<?>>() {
		public void write(SOSerializer sos, Class<?> obj) throws IOException {
			sos.writeNewClass(obj);
		}
	};

	/** Enum Writer */
	private static final CheckedWriter<Enum<?>> EW = new CheckedWriter<Enum<?>>() {
		public void write(SOSerializer sos, Enum<?> obj) throws IOException {
			sos.writeNewEnum(obj);
		}
	};

	/** Object Writer */
	private static final CheckedWriter<Object> OW = new CheckedWriter<Object>() {
		public void write(SOSerializer sos, Object obj) throws IOException,
				StructDefineNotFoundException {
			sos.writeNewObject(obj);
		}
	};

	private final Digester FIELD_TYPE_DIGESTER = new Digester() {
		public void update(boolean input) {
			try {
				SOSerializer.this.out.writeBoolean(input);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		public void update(byte input) {
			try {
				SOSerializer.this.out.writeByte(input);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		public void update(char input) {
			try {
				SOSerializer.this.out.writeChar(input);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		public void update(short input) {
			try {
				SOSerializer.this.out.writeShort(input);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		public void update(int input) {
			try {
				SOSerializer.this.out.writeInt(input);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		public void update(long input) {
			try {
				SOSerializer.this.out.writeLong(input);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		public void update(double input) {
			try {
				SOSerializer.this.out.writeDouble(input);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		public void update(float input) {
			try {
				SOSerializer.this.out.writeFloat(input);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		public void update(String input) {
			try {
				SOSerializer.this.writeUTF8(input);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		public void update(byte[] input) {
			try {
				if (input == null) {
					SOSerializer.this.out.writeInt(-1);
					return;
				}
				SOSerializer.this.out.writeInt(input.length);
				SOSerializer.this.out.write(input);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		public void update(Class<?> input) {
			try {
				SOSerializer.this.writeClass(input);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		public void update(Enum<?> input) {
			try {
				SOSerializer.this.writeEnum(input);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		public void update(GUID input) {
			try {
				SOSerializer.this.writeGUID(input);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	};

	// /////////////////////////////////////////////////////////////////////////
	// from java.io.ObjectOutputStream
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * Lightweight identity hash table which maps objects to integer handles,
	 * assigned in ascending order.
	 */
	private static class HandleTable {
		/* number of mappings in table/next available handle */
		private int size;
		/* size threshold determining when to expand hash spine */
		private int threshold;
		/* factor for computing size threshold */
		private final float loadFactor;
		/* maps hash value -> candidate handle value */
		private int[] spine;
		/* maps handle value -> next candidate handle value */
		private int[] next;
		/* maps handle value -> associated object */
		private Object[] objs;

		/**
		 * Creates new HandleTable with given capacity and load factor.
		 */
		HandleTable(int initialCapacity, float loadFactor) {
			this.loadFactor = loadFactor;
			this.spine = new int[initialCapacity];
			this.next = new int[initialCapacity];
			this.objs = new Object[initialCapacity];
			this.threshold = (int) (initialCapacity * loadFactor);
			this.clear();
		}

		/**
		 * Assigns next available handle to given object, and returns handle
		 * value. Handles are assigned in ascending order starting at 0.
		 */
		int assign(Object obj) {
			if (this.size >= this.next.length) {
				this.growEntries();
			}
			if (this.size >= this.threshold) {
				this.growSpine();
			}
			this.insert(obj, this.size);
			return this.size++;
		}

		/**
		 * Looks up and returns handle associated with given object, or -1 if no
		 * mapping found.
		 */
		int lookup(Object obj) {
			if (this.size == 0) {
				return -1;
			}
			int index = this.hash(obj) % this.spine.length;
			for (int i = this.spine[index]; i >= 0; i = this.next[i]) {
				if (this.objs[i] == obj) {
					return i;
				}
			}
			return -1;
		}

		/**
		 * Resets table to its initial (empty) state.
		 */
		void clear() {
			Arrays.fill(this.spine, -1);
			Arrays.fill(this.objs, 0, this.size, null);
			this.size = 0;
		}

		// /**
		// * Returns the number of mappings currently in table.
		// */
		// int size() {
		// return this.size;
		// }

		/**
		 * Inserts mapping object -> handle mapping into table. Assumes table is
		 * large enough to accommodate new mapping.
		 */
		private void insert(Object obj, int handle) {
			int index = this.hash(obj) % this.spine.length;
			this.objs[handle] = obj;
			this.next[handle] = this.spine[index];
			this.spine[index] = handle;
		}

		/**
		 * Expands the hash "spine" -- equivalent to increasing the number of
		 * buckets in a conventional hash table.
		 */
		private void growSpine() {
			this.spine = new int[(this.spine.length << 1) + 1];
			this.threshold = (int) (this.spine.length * this.loadFactor);
			Arrays.fill(this.spine, -1);
			for (int i = 0; i < this.size; i++) {
				this.insert(this.objs[i], i);
			}
		}

		/**
		 * Increases hash table capacity by lengthening entry arrays.
		 */
		private void growEntries() {
			int newLength = (this.next.length << 1) + 1;
			int[] newNext = new int[newLength];
			System.arraycopy(this.next, 0, newNext, 0, this.size);
			this.next = newNext;

			Object[] newObjs = new Object[newLength];
			System.arraycopy(this.objs, 0, newObjs, 0, this.size);
			this.objs = newObjs;
		}

		/**
		 * Returns hash value for given object.
		 */
		private int hash(Object obj) {
			return System.identityHashCode(obj) & 0x7FFFFFFF;
		}
	}

	// ////////////////////////////////////////////////////////////////////////
	// 下面的方法不推荐使用。仅限于某些测试用途。
	// ////////////////////////////////////////////////////////////////////////
	static byte[] toBinary(Object obj) throws IOException,
			StructDefineNotFoundException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		SOSerializer sos = new SOSerializer(new StreamBasedDataSerializer(out,
				Endianness.LOCAL_ENDIAN));
		sos.serialize(obj);
		sos.close();
		return out.toByteArray();
	}
}
