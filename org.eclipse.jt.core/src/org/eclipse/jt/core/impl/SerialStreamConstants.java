/**
 * Copyright (C) 2007-2008 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File SerialStreamConstants.java
 * Date 2008-12-1
 */
package org.eclipse.jt.core.impl;

/**
 * 序列化流中的常量。
 * 
 * @author Jeff Tang
 * @version 1.0
 */
// TODO 注释
final class SerialStreamConstants {
    private SerialStreamConstants() {
    }

    /**
     * Magic number that is written to the stream header.
     */
    final static short STREAM_MAGIC = (short) 0xaced;

    /**
     * Version number that is written to the stream header.
     */
    final static short STREAM_VERSION = 5;

    /*
     * Each item in the stream is preceded by a tag
     */

    /**
     * Null object reference.
     */
    final static byte TC_NULL = (byte) 0x70;

    /**
     * Reference to an object already written into the stream.
     */
    final static byte TC_REFERENCE = (byte) 0x71;

    /**
     * new Struct Define.
     */
    final static byte TC_STRUCTDEF = (byte) 0x72;

    /**
     * new Struct Summary.
     */
    final static byte TC_STRUCTSUM = (byte) 0x73;

    /**
     * new Object.
     */
    final static byte TC_OBJECT = (byte) 0x74;

    /**
     * new String.
     */
    final static byte TC_STRING = (byte) 0x75;

    /**
     * End of optional block data blocks for an object.
     */
    final static byte TC_GUID = (byte) 0x76;

    /**
     * new Array.
     */
    final static byte TC_ARRAY = (byte) 0x77;

    /**
     * Reference to Class.
     */
    final static byte TC_CLASS = (byte) 0x78;

    // /**
    // * Block of optional data. Byte following tag indicates number of bytes in
    // * this block data.
    // */
    // final static byte TC_BLOCKDATA = (byte) 0x79;

    // /**
    // * Reset stream context. All handles written into stream are reset.
    // */
    // // final static byte TC_RESET = (byte) 0x7A;
    /**
     * Exception during write.
     */
    final static byte TC_EXCEPTION = (byte) 0x7B;

    /**
     * Long string.
     */
    final static byte TC_LONGSTRING = (byte) 0x7C;

    // /**
    // * long Block data. The long following the tag indicates the number of
    // bytes
    // * in this block data.
    // */
    // final static byte TC_BLOCKDATALONG = (byte) 0x7D;

    /**
     * new Enum constant.
     */
    final static byte TC_ENUM = (byte) 0x7E;

    /**
     * Special objects.
     */
    final static byte TC_SPECIAL = (byte) 0x7F;

    /**
     * First wire handle to be assigned.
     */
    final static int baseWireHandle = 0x7e0000;
}
