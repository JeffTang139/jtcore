package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.impl.NSerializer.NSerializerFactory;

/**
 * ���л���������Ӧ��
 * 
 * @author Jeff Tang
 * 
 */
public interface NSerializerFactoryProvider {
	/**
	 * ������л�������
	 */
	public NSerializerFactory getNSerializerFactory();
}
