package org.eclipse.jt.core.def.query;

/**
 * ��������
 * 
 * <p>
 * ��ѯĬ�Ϸ�������Ϊdefault
 * 
 * @author Jeff Tang
 * 
 */
public enum GroupByType {

	/**
	 * Ĭ�ϵķ������
	 */
	DEFAULT,

	/**
	 * ָ��������а���rollup���͵Ļ�����
	 */
	ROLL_UP,

	/**
	 * ָ��������а���cube���͵Ļ�����
	 * 
	 * @deprecated ��֧��
	 */
	@Deprecated
	CUBE;

}
