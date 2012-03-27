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
import org.eclipse.jt.core.type.Undigester;


/**
 * 字符串数组类型。
 * 
 * @author Jeff Tang
 * @version 1.0
 */
public final class StringArrayDataType extends ObjectArrayDataType {

	public static final StringArrayDataType TYPE = new StringArrayDataType();

	private StringArrayDataType() {
		super(String[].class, StringType.TYPE);
	}

	@Override
	public void digestType(Digester digester) {
		digester.update(TypeCodeSet.STRINGS);
	}

	static {
		DataTypeUndigester.regUndigester(new DataTypeUndigester(
				TypeCodeSet.STRINGS) {
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
			String[] la = (String[]) obj;
			serializer.writeInt(la.length);
			for (int i = 0, len = la.length; i < len; i++) {
				serializer.writeString(la[i]);
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
		String[] la = new String[len];
		for (int i = 0; i < len; i++) {
			la[i] = deserializer.readString();
		}
		return la;
	}
}
