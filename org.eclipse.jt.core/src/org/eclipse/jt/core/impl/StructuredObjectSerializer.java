/**
 * Copyright (C) 2007-2008 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File StructuredObjectSerializer.java
 * Date 2008-12-1
 */
package org.eclipse.jt.core.impl;

import java.io.IOException;

/**
 * StructuredObjectSerializer extends the DataSerializer interface to include
 * writing of structured objects. DataSerializer includes methods for output of
 * primitive types, StructuredObjectSerializer extends that interface to include
 * structured objects, arrays, and Strings.
 * 
 * @author Jeff Tang
 * @version 1.0
 */
public interface StructuredObjectSerializer {
    /**
     * Write the specified object to the underlying stream. The struct define of
     * the object, the fields of the struct define, and the values of the state
     * fields of the struct define are written. Objects referenced by this
     * object are written transitively so that a complete equivalent graph of
     * objects can be reconstructed by a SODeserializer.
     * <p>
     * Exceptions are thrown for problems with the OutputStream. All exceptions
     * are fatal to the OutputStream, which is left in an indeterminate state,
     * and it is up to the caller to ignore or recover the stream state.
     * 
     * @param obj
     *            the object to be serialized
     * @exception IOException
     *                Any of the usual Input/Output related exceptions.
     * @exception StructDefineNotFoundException
     *                StructDefine of the specified object can not be found.
     */
    void serialize(Object obj) throws IOException,
            StructDefineNotFoundException;

    void writeDataOnly(Object obj) throws IOException,
            StructDefineNotFoundException;

    /**
     * Flushes the underlying stream. This will write any buffered output bytes.
     * 
     * @exception IOException
     *                If an I/O error has occurred.
     */
    void flush() throws IOException;

    /**
     * Closes the underlying stream. This method must be called to release any
     * resources associated with the stream.
     * 
     * @exception IOException
     *                If an I/O error has occurred.
     */
    void close() throws IOException;
}
