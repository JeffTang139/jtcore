/**
 * Copyright (C) 2007-2008 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File Endianness.java
 * Date 2008-11-28
 */
package org.eclipse.jt.core.impl;

import java.nio.ByteOrder;

import org.eclipse.jt.core.exception.NullArgumentException;

import sun.misc.Unsafe;


/**
 * 字节序。
 * 
 * 用于处理基本数据类型与字节序列之间的转换问题。<br/>
 * 当使用一个特定的字节序对象时，基本数据类型各自所对应的字节的排列顺序就是确定的。
 * 
 * @author Jeff Tang
 * @version 1.0
 */
// TODO 注释
abstract class Endianness {
    Endianness() {
    }

    /**
     * 本机的字节序。
     */
    static final Endianness LOCAL_ENDIAN;

    private static final Unsafe UNSAFE = Unsf.unsafe;
    private static final long BYTES_BASE_OFFSET = UNSAFE
            .arrayBaseOffset(byte[].class);

    /**
     * 字节序对象为每一种字节序分配了一个代码（<code>code</code>），本方法提供由代码解析出相应的字节顺序对象。
     * 
     * @param code
     *            字节序的代码。
     * @return 字节顺序对象。
     * @throws IllegalArgumentException
     *             提供的字节充代码无效。
     */
    static ByteOrder parseByteOrder(final int code) {
        return parseEndianness(code).byteOrder();
    }

    /**
     * 字节序对象为每一种字节序分配了一个代码（<code>code</code>），本方法提供由代码解析出相应的字节序对象。
     * 
     * @param code
     *            字节序的代码。
     * @return 字节序对象。
     * @throws IllegalArgumentException
     *             提供的字节充代码无效。
     */
    static Endianness parseEndianness(final int code) {
        if (code == Endianness.LITTLE_ENDIAN.code()) {
            return Endianness.LITTLE_ENDIAN.tryUnsafe();
        } else if (code == Endianness.BIG_ENDIAN.code()) {
            return Endianness.BIG_ENDIAN.tryUnsafe();
        } else {
            throw new IllegalArgumentException("invalid code: " + code);
        }
    }

    static Endianness valueOf(final ByteOrder byteOrder) {
        if (byteOrder == null) {
            throw new NullArgumentException("byteOrder");
        }
        Endianness ed = byteOrder == ByteOrder.BIG_ENDIAN ? Endianness.BIG_ENDIAN
                : Endianness.LITTLE_ENDIAN;
        return ed.tryUnsafe();
    }

    /*
     * Methods for unpacking primitive values from byte arrays starting at given
     * offsets.
     */

    abstract boolean getBoolean(byte[] b, int off);

    abstract char getChar(byte[] b, int off);

    abstract short getShort(byte[] b, int off);

    abstract int getInt(byte[] b, int off);

    abstract float getFloat(byte[] b, int off);

    abstract long getLong(byte[] b, int off);

    abstract double getDouble(byte[] b, int off);

    /*
     * Methods for packing primitive values into byte arrays starting at given
     * offsets.
     */

    abstract void putBoolean(byte[] b, int off, boolean val);

    abstract void putChar(byte[] b, int off, char val);

    abstract void putShort(byte[] b, int off, short val);

    abstract void putInt(byte[] b, int off, int val);

    abstract void putFloat(byte[] b, int off, float val);

    abstract void putLong(byte[] b, int off, long val);

    abstract void putDouble(byte[] b, int off, double val);

    /*
     * 
     */

    boolean isNative() {
        return this == LOCAL_ENDIAN;
    }

    Endianness tryUnsafe() {
        return this.isNative() ? UNSAFE_ENDIAN : this;
    }

    abstract ByteOrder byteOrder();

    abstract byte code();

    // //////////////////////////////////////////////////////////////////////////

