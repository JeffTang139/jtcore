package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.impl.NSerializer.NSerializerFactory;

/**
 * 序列化器工厂供应商
 * 
 * @author Jeff Tang
 * 
 */
public interface NSerializerFactoryProvider {
	/**
	 * 获得序列化器工厂
	 */
	public NSerializerFactory getNSerializerFactory();
}
