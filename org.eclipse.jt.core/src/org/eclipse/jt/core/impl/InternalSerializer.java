/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File AllSerializer.java
 * Date 2009-3-12
 */
package org.eclipse.jt.core.impl;

import java.io.IOException;

import org.eclipse.jt.core.type.GUID;


/**
 * 内部序列化器。
 * 
 * @author Jeff Tang
 * @version 1.0
 */
interface InternalSerializer {
    /**
     * 写入一个布尔值。
     * 
     * @param v
     *            要写入的布尔值。
     * @throws IOException
     *             出现任何读写异常。
     */
    void writeBoolean(boolean v) throws IOException;

    /**
     * 写入一个字节值。
     * 
     * @param v
     *            要写入的字节值。
     * @throws IOException
     *             出现任何读写异常。
     */
    void writeByte(byte v) throws IOException;

    /**
     * 写入一个字符值。
     * 
     * @param v
     *            要写入的字符值。
     * @throws IOException
     *             出现任何读写异常。
     */
    void writeChar(char v) throws IOException;

    /**
     * 写入一个双精度浮点数值。
     * 
     * @param v
     *            要写入的双精度浮点数值。
     * @throws IOException
     *             出现任何读写异常。
     */
    void writeDouble(double v) throws IOException;

    /**
     * 写入一个单精度浮点数值。
     * 
     * @param v
     *            要写入的单精度浮点数值。
     * @throws IOException
     *             出现任何读写异常。
     */
    void writeFloat(float v) throws IOException;

    /**
     * 写入一个整数值。
     * 
     * @param v
     *            要写入的整数值。
     * @throws IOException
     *             出现任何读写异常。
     */
    void writeInt(int v) throws IOException;

    /**
     * 写入一个长整型数值。
     * 
     * @param v
     *            要写入的长整型数值。
     * @throws IOException
     *             出现任何读写异常。
     */
    void writeLong(long v) throws IOException;

    /**
     * 写入一个短整型数值。
     * 
     * @param v
     *            要写入的短整型数值。
     * @throws IOException
     *             出现任何读写异常。
     */
    void writeShort(short v) throws IOException;

    /**
     * 写入一个字节序列。
     * 
     * @param bytes
     *            要写入的字节序列。
     * @throws IOException
     *             出现任何读写异常。
     */
    void writeBytes(byte[] bytes) throws IOException;

    /**
     * 写入一个字符串对象。
     * 
     * @param str
     *            要写入的字符串对象。
     * @throws IOException
     *             出现任何读写异常。
     */
    void writeString(String str) throws IOException;

    /**
     * 写入一个全局唯一标识符对象。
     * 
     * @param guid
     *            要写入的全局唯一标识符对象。
     * @throws IOException
     *             出现任何读写异常。
     */
    void writeGUID(GUID guid) throws IOException;

    /**
     * 写入一个类型实例。
     * 
     * @param clazz
     *            要写入的类型实例。
     * @throws IOException
     *             出现任何读写异常。
     */
    void writeClass(Class<?> clazz) throws IOException;

    /**
     * 写入一个枚举值。
     * 
     * @param str
     *            要写入的枚举值。
     * @throws IOException
     *             出现任何读写异常。
     */
    void writeEnum(Enum<?> en) throws IOException;

    /**
     * 写入一个对象。
     * 
     * @param array
     *            要写入的对象。
     * @throws IOException
     *             出现任何读写异常。
     * @throws StructDefineNotFoundException
     *             找不到合适的结构定义。
     */
    void writeObject(Object obj) throws IOException,
            StructDefineNotFoundException;

    // 需要整改
    void writeSpecialObject(ObjectDataTypeInternal assigner, Object specialObj)
            throws IOException, StructDefineNotFoundException;
}