    /**
     * Utility methods for packing/unpacking primitive values in/out of byte
     * arrays using big-endian byte ordering.
     */
    static final Endianness BIG_ENDIAN = new Endianness() {
        @Override
        ByteOrder byteOrder() {
            return ByteOrder.BIG_ENDIAN;
        }

        @Override
        final byte code() {
            return 1;
        }

        @Override
        public boolean getBoolean(byte[] b, int off) {
            return b[off] != 0;
        }

        @Override
        public char getChar(byte[] b, int off) {
            return (char) (((b[off + 1] & 0xFF) << 0) + ((b[off + 0] & 0xFF) << 8));
        }

        @Override
        public double getDouble(byte[] b, int off) {
            long j = ((b[off + 7] & 0xFFL) << 0) + ((b[off + 6] & 0xFFL) << 8)
                    + ((b[off + 5] & 0xFFL) << 16)
                    + ((b[off + 4] & 0xFFL) << 24)
                    + ((b[off + 3] & 0xFFL) << 32)
                    + ((b[off + 2] & 0xFFL) << 40)
                    + ((b[off + 1] & 0xFFL) << 48)
                    + ((b[off + 0] & 0xFFL) << 56);
            return Double.longBitsToDouble(j);
        }

        @Override
        public float getFloat(byte[] b, int off) {
            int i = ((b[off + 3] & 0xFF) << 0) + ((b[off + 2] & 0xFF) << 8)
                    + ((b[off + 1] & 0xFF) << 16) + ((b[off + 0] & 0xFF) << 24);
            return Float.intBitsToFloat(i);
        }

        @Override
        public int getInt(byte[] b, int off) {
            return ((b[off + 3] & 0xFF) << 0) + ((b[off + 2] & 0xFF) << 8)
                    + ((b[off + 1] & 0xFF) << 16) + ((b[off + 0] & 0xFF) << 24);
        }

        @Override
        public long getLong(byte[] b, int off) {
            return ((b[off + 7] & 0xFFL) << 0) + ((b[off + 6] & 0xFFL) << 8)
                    + ((b[off + 5] & 0xFFL) << 16)
                    + ((b[off + 4] & 0xFFL) << 24)
                    + ((b[off + 3] & 0xFFL) << 32)
                    + ((b[off + 2] & 0xFFL) << 40)
                    + ((b[off + 1] & 0xFFL) << 48)
                    + ((b[off + 0] & 0xFFL) << 56);
        }

        @Override
        public short getShort(byte[] b, int off) {
            return (short) (((b[off + 1] & 0xFF) << 0) + ((b[off + 0] & 0xFF) << 8));
        }

        @Override
        public void putBoolean(byte[] b, int off, boolean val) {
            b[off] = (byte) (val ? 1 : 0);
        }

        @Override
        public void putChar(byte[] b, int off, char val) {
            b[off + 1] = (byte) (val >>> 0);
            b[off + 0] = (byte) (val >>> 8);
        }

        @Override
        public void putDouble(byte[] b, int off, double val) {
            long j = Double.doubleToLongBits(val);
            b[off + 7] = (byte) (j >>> 0);
            b[off + 6] = (byte) (j >>> 8);
            b[off + 5] = (byte) (j >>> 16);
            b[off + 4] = (byte) (j >>> 24);
            b[off + 3] = (byte) (j >>> 32);
            b[off + 2] = (byte) (j >>> 40);
            b[off + 1] = (byte) (j >>> 48);
            b[off + 0] = (byte) (j >>> 56);
        }

        @Override
        public void putFloat(byte[] b, int off, float val) {
            int i = Float.floatToIntBits(val);
            b[off + 3] = (byte) (i >>> 0);
            b[off + 2] = (byte) (i >>> 8);
            b[off + 1] = (byte) (i >>> 16);
            b[off + 0] = (byte) (i >>> 24);
        }

        @Override
        public void putInt(byte[] b, int off, int val) {
            b[off + 3] = (byte) (val >>> 0);
            b[off + 2] = (byte) (val >>> 8);
            b[off + 1] = (byte) (val >>> 16);
            b[off + 0] = (byte) (val >>> 24);
        }

        @Override
        public void putLong(byte[] b, int off, long val) {
            b[off + 7] = (byte) (val >>> 0);
            b[off + 6] = (byte) (val >>> 8);
            b[off + 5] = (byte) (val >>> 16);
            b[off + 4] = (byte) (val >>> 24);
            b[off + 3] = (byte) (val >>> 32);
            b[off + 2] = (byte) (val >>> 40);
            b[off + 1] = (byte) (val >>> 48);
            b[off + 0] = (byte) (val >>> 56);
        }

        @Override
        public void putShort(byte[] b, int off, short val) {
            b[off + 1] = (byte) (val >>> 0);
            b[off + 0] = (byte) (val >>> 8);
        }
    };

