package org.eclipse.jt.core.impl;

import java.io.IOException;

import org.eclipse.jt.core.type.DataType;
import org.eclipse.jt.core.type.Undigester;


abstract class DataTypeUndigester {
    public final byte typeCode;

    DataTypeUndigester(byte typeCode) {
        this.typeCode = typeCode;
    }

    protected abstract DataType doUndigest(Undigester undigester)
            throws IOException, StructDefineNotFoundException;

    private static DataTypeUndigester[] dataTypeUndigesters = new DataTypeUndigester[256];

    static void regUndigester(DataTypeUndigester undigester) {
        dataTypeUndigesters[undigester.typeCode - Byte.MIN_VALUE] = undigester;
    }

    private static DataTypeUndigester getDataTypeUndigester(byte typeCode) {
        DataTypeUndigester dataTypeUndigester = dataTypeUndigesters[typeCode
                - Byte.MIN_VALUE];
        if (dataTypeUndigester == null) {
            throw new UnsupportedOperationException("不支持" + typeCode + "类型代码");
        }
        return dataTypeUndigester;
    }

    static DataType undigestType(Undigester undigester) throws IOException,
            StructDefineNotFoundException {
        return getDataTypeUndigester(undigester.extractByte()).doUndigest(
                undigester);
    }
}
