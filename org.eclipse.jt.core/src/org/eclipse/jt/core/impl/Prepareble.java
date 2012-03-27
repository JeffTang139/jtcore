package org.eclipse.jt.core.impl;

/**
 * 需要准备的接口
 * 
 * @author Jeff Tang
 * 
 */
interface Prepareble {
	/**
	 * 如果数据库没有准备好就不准备
	 */
	public boolean ignorePrepareIfDBInvalid();

	/**
	 * 获得是否已经准备好了
	 */
	public boolean isPrepared();

	/**
	 * 确认准备
	 * 
	 * @param context
	 *            上下文
	 * @param rePrepared
	 *            是否重新准备
	 */
	public void ensurePrepared(ContextImpl<?, ?, ?> context, boolean rePrepared);
}