    /**
     * Utility methods for packing/unpacking primitive values in/out of byte
     * arrays using little-endian byte ordering.
     */
    static final Endianness LITTLE_ENDIAN = new Endianness() {
        @Override
        ByteOrder byteOrder() {
            return ByteOrder.LITTLE_ENDIAN;
        }

        @Override
        final byte code() {
            return 0;
        }

        @Override
        public boolean getBoolean(byte[] b, int off) {
            return b[off] != 0;
        }

        @Override
        public char getChar(byte[] b, int off) {
            return (char) (((b[off + 0] & 0xFF) << 0) + ((b[off + 1] & 0xFF) << 8));
        }

        @Override
        public double getDouble(byte[] b, int off) {
            long j = ((b[off + 0] & 0xFFL) << 0) + ((b[off + 1] & 0xFFL) << 8)
                    + ((b[off + 2] & 0xFFL) << 16)
                    + ((b[off + 3] & 0xFFL) << 24)
                    + ((b[off + 4] & 0xFFL) << 32)
                    + ((b[off + 5] & 0xFFL) << 40)
                    + ((b[off + 6] & 0xFFL) << 48)
                    + ((b[off + 7] & 0xFFL) << 56);
            return Double.longBitsToDouble(j);
        }

        @Override
        public float getFloat(byte[] b, int off) {
            int i = ((b[off + 0] & 0xFF) << 0) + ((b[off + 1] & 0xFF) << 8)
                    + ((b[off + 2] & 0xFF) << 16) + ((b[off + 3] & 0xFF) << 24);
            return Float.intBitsToFloat(i);
        }

        @Override
        public int getInt(byte[] b, int off) {
            return ((b[off + 0] & 0xFF) << 0) + ((b[off + 1] & 0xFF) << 8)
                    + ((b[off + 2] & 0xFF) << 16) + ((b[off + 3] & 0xFF) << 24);
        }

        @Override
        public long getLong(byte[] b, int off) {
            return ((b[off + 0] & 0xFFL) << 0) + ((b[off + 1] & 0xFFL) << 8)
                    + ((b[off + 2] & 0xFFL) << 16)
                    + ((b[off + 3] & 0xFFL) << 24)
                    + ((b[off + 4] & 0xFFL) << 32)
                    + ((b[off + 5] & 0xFFL) << 40)
                    + ((b[off + 6] & 0xFFL) << 48)
                    + ((b[off + 7] & 0xFFL) << 56);
        }

        @Override
        public short getShort(byte[] b, int off) {
            return (short) (((b[off + 0] & 0xFF) << 0) + ((b[off + 1] & 0xFF) << 8));
        }

        @Override
        public void putBoolean(byte[] b, int off, boolean val) {
            b[off] = (byte) (val ? 1 : 0);
        }

        @Override
        public void putChar(byte[] b, int off, char val) {
            b[off + 0] = (byte) (val >>> 0);
            b[off + 1] = (byte) (val >>> 8);
        }

        @Override
        public void putDouble(byte[] b, int off, double val) {
            long j = Double.doubleToLongBits(val);
            b[off + 0] = (byte) (j >>> 0);
            b[off + 1] = (byte) (j >>> 8);
            b[off + 2] = (byte) (j >>> 16);
            b[off + 3] = (byte) (j >>> 24);
            b[off + 4] = (byte) (j >>> 32);
            b[off + 5] = (byte) (j >>> 40);
            b[off + 6] = (byte) (j >>> 48);
            b[off + 7] = (byte) (j >>> 56);
        }

        @Override
        public void putFloat(byte[] b, int off, float val) {
            int i = Float.floatToIntBits(val);
            b[off + 0] = (byte) (i >>> 0);
            b[off + 1] = (byte) (i >>> 8);
            b[off + 2] = (byte) (i >>> 16);
            b[off + 3] = (byte) (i >>> 24);
        }

        @Override
        public void putInt(byte[] b, int off, int val) {
            b[off + 0] = (byte) (val >>> 0);
            b[off + 1] = (byte) (val >>> 8);
            b[off + 2] = (byte) (val >>> 16);
            b[off + 3] = (byte) (val >>> 24);
        }

        @Override
        public void putLong(byte[] b, int off, long val) {
            b[off + 0] = (byte) (val >>> 0);
            b[off + 1] = (byte) (val >>> 8);
            b[off + 2] = (byte) (val >>> 16);
            b[off + 3] = (byte) (val >>> 24);
            b[off + 4] = (byte) (val >>> 32);
            b[off + 5] = (byte) (val >>> 40);
            b[off + 6] = (byte) (val >>> 48);
            b[off + 7] = (byte) (val >>> 56);
        }

        @Override
        public void putShort(byte[] b, int off, short val) {
            b[off + 0] = (byte) (val >>> 0);
            b[off + 1] = (byte) (val >>> 8);
        }
    };

