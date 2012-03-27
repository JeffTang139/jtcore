/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File BooleanArrayDataType.java
 * Date Apr 28, 2009
 */
package org.eclipse.jt.core.impl;

import java.io.IOException;

import org.eclipse.jt.core.type.DataType;
import org.eclipse.jt.core.type.Digester;
import org.eclipse.jt.core.type.Undigester;


/**
 * 布尔数组类型。
 * 
 * @author Jeff Tang
 * @version 1.0
 */
public final class BooleanArrayDataType extends ArrayDataTypeBase {

	public static final BooleanArrayDataType TYPE = new BooleanArrayDataType();

	private BooleanArrayDataType() {
		super(boolean[].class, BooleanType.TYPE);
	}

	public void digestType(Digester digester) {
		digester.update(TypeCodeSet.BOOLEANS);
	}

	static {
		DataTypeUndigester.regUndigester(new DataTypeUndigester(
				TypeCodeSet.BOOLEANS) {
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
			boolean[] ba = (boolean[]) obj;
			serializer.writeInt(ba.length);
			for (int i = 0, len = ba.length; i < len; i++) {
				serializer.writeBoolean(ba[i]);
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
		boolean[] ba = new boolean[len];
		for (int i = 0; i < len; i++) {
			ba[i] = deserializer.readBoolean();
		}
		return ba;
	}

	// //////////////////////////////////////////
	// / NEW IO Serialization
	// //////////////////////////////////////////

	@Override
	public final boolean nioSerializeData(final NSerializer serializer,
			final Object object) {
		return serializer.writeBooleanArrayData((boolean[]) object);
	}

}
