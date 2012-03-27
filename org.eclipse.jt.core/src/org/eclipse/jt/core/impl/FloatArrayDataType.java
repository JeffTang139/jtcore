/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File FloatArrayDataType.java
 * Date Apr 28, 2009
 */
package org.eclipse.jt.core.impl;

import java.io.IOException;

import org.eclipse.jt.core.type.DataType;
import org.eclipse.jt.core.type.Digester;
import org.eclipse.jt.core.type.Undigester;


/**
 * 单精度浮点数数组类型。
 * 
 * @author Jeff Tang
 * @version 1.0
 */
public final class FloatArrayDataType extends ArrayDataTypeBase {

	public static final FloatArrayDataType TYPE = new FloatArrayDataType();

	private FloatArrayDataType() {
		super(float[].class, FloatType.TYPE);
	}

	public void digestType(Digester digester) {
		digester.update(TypeCodeSet.FLOATS);
	}

	static {
		DataTypeUndigester.regUndigester(new DataTypeUndigester(
				TypeCodeSet.FLOATS) {
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
			float[] fa = (float[]) obj;
			serializer.writeInt(fa.length);
			for (int i = 0, len = fa.length; i < len; i++) {
				serializer.writeFloat(fa[i]);
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
		float[] fa = new float[len];
		for (int i = 0; i < len; i++) {
			fa[i] = deserializer.readFloat();
		}
		return fa;
	}

	// //////////////////////////////////////////
	// / NEW IO Serialization
	// //////////////////////////////////////////
	@Override
	public final boolean nioSerializeData(final NSerializer serializer,
			final Object object) {
		return serializer.writeFloatArrayData((float[]) object);
	}

}
