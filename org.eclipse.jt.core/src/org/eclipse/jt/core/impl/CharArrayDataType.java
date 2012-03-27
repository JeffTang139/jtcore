/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File CharArrayDataType.java
 * Date Apr 28, 2009
 */
package org.eclipse.jt.core.impl;

import java.io.IOException;

import org.eclipse.jt.core.type.DataType;
import org.eclipse.jt.core.type.Digester;
import org.eclipse.jt.core.type.Undigester;


/**
 * 字符数组类型。
 * 
 * @author Jeff Tang
 * @version 1.0
 */
public final class CharArrayDataType extends ArrayDataTypeBase {

	public static final CharArrayDataType TYPE = new CharArrayDataType();

	private CharArrayDataType() {
		super(char[].class, CharacterType.TYPE);
	}

	public void digestType(Digester digester) {
		digester.update(TypeCodeSet.CHARS);
	}

	static {
		DataTypeUndigester.regUndigester(new DataTypeUndigester(
				TypeCodeSet.CHARS) {
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
			char[] ca = (char[]) obj;
			serializer.writeInt(ca.length);
			for (int i = 0, len = ca.length; i < len; i++) {
				serializer.writeChar(ca[i]);
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
		char[] ca = new char[len];
		for (int i = 0; i < len; i++) {
			ca[i] = deserializer.readChar();
		}
		return ca;
	}

	// //////////////////////////////////////////
	// / NEW IO Serialization
	// //////////////////////////////////////////

	@Override
	public final boolean nioSerializeData(final NSerializer serializer,
			final Object object) {
		return serializer.writeCharArrayData((char[]) object);
	}

}
