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
 * 长整数数组类型。
 * 
 * @author Jeff Tang
 * @version 1.0
 */
public final class LongArrayDataType extends ArrayDataTypeBase {

	public static final LongArrayDataType TYPE = new LongArrayDataType();

	private LongArrayDataType() {
		super(long[].class, LongType.TYPE);
	}

	public void digestType(Digester digester) {
		digester.update(TypeCodeSet.LONGS);
	}

	static {
		DataTypeUndigester.regUndigester(new DataTypeUndigester(
				TypeCodeSet.LONGS) {
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
			long[] la = (long[]) obj;
			serializer.writeInt(la.length);
			for (int i = 0, len = la.length; i < len; i++) {
				serializer.writeLong(la[i]);
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
		long[] la = new long[len];
		for (int i = 0; i < len; i++) {
			la[i] = deserializer.readLong();
		}
		return la;
	}

	// //////////////////////////////////////////
	// / NEW IO Serialization
	// //////////////////////////////////////////

	@Override
	public final boolean nioSerializeData(final NSerializer serializer,
			final Object object) {
		return serializer.writeLongArrayData((long[]) object);
	}

}
