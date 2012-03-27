/**
 * Copyright (C) 2007-2008 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File DataSerializer.java
 * Date 2008-12-1
 */
package org.eclipse.jt.core.impl;

import java.io.IOException;

/**
 * The <code>DataSerializer</code> interface provides for converting data from
 * any of the Java primitive types to a series of bytes and writing these bytes
 * to a binary stream.
 * <p>
 * For all the methods in this interface that write bytes, it is generally true
 * that if a byte cannot be written for any reason, an <code>IOException</code>
 * is thrown.
 * 
 * @author Jeff Tang
 * @version 1.0
 */
// XXX ÖØÐ´×¢ÊÍ
interface DataSerializer {
    /**
     * Writes a <code>boolean</code> value to the underlying binary stream. If
     * the argument <code>v</code> is <code>true</code>, the value
     * <code>(byte)1</code> is written; if <code>v</code> is <code>false</code>,
     * the value <code>(byte)0</code> is written. The byte written by this
     * method may be read by the <code>readBoolean</code> method of interface
     * <code>DataDeserializer</code>, which will then return a
     * <code>boolean</code> equal to <code>v</code>.
     * 
     * @param v
     *            the boolean to be written.
     * @exception IOException
     *                if an I/O error occurs.
     */
    void writeBoolean(boolean v) throws IOException;

    /**
     * Writes a <code>byte</code> value to the underlying binary stream. The
     * byte written by this method may be read by the <code>readByte</code>
     * method of interface <code>DataDeserializer</code>, which will then return
     * a <code>byte</code> equal to <code>v</code>.
     * 
     * @param v
     *            the byte value to be written.
     * @exception IOException
     *                if an I/O error occurs.
     */
    void writeByte(byte v) throws IOException;

    /**
     * Writes a <code>char</code> value, which is comprised of two bytes, to the
     * underlying binary stream. The byte values to be written, in the order
     * that depends on the implementation. The bytes written by this method may
     * be read by the <code>readChar</code> method of interface
     * <code>DataDeserializer</code> , which will then return a
     * <code>char</code> equal to <code>(char)v</code>.
     * 
     * @param v
     *            the <code>char</code> value to be written.
     * @exception IOException
     *                if an I/O error occurs.
     */
    void writeChar(char v) throws IOException;

    /**
     * Writes a <code>double</code> value, which is comprised of eight bytes, to
     * the underlying binary stream. The bytes written by this method may be
     * read by the <code>readDouble</code> method of interface
     * <code>DataDeserializer</code>, which will then return a
     * <code>double</code> equal to <code>v</code>.
     * 
     * @param v
     *            the <code>double</code> value to be written.
     * @exception IOException
     *                if an I/O error occurs.
     */
    void writeDouble(double v) throws IOException;

    /**
     * Writes a <code>float</code> value, which is comprised of four bytes, to
     * the underlying binary stream. The bytes written by this method may be
     * read by the <code>readFloat</code> method of interface
     * <code>DataDeserializer</code>, which will then return a
     * <code>float</code> equal to <code>v</code>.
     * 
     * @param v
     *            the <code>float</code> value to be written.
     * @exception IOException
     *                if an I/O error occurs.
     */
    void writeFloat(float v) throws IOException;

    /**
     * Writes an <code>int</code> value, which is comprised of four bytes, to
     * the underlying binary stream. The byte values to be written, in the order
     * that depends on the implementation. The bytes written by this method may
     * be read by the <code>readInt</code> method of interface
     * <code>DataDeserializer</code> , which will then return an
     * <code>int</code> equal to <code>v</code>.
     * 
     * @param v
     *            the <code>int</code> value to be written.
     * @exception IOException
     *                if an I/O error occurs.
     */
    void writeInt(int v) throws IOException;

    /**
     * Writes a <code>long</code> value, which is comprised of eight bytes, to
     * the underlying binary stream. The byte values to be written, in the order
     * that depends on the implementation. The bytes written by this method may
     * be read by the <code>readLong</code> method of interface
     * <code>DataDeserializer</code> , which will then return a
     * <code>long</code> equal to <code>v</code>.
     * 
     * @param v
     *            the <code>long</code> value to be written.
     * @exception IOException
     *                if an I/O error occurs.
     */
    void writeLong(long v) throws IOException;

    /**
     * Writes two bytes to the underlying binary stream to represent the value
     * of the argument. The byte values to be written, in the order that depends
     * on the implementation. The bytes written by this method may be read by
     * the <code>readShort</code> method of interface
     * <code>DataDeserializer</code> , which will then return a
     * <code>short</code> equal to <code>(short)v</code>.
     * 
     * @param v
     *            the <code>short</code> value to be written.
     * @exception IOException
     *                if an I/O error occurs.
     */
    void writeShort(short v) throws IOException;

    /**
     * Writes to the underlying binary stream all the bytes in array
     * <code>b</code>. If <code>b</code> is <code>null</code>, a
     * <code>NullPointerException</code> is thrown. If <code>b.length</code> is
     * zero, then no bytes are written. Otherwise, the byte <code>b[0]</code> is
     * written first, then <code>b[1]</code>, and so on; the last byte written
     * is <code>b[b.length-1]</code>.
     * 
     * @param b
     *            the data.
     * @exception IOException
     *                if an I/O error occurs.
     */
    void write(byte[] b) throws IOException;

    /**
     * Writes <code>len</code> bytes from array <code>b</code>, in order, to the
     * underlying binary stream. If <code>b</code> is <code>null</code>, a
     * <code>NullPointerException</code> is thrown. If <code>off</code> is
     * negative, or <code>len</code> is negative, or <code>off+len</code> is
     * greater than the length of the array <code>b</code>, then an
     * <code>IndexOutOfBoundsException</code> is thrown. If <code>len</code> is
     * zero, then no bytes are written. Otherwise, the byte <code>b[off]</code>
     * is written first, then <code>b[off+1]</code>, and so on; the last byte
     * written is <code>b[off+len-1]</code>.
     * 
     * @param b
     *            the data.
     * @param off
     *            the start offset in the data.
     * @param len
     *            the number of bytes to write.
     * @exception IOException
     *                if an I/O error occurs.
     */
    void write(byte[] b, int off, int len) throws IOException;

    /**
     * Flushes this stream by writing any buffered output to the underlying
     * stream.
     * 
     * @throws IOException
     *             If an I/O error occurs
     */
    void flush() throws IOException;

    /**
     * Closes this stream and releases any system resources associated with it.
     * If the stream is already closed then invoking this method has no effect.
     * 
     * @throws IOException
     *             if an I/O error occurs
     */
    void close() throws IOException;
}
