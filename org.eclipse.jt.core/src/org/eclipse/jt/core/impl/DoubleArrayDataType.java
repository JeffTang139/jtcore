/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File DoubleArrayDataType.java
 * Date Apr 28, 2009
 */
package org.eclipse.jt.core.impl;

import java.io.IOException;

import org.eclipse.jt.core.type.DataType;
import org.eclipse.jt.core.type.Digester;
import org.eclipse.jt.core.type.Undigester;


/**
 * 双精度浮点数数组类型。
 * 
 * @author Jeff Tang
 * @version 1.0
 */
public final class DoubleArrayDataType extends ArrayDataTypeBase {

	public static final DoubleArrayDataType TYPE = new DoubleArrayDataType();

	private DoubleArrayDataType() {
		super(double[].class, DoubleType.TYPE);
	}

	public void digestType(Digester digester) {
		digester.update(TypeCodeSet.DOUBLES);
	}

	static {
		DataTypeUndigester.regUndigester(new DataTypeUndigester(
				TypeCodeSet.DOUBLES) {
			@Override
			protected DataType doUndigest(Undigester undigester) {
				return TYPE;
			}
		});
	}

	// //////////////////////////////////
	// Serialization

	@Override
	public void writeObjectData(InternalSerializer serializer, Object obj)
			throws IOException, StructDefineNotFoundException {
		if (obj == null) {
			serializer.writeInt(-1);
		} else {
			double[] da = (double[]) obj;
			serializer.writeInt(da.length);
			for (int i = 0, len = da.length; i < len; i++) {
				serializer.writeDouble(da[i]);
			}
		}
	}

	@Override
	public Object readObjectData(InternalDeserializer deserializer)
			throws IOException, StructDefineNotFoundException {
		int len = deserializer.readInt();
		if (len == -1) {
			return null;
		}
		double[] da = new double[len];
		for (int i = 0; i < len; i++) {
			da[i] = deserializer.readDouble();
		}
		return da;
	}

	// //////////////////////////////////////////
	// / NEW IO Serialization
	// //////////////////////////////////////////

	@Override
	public final boolean nioSerializeData(final NSerializer serializer,
			final Object object) {
		return serializer.writeDoubleArrayData((double[]) object);
	}

}
