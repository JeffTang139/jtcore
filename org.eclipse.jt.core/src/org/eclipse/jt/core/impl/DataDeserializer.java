/**
 * Copyright (C) 2007-2008 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File DataDeserializer.java
 * Date 2008-12-1
 */
package org.eclipse.jt.core.impl;

import java.io.IOException;

/**
 * 数据反序列化器。<br/>
 * 本接口提供从底层二进制数据中读取Java的基本类型数据的方法。
 * 
 * @author Jeff Tang
 * @version 1.0
 */
interface DataDeserializer {
    /**
     * 读取一个字节，如果这个字节为<code>0</code>，则返回<code>false</code>，否则返回<code>true</code>。
     * 该方法适用于读取由<code>DataSerializer</code>接口的<code>writeBoolean</code>方法写入的字节。
     * 
     * @return 读到的布尔（<code>boolean</code>）值。
     * @throws IOException
     *             出现任何读写异常。
     */
    boolean readBoolean() throws IOException;

    /**
     * 读取并返回一个字节。该方法适用于读取由<code>DataSerializer</code>接口的<code>writeByte</code>
     * 方法写入的字节。
     * 
     * @return 读到的字节。
     * @throws IOException
     *             出现任何读写异常。
     */
    byte readByte() throws IOException;

    /**
     * 读取一个字节，并在它前面补<code>0</code>转化为一个整数，并返回这个整数。这个整数值的范围在<code>0</code>和
     * <code>255</code>之前（包括首尾数值）。
     * 
     * @return 读到的8位无符号字节值。
     * @throws IOException
     *             出现任何读写异常。
     */
    int readUnsignedByte() throws IOException;

    /**
     * 读取并返回一个Unicode字符（由两个字节组成）。该方法适用于读取由<code>DataSerializer</code>接口的
     * <code>writeChar</code> 方法写入的Unicode字符。
     * 
     * @return 读到的Unicode字符。
     * @throws IOException
     *             出现任何读写异常。
     */
    char readChar() throws IOException;

    /**
     * 读取并返回一个双精度浮点数（由八个字节组成）。该方法适用于读取由<code>DataSerializer</code>接口的
     * <code>writeDouble</code> 方法写入的双精度浮点数。
     * 
     * @return 读到的双精度浮点数。
     * @throws IOException
     *             出现任何读写异常。
     */
    double readDouble() throws IOException;

    /**
     * 读取并返回一个单精度浮点数（由四个字节组成）。该方法适用于读取由<code>DataSerializer</code>接口的
     * <code>writeFloat</code> 方法写入的单精度浮点数。
     * 
     * @return 读到的单精度浮点数。
     * @throws IOException
     *             出现任何读写异常。
     */
    float readFloat() throws IOException;

    /**
     * 读取并返回一个整数（由四个字节组成）。该方法适用于读取由<code>DataSerializer</code>接口的
     * <code>writeInt</code> 方法写入的整数。
     * 
     * @return 读到的整数。
     * @throws IOException
     *             出现任何读写异常。
     */
    int readInt() throws IOException;

    /**
     * 读取并返回一个长整型数值（由八个字节组成）。该方法适用于读取由<code>DataSerializer</code>接口的
     * <code>writeLong</code> 方法写入的长整型数值。
     * 
     * @return 读到的长整型数值。
     * @throws IOException
     *             出现任何读写异常。
     */
    long readLong() throws IOException;

    /**
     * 读取并返回一个短整型数值（由两个字节组成）。该方法适用于读取由<code>DataSerializer</code>接口的
     * <code>writeShort</code> 方法写入的短整型数值。
     * 
     * @return 读到的短整型数值。
     * @throws IOException
     *             出现任何读写异常。
     */
    short readShort() throws IOException;

    /**
     * 读取一些字节，并填充进缓冲数组<code>b</code>中。读取的字节的个数等于<code>b</code>的长度。
     * 
     * 该方法会一直阻塞，直到出现下面几种情况：
     * <ul>
     * <li>有<code>b.length</code>个字节可读，这种情况下方法读完需要的字节便可正常返回。
     * <li>出现任何读写错误（包括在读到需要的字节之前遇到文件结束标记），将会抛出异常。
     * </ul>
     * 
     * 如果<code>b</code>空（<code>null</code>），则抛出<code>NullPointerException</code>
     * 异常。如果<code>b.length</code>是<code>0</code>，则不会读取任何字节。否则的话，读取到的字节将被依次写入缓冲数组
     * <code>b</code>中（从第一个位置开始直到最后一个位置）。如果这个过程中有异常抛出，那么可能已经有一部分字节被写入了缓冲数组
     * <code>b</code>中。
     * 
     * @param b
     *            缓冲数组，用于装载读取到的字节。
     * @throws IOException
     *             出现任何读写异常。
     */
    void readFully(byte[] b) throws IOException;

    /**
     * 查看但并不读取一个字节。
     * 
     * @return 处于数据流中的下一个要读取的字节。
     * @throws IOException
     *             出现任何读写异常。
     */
    byte peekByte() throws IOException;

    /**
     * 关闭数据反序列化器及底层的数据读写通道。如果在此之前该反序列化器已经被关闭，则不进行任何操作。
     * 
     * @throws IOException
     *             出现任何读写异常。
     */
    void close() throws IOException;
}
