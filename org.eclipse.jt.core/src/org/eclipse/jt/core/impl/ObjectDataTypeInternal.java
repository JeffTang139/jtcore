package org.eclipse.jt.core.impl;

import java.io.IOException;

import org.eclipse.jt.core.serial.DataObjectTranslator;
import org.eclipse.jt.core.type.ObjectDataType;


/**
 * ��ֵ��
 * 
 * @author Jeff Tang
 * 
 */
interface ObjectDataTypeInternal extends ObjectDataType, DataTypeInternal {
	public Object assignNoCheckSrcD(DynObj dynSrc, Object dest,
			OBJAContext objaContext);

	public Object assignNoCheckSrc(Object src, Object dest,
			OBJAContext objaContext);

	// ////////////////////////////////////////////////////////////
	// Serialization

	/*
	 * FIXME �������ֻ��Ȩ��֮�ƣ��д�����
	 */
	boolean supportSerialization();

	void writeObjectData(InternalSerializer serializer, Object obj)
			throws IOException, StructDefineNotFoundException,
			UnsupportedOperationException;

	Object readObjectData(InternalDeserializer deserializer)
			throws IOException, StructDefineNotFoundException,
			UnsupportedOperationException;

	// //////////////////////////////////////////
	// / NEW IO Serialization
	// //////////////////////////////////////////

	public boolean nioSerializeData(final NSerializer serializer,
			final Object object);

	public DataObjectTranslator<?, ?> getDataObjectTranslator();

	public DataObjectTranslator<?, ?> registerDataObjectTranslator(
			final DataObjectTranslator<?, ?> serializer);

}
