package org.eclipse.jt.core.impl;

/**
 * �ɶ�λ����ӿ�
 * 
 * @author Jeff Tang
 * 
 */
public interface TextLocalizable {
	/**
	 * ��ȡ��ʼ�к�
	 * 
	 * @return
	 */
	public abstract int startLine();

	/**
	 * ��ȡ��ʼ�к�
	 * 
	 * @return
	 */
	public abstract int startCol();

	/**
	 * ��ȡ�����к�
	 * 
	 * @return
	 */
	public abstract int endLine();

	/**
	 * ��ȡ�����к�
	 * 
	 * @return
	 */
	public abstract int endCol();
}
