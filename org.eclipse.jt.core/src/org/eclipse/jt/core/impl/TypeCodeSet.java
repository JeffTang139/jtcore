/**
 * Copyright (C) 2007-2008 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File TypeCodeSet.java
 * Date 2008-11-27
 */
package org.eclipse.jt.core.impl;

/**
 * 类型代码集。
 * 
 * @author Jeff Tang
 * @version 1.0
 */
final class TypeCodeSet {

    public static final byte BYTE = 'B';
    public static final byte CHAR = 'C';
    public static final byte DOUBLE = 'D';
    public static final byte FLOAT = 'F';
    public static final byte INT = 'I';
    public static final byte LONG = 'J';
    public static final byte SHORT = 'S';
    public static final byte BOOLEAN = 'Z';

    public static final byte CLASS = 'A';

    public static final byte OBJECT = 'L'; // class or interface in Java.
    public static final byte STRING = 'N';
    public static final byte GUID = 'G';
    public static final byte DATE = 'T';

    public static final byte TEXT = 0x01;
    public static final byte NTEXT = 0x02;
    public static final byte BLOB = 0x03;
    public static final byte NUMERIC_H = 0x04;
    public static final byte VARCHAR_H = 0x05;
    public static final byte NVARCHAR_H = 0x06;
    public static final byte CHAR_H = 0x07;
    public static final byte NCHAR_H = 0x08;

    public static final byte BINARY_H = 0x09;
    public static final byte VARBINARY_H = 0x0A;

    public static final byte RECORDSET_H = 0x0B;
    public static final byte QUERY_H = 0x0C;
    public static final byte TABLE_H = 0x0D;
    public static final byte MODEL_H = 0x0E;
    public static final byte STRUCT_H = 0x0F;
    public static final byte STRUCTS_H = 0x11;

    public static final byte ENUM_H = 'E';
    public static final byte RESOURCE_H = 'R';

    // public static final byte ARRAY = '[';
    public static final byte LIST = '(';

    public static final byte BYTES = (byte) 0xA0;
    public static final byte INTS = (byte) 0xA1;
    public static final byte CHARS = (byte) 0xA2;
    public static final byte LONGS = (byte) 0xA3;
    public static final byte SHORTS = (byte) 0xA4;
    public static final byte FLOATS = (byte) 0xA5;
    public static final byte DOUBLES = (byte) 0xA6;
    public static final byte BOOLEANS = (byte) 0xA7;
    public static final byte STRINGS = (byte) 0xA8;
    public static final byte GUIDS = (byte) 0xA9;
    public static final byte OBJECTS = (byte) 0xAA;

    public static final byte UNKNOWN = 'U'; // unsupported type.
}
