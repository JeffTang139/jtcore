/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File LongArrayDataType.java
 * Date Apr 28, 2009
 */
package org.eclipse.jt.core.impl;

import java.io.IOException;

import org.eclipse.jt.core.type.DataType;
import org.eclipse.jt.core.type.Digester;
import org.eclipse.jt.core.type.GUID;
import org.eclipse.jt.core.type.Undigester;


/**
 * GUID数组类型。
 * 
 * @author Jeff Tang
 * @version 1.0
 */
public final class GUIDArrayDataType extends ObjectArrayDataType {

	public static final GUIDArrayDataType TYPE = new GUIDArrayDataType();

	private GUIDArrayDataType() {
		super(GUID[].class, GUIDType.TYPE);
	}

	@Override
	public void digestType(Digester digester) {
		digester.update(TypeCodeSet.GUIDS);
	}

	static {
		DataTypeUndigester.regUndigester(new DataTypeUndigester(
				TypeCodeSet.GUIDS) {
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
			GUID[] la = (GUID[]) obj;
			serializer.writeInt(la.length);
			for (GUID guid : la) {
				serializer.writeLong(guid.getMostSigBits());
				serializer.writeLong(guid.getLeastSigBits());
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
		GUID[] la = new GUID[len];
		for (int i = 0; i < len; i++) {
			la[i] = GUID.valueOf(deserializer.readLong(), deserializer
					.readLong());
		}
		return la;
	}
}
