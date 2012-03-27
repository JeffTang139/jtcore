/**
 * Copyright (C) 2007-2008 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File ResourceFieldAccessor.java
 * Date 2008-10-29
 */
package org.eclipse.jt.core.impl;

import java.lang.reflect.Field;

import sun.misc.Unsafe;

/**
 * 
 * @author Jeff Tang
 * @version 1.0
 */
@Deprecated
abstract class ResourceFieldReader {
    static final Unsafe unsafe = Unsf.unsafe;

    static final ResourceFieldReader getReader(Field field) {
        return getReader(field.getType());
    }

    abstract Object getValue(Object o, int offset);

    private static final ResourceFieldReader getReader(Class<?> fieldType) {
        if (fieldType.isPrimitive()) {
            if (fieldType == int.class) {
                return INT_READER;
            } else if (fieldType == long.class) {
                return LONG_READER;
            } else if (fieldType == short.class) {
                return SHORT_READER;
            } else if (fieldType == byte.class) {
                return BYTE_READER;
            } else if (fieldType == double.class) {
                return DOUBLE_READER;
            } else if (fieldType == float.class) {
                return FLOAT_READER;
            } else if (fieldType == boolean.class) {
                return BOOLEAN_READER;
            } else if (fieldType == char.class) {
                return CHAR_READER;
            } else {
                throw new UnsupportedOperationException();
            }
        } else {
            return OBJECT_READER;
        }
    }

    static final ResourceFieldReader OBJECT_READER = new ResourceFieldReader() {
        @Override
        Object getValue(Object o, int offset) {
            return unsafe.getObject(o, (long) offset);
        }
    };

    static final ResourceFieldReader INT_READER = new ResourceFieldReader() {
        @Override
        Object getValue(Object o, int offset) {
            return unsafe.getInt(o, (long) offset);
        }
    };

    static final ResourceFieldReader LONG_READER = new ResourceFieldReader() {
        @Override
        Object getValue(Object o, int offset) {
            return unsafe.getLong(o, (long) offset);
        }
    };

    static final ResourceFieldReader SHORT_READER = new ResourceFieldReader() {
        @Override
        Object getValue(Object o, int offset) {
            return unsafe.getShort(o, (long) offset);
        }
    };

    static final ResourceFieldReader BYTE_READER = new ResourceFieldReader() {
        @Override
        Object getValue(Object o, int offset) {
            return unsafe.getByte(o, (long) offset);
        }
    };

    static final ResourceFieldReader DOUBLE_READER = new ResourceFieldReader() {
        @Override
        Object getValue(Object o, int offset) {
            return unsafe.getDouble(o, (long) offset);
        }
    };

    static final ResourceFieldReader FLOAT_READER = new ResourceFieldReader() {
        @Override
        Object getValue(Object o, int offset) {
            return unsafe.getFloat(o, (long) offset);
        }
    };

    static final ResourceFieldReader CHAR_READER = new ResourceFieldReader() {
        @Override
        Object getValue(Object o, int offset) {
            return unsafe.getChar(o, (long) offset);
        }
    };

    static final ResourceFieldReader BOOLEAN_READER = new ResourceFieldReader() {
        @Override
        Object getValue(Object o, int offset) {
            return unsafe.getBoolean(o, (long) offset);
        }
    };
}
