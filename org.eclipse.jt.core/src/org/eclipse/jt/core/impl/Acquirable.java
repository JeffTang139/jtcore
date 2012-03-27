package org.eclipse.jt.core.impl;

/**
 * 可被请求的（读写锁）对象的基类
 * 
 * @author Jeff Tang
 * 
 */
class Acquirable {
	/**
	 * 请求者列队
	 */
	@SuppressWarnings("unchecked")
	volatile Acquirer acquirer;
}
