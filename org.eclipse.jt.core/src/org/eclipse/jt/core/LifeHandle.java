package org.eclipse.jt.core;

/**
 * ���������ڵľ��
 * 
 * @author Jeff Tang
 * 
 */
public interface LifeHandle {
	/**
	 * �����Ƿ���Ч
	 */
	public boolean isValid();

	/**
	 * ����Ƿ���Ч
	 */
	public void checkValid();
}
