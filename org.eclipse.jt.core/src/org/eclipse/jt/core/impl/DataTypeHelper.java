/**
 * Copyright (C) 2007-2008 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File DataTypeHelper.java
 * Date 2008-12-4
 */
package org.eclipse.jt.core.impl;

import java.io.IOException;

import org.eclipse.jt.core.type.DataType;
import org.eclipse.jt.core.type.Type;
import org.eclipse.jt.core.type.Undigester;


/**
 * 
 * @author Jeff Tang
 * @version 1.0
 */
// TODO 注释
final class DataTypeHelper {
    static DataType readDataType(Undigester undigester) throws IOException,
            StructDefineNotFoundException {
        return DataTypeUndigester.undigestType(undigester);
    }

    static void skipData(InternalDeserializer sod, DataType type) {
        // TODO 当双方结构定义对等时，该方法不会被调用。
        // 但若以后支持双方结构定义存在差异，应该实现这一方法。
        throw new UnsupportedOperationException();
    }

    static Type undigestType(Undigester undigester) throws IOException,
            StructDefineNotFoundException {
        // XXX 目前这个方法仅支持DataType的读取，对Type支持尚不完整。
        return DataTypeUndigester.undigestType(undigester);
    }
}
