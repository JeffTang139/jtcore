package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.obja.StructClass;

/**
 * ���л�ö����Ϣ������֧�����ڵ����ڰ汾��ͬ��ö�����ͼ����������
 * 
 * @author Jeff Tang
 * 
 */
@StructClass
public class SerializationEnumInfo {
	public final String[] names;
	public final int[] values;

	SerializationEnumInfo(Class<? extends Enum<?>> enumClass) {
		final Enum<?>[] contants = enumClass.getEnumConstants();
		final int contantsCount = contants.length;
		this.names = new String[contantsCount];
		this.values = new int[contantsCount];
		for (int i = 0; i < contantsCount; i++) {
			Enum<?> e = contants[i];
			this.names[i] = e.name();
			this.values[i] = e.ordinal();
		}
	}
}
