package org.eclipse.jt.core.da;

/**
 * ��¼״̬
 * 
 * @author Jeff Tang
 * 
 */
public enum RecordState {
	/**
	 * �¼�¼
	 */
	NEW,
	/**
	 * �¼�¼���޸Ĺ�
	 */
	NEW_MODIFIED,
	/**
	 * �����ݿ�װ�أ���û���޸ģ�
	 */
	IN_DB,
	/**
	 * �����ݿ�װ�أ������Ѿ��Ķ������Ӷ���д�������ݣ���һ����Ҫ�ǲ�ͬ�����ݣ�
	 */
	IN_DB_MODIFING,
	/**
	 * �����ݿ�װ�أ������Ѿ��ڼ�¼����ɾ��
	 */
	IN_DB_DELETING,
}
