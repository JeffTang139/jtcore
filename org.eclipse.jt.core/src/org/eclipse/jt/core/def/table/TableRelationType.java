package org.eclipse.jt.core.def.table;

/**
 * ��ϵ
 * 
 * @author Jeff Tang
 * 
 */
public enum TableRelationType {

	/**
	 * �ο���ǰ�߲ο����ߣ����߲ο�ǰ�ߣ�
	 */
	REFERENCE,
	/**
	 * ������ǰ���������ߣ���ǰ�ߵ��������ڱ����߰���
	 */
	DEPENDENCE,
	/**
	 * ӵ�У�ǰ��ӵ�к��ߣ�������������ǰ�߹�����ߵ��������ڡ�
	 */
	OWNERSHIP,
	/**
	 * ���������߻���������ͬʱ���֣�ͬʱ����
	 */
	SYMBIOSIS
}