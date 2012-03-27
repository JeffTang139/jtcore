/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File AllDeserializer.java
 * Date 2009-3-12
 */
package org.eclipse.jt.core.impl;

import java.io.IOException;

import org.eclipse.jt.core.type.GUID;


/**
 * 内部反序列化器。
 * 
 * @author Jeff Tang
 * @version 1.0
 */
interface InternalDeserializer {
    /**
     * 读取一个布尔值。
     * 
     * @return 布尔值。
     * @throws IOException
     *             出现任何读写异常。
     */
    boolean readBoolean() throws IOException;

    /**
     * 读取一个字节值。
     * 
     * @return 字节值。
     * @throws IOException
     *             出现任何读写异常。
     */
    byte readByte() throws IOException;

    /**
     * 读取一个字符值。
     * 
     * @return 字符值。
     * @throws IOException
     *             出现任何读写异常。
     */
    char readChar() throws IOException;

    /**
     * 读取一个双精度浮点数值。
     * 
     * @return 双精度浮点数值。
     * @throws IOException
     *             出现任何读写异常。
     */
    double readDouble() throws IOException;

    /**
     * 读取一个单精度浮点数值。
     * 
     * @return 单精度浮点数值。
     * @throws IOException
     *             出现任何读写异常。
     */
    float readFloat() throws IOException;

    /**
     * 读取一个整数值。
     * 
     * @return 整数值。
     * @throws IOException
     *             出现任何读写异常。
     */
    int readInt() throws IOException;

    /**
     * 读取一个长整型数值。
     * 
     * @return 长整型数值。
     * @throws IOException
     *             出现任何读写异常。
     */
    long readLong() throws IOException;

    /**
     * 读取一个短整型数值。
     * 
     * @return 短整型数值。
     * @throws IOException
     *             出现任何读写异常。
     */
    short readShort() throws IOException;

    /**
     * 读取一个字符串对象。
     * 
     * @return 字符串对象。
     * @throws IOException
     *             出现任何读写异常。
     * @throws StructDefineNotFoundException
     *             找不到合适的结构定义。
     */
    String readString() throws IOException, StructDefineNotFoundException;

    /**
     * 读取一个全局唯一标识符对象。
     * 
     * @return 全局唯一标识符对象。
     * @throws IOException
     *             出现任何读写异常。
     * @throws StructDefineNotFoundException
     *             找不到合适的结构定义。
     */
    GUID readGUID() throws IOException, StructDefineNotFoundException;

    /**
     * 读取一个枚举值。
     * 
     * @return 枚举值。
     * @throws IOException
     *             出现任何读写异常。
     * @throws StructDefineNotFoundException
     *             找不到合适的结构定义。
     */
    Enum<?> readEnum() throws IOException, StructDefineNotFoundException;

    // /**
    // * 读取一个数组对象。
    // *
    // * @return 数组对象。
    // * @throws IOException
    // * 出现任何读写异常。
    // * @throws StructDefineNotFoundException
    // * 找不到合适的结构定义。
    // */
    // Object readArray() throws IOException, StructDefineNotFoundException;

    /**
     * 读取一个对象。
     * 
     * @return 读到的对象。
     * @throws IOException
     *             出现任何读写异常。
     * @throws StructDefineNotFoundException
     *             出现任何读写异常。
     */
    Object readObject() throws IOException, StructDefineNotFoundException;

    void readFully(byte[] bytes) throws IOException;

    // //////////////////////////////////////////
    // 以下方法有待整改。

    /**
     * 备份反序列化器中记录的刚读到的对象的句柄。
     * 
     * @return 反序列化器中记录的刚读到的对象的句柄。
     */
    int backupHandle();

    /**
     * 还原反序列化器中记录的刚讲到的对象的句柄。
     * 
     * @param backupHandle
     *            先前备份的反序列化器中记录的刚读到的对象的句柄。
     */
    void restoreHandle(int backupHandle);
}
