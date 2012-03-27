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
// TODO ע��
final class DataTypeHelper {
    static DataType readDataType(Undigester undigester) throws IOException,
            StructDefineNotFoundException {
        return DataTypeUndigester.undigestType(undigester);
    }

    static void skipData(InternalDeserializer sod, DataType type) {
        // TODO ��˫���ṹ����Ե�ʱ���÷������ᱻ���á�
        // �����Ժ�֧��˫���ṹ������ڲ��죬Ӧ��ʵ����һ������
        throw new UnsupportedOperationException();
    }

    static Type undigestType(Undigester undigester) throws IOException,
            StructDefineNotFoundException {
        // XXX Ŀǰ���������֧��DataType�Ķ�ȡ����Type֧���в�������
        return DataTypeUndigester.undigestType(undigester);
    }
}
