/**
 * Copyright (C) 2007-2008 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File StructuredObjectDeserializer.java
 * Date 2008-12-1
 */
package org.eclipse.jt.core.impl;

import java.io.IOException;

/**
 * StructuredObjectDeserializer extends the DataDeserializer interface to
 * include the reading of structured objects. DataDeserializer includes methods
 * for the input of primitive types, StructuredObjectDeserializer extends that
 * interface to include objects, arrays, and Strings.
 * 
 * @author Jeff Tang
 * @version 1.0
 */
public interface StructuredObjectDeserializer {
    /**
     * Deserialize an object from the underlying stream. The struct define of
     * the object is read firstly. Then the values of the state fields of the
     * struct define are read. Objects referenced by this object are read
     * transitively so that a complete equivalent graph of objects is
     * reconstructed by deserialize.
     * <p>
     * The root object is completely restored when all of its fields and the
     * objects it references are completely restored.
     * <p>
     * Exceptions are thrown for problems with the underlying InputStream. All
     * exceptions are fatal to the InputStream and leave it in an indeterminate
     * state; it is up to the caller to ignore or recover the stream state.
     * 
     * @return the object deserialized from the underlying stream
     * @exception StructDefineNotFoundException
     *                If the StructDefine of a serialized object cannot be
     *                found.
     * @exception IOException
     *                If any of the usual Input/Output related exceptions occur.
     */
    Object deserialize() throws StructDefineNotFoundException, IOException;

    /**
     * Closes the underlying input stream. Must be called to release any
     * resources associated with the stream.
     * 
     * @exception IOException
     *                If an I/O error has occurred.
     */
    void close() throws IOException;
}
