/**
 * Copyright (C) 2007-2008 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File Undigester.java
 * Date 2008-12-18
 */
package org.eclipse.jt.core.type;

import java.io.IOException;

import org.eclipse.jt.core.impl.StructDefineNotFoundException;
import org.eclipse.jt.core.type.GUID;


/**
 * 反摘要提取器
 * 
 * @author Jeff Tang
 * @version 1.0
 */
public interface Undigester {
    boolean extractBoolean() throws IOException;

    byte extractByte() throws IOException;

    char extractChar() throws IOException;

    short extractShort() throws IOException;

    int extractInt() throws IOException;

    long extractLong() throws IOException;

    float extractFloat() throws IOException;

    String extractString() throws IOException, StructDefineNotFoundException;

    byte[] extractBytes() throws IOException;

    @SuppressWarnings("unchecked")
    Class extractClass() throws IOException, StructDefineNotFoundException;

    @SuppressWarnings("unchecked")
    Enum extractEnum() throws IOException, StructDefineNotFoundException;

    GUID extractGUID() throws IOException, StructDefineNotFoundException;
}