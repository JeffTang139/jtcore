package org.eclipse.jt.core.impl;

/**
 * 模块调用器项，用于hash管理用
 * 
 * @author Jeff Tang
 * 
 */
@SuppressWarnings("unchecked")
final class ServiceInvokeeEntry {
	ServiceInvokeeEntry(Class<?> targetClass, int hash, ServiceInvokeeEntry next,
			ServiceInvokeeBase first) {
		this.targetClass = targetClass;
		this.hash = hash;
		this.next = next;
		this.first = first;
	}

	final void put(ServiceInvokeeBase invokee) {
		invokee.next = this.first;
		this.first = invokee;
	}

	/**
	 * 标的类
	 */
	final Class<?> targetClass;
	final int hash;
	ServiceInvokeeEntry next;
	ServiceInvokeeBase first;
}