    /**
     * Utility methods for packing/unpacking primitive values in/out of byte
     * arrays using local machine byte ordering.
     */
    static final Endianness UNSAFE_ENDIAN = new Endianness() {
        @Override
        ByteOrder byteOrder() {
            return LOCAL_ENDIAN.byteOrder();
        }

        @Override
        final byte code() {
            return LOCAL_ENDIAN.code();
        }

        @Override
        boolean isNative() {
            return true;
        }

        @Override
        Endianness tryUnsafe() {
            return this;
        }

        @Override
        public boolean getBoolean(byte[] b, int off) {
            return b[off] != 0;
        }

        @Override
        public char getChar(byte[] b, int off) {
            return UNSAFE.getChar(b, BYTES_BASE_OFFSET + off);
        }

        @Override
        public double getDouble(byte[] b, int off) {
            return UNSAFE.getDouble(b, BYTES_BASE_OFFSET + off);
        }

        @Override
        public float getFloat(byte[] b, int off) {
            return UNSAFE.getFloat(b, BYTES_BASE_OFFSET + off);
        }

        @Override
        public int getInt(byte[] b, int off) {
            return UNSAFE.getInt(b, BYTES_BASE_OFFSET + off);
        }

        @Override
        public long getLong(byte[] b, int off) {
            return UNSAFE.getLong(b, BYTES_BASE_OFFSET + off);
        }

        @Override
        public short getShort(byte[] b, int off) {
            return UNSAFE.getShort(b, BYTES_BASE_OFFSET + off);
        }

        @Override
        public void putBoolean(byte[] b, int off, boolean val) {
            b[off] = (byte) (val ? 1 : 0);
        }

        @Override
        public void putChar(byte[] b, int off, char val) {
            UNSAFE.putChar(b, BYTES_BASE_OFFSET + off, val);
        }

        @Override
        public void putDouble(byte[] b, int off, double val) {
            UNSAFE.putDouble(b, BYTES_BASE_OFFSET + off, val);
        }

        @Override
        public void putFloat(byte[] b, int off, float val) {
            UNSAFE.putFloat(b, BYTES_BASE_OFFSET + off, val);
        }

        @Override
        public void putInt(byte[] b, int off, int val) {
            UNSAFE.putInt(b, BYTES_BASE_OFFSET + off, val);
        }

        @Override
        public void putLong(byte[] b, int off, long val) {
            UNSAFE.putLong(b, BYTES_BASE_OFFSET + off, val);
        }

        @Override
        public void putShort(byte[] b, int off, short val) {
            UNSAFE.putShort(b, BYTES_BASE_OFFSET + off, val);
        }
    };

    static {
        // init endianness
        final byte[] a = new byte[4];
        UNSAFE.putInt(a, (long) UNSAFE.arrayBaseOffset(byte[].class),
                0x08040201);
        LOCAL_ENDIAN = a[0] == 0x01 ? Endianness.LITTLE_ENDIAN
                : Endianness.BIG_ENDIAN;
    }
}
