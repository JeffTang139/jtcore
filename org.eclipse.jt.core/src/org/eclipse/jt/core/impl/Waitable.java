package org.eclipse.jt.core.impl;

/**
 * �ɵȴ��ӿ�<br>
 * ����ͳһContext.WaitFor(...)��ʵ��
 * 
 * @author Jeff Tang
 * 
 */
interface Waitable {
	/**
	 * �ȴ�ֱ��������ʱ
	 * 
	 * @param timeout
	 *            ��ʱ��������0��ʾ����ʱ��
	 */
	public void waitStop(long timeout) throws InterruptedException;
}
