package org.eclipse.jt.core;

/**
 * 有生命周期的句柄
 * 
 * @author Jeff Tang
 * 
 */
public interface LifeHandle {
	/**
	 * 返回是否有效
	 */
	public boolean isValid();

	/**
	 * 检查是否有效
	 */
	public void checkValid();
}
