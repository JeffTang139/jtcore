package org.eclipse.jt.core.info;


/**
 * ���̴�����Ϣ
 * 
 * @author Jeff Tang
 * 
 */
public interface ProcessInfo extends Info {
	/**
	 * ��ȡ����ʱ��
	 */
	public long getDuration();

	/**
	 * �Ƿ��д���
	 */
	public boolean hasError();

	/**
	 * �����Ƿ����
	 */
	public boolean isFinished();
}
