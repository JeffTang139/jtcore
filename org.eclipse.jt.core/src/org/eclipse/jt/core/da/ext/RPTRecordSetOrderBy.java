package org.eclipse.jt.core.da.ext;

/**
 * ������
 * 
 * @author Jeff Tang
 * 
 */
public interface RPTRecordSetOrderBy {
	/**
	 * ��ö�Ӧ��
	 */
	public RPTRecordSetColumn getColumn();

	/**
	 * ����Ƿ���
	 */
	public boolean isDesc();

	/**
	 * ��ȡ��ֵ�Ƿ���Ϊ��Сֵ����
	 */
	public boolean isNullAsMIN();
}
