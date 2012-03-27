package org.eclipse.jt.core.impl;

/**
 * 待请求资源
 * 
 * @author Jeff Tang
 * 
 */
public abstract class NewAcquirable {
	/**
	 * ID标识，用以在多个节点间确定资源
	 */
	long id;
	/**
	 * 请求者列队的队尾
	 */
	@SuppressWarnings("unchecked")
	NewAcquirer tail;
}
