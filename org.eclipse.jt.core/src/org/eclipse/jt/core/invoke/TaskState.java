package org.eclipse.jt.core.invoke;
/**
 * �����״̬
 * @author Jeff Tang
 *
 */
public enum TaskState {
	/**
	 * ׼����
	 */
	PREPARING,
	/**
	 * ׼�����
	 */
	PREPARED,
	/**
	 * ׼���쳣
	 */
	PREPARERROR,
	/**
	 * ������
	 */
	PROCESSING,
	/**
	 * �����쳣
	 */
	PROCESSERROR,
	/**
	 * �������
	 */
	PROCESSED
}
