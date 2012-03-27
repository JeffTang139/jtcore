package org.eclipse.jt.core.da.ext;

/**
 * ��
 * 
 * @author Jeff Tang
 * 
 */
public interface RPTRecordSetKey extends RPTRecordSetColumn {
	/**
	 * ������
	 */
	public String getName();

	/**
	 * ���Ĭ�ϵ�Լ������¼����Ĭ��Լ����Ӧ�ü��ļ�Լ�������Ը�Լ��������Ӱ��Ĭ�ϵ�����
	 */
	public RPTRecordSetKeyRestriction getDefaultKeyRestriction();

	/**
	 * ��Ӽ�Լ��<br>
	 * ��Ч�ڣ�this.getDefaultKeyRestriction().addMatchValue(Object keyValue);
	 */
	public int addMatchValue(Object keyValue);

	/**
	 * ���Լ��<br>
	 * ��Ч�ڣ�this.getDefaultKeyRestriction().clearMatchValues();
	 */
	public void clearMatchValues();
}
