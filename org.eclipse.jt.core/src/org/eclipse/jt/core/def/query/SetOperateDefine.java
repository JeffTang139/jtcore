package org.eclipse.jt.core.def.query;

/**
 * ��������
 * 
 * @author Jeff Tang
 * 
 */
public interface SetOperateDefine {

	/**
	 * ��ȡ���������
	 * 
	 * @return
	 */
	public SetOperator getOperator();

	/**
	 * ��ȡ�����������
	 * 
	 * @return
	 */
	public DerivedQueryDefine getTarget();
}
