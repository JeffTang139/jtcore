package org.eclipse.jt.core.spi.log;

/**
 * ��־�����
 * 
 * @author Jeff Tang
 * 
 */
public enum LogEntryKind {
	/**
	 * ��ʾ<br>
	 */
	HINT,
	/**
	 * ����<br>
	 */
	WARNING,
	/**
	 * ����<br>
	 */
	ERROR,
	/**
	 * ���̿�ʼ<br>
	 */
	PROCESS_BEGIN,
	/**
	 * ���̳ɹ�����
	 */
	PROCESS_SUCCESS,
	/**
	 * ����ʧ�ܽ���
	 */
	PROCESS_FAIL
}
