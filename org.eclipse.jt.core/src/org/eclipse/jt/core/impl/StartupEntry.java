package org.eclipse.jt.core.impl;

/**
 * 启动项基类
 * 
 * @author Jeff Tang
 * 
 */
abstract class StartupEntry {
	/**
	 * 启动时使用，同一步中的下一个元素，环链表
	 */
	StartupEntry nextInStep;
}
