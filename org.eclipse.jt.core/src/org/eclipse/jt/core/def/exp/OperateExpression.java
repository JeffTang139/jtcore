package org.eclipse.jt.core.def.exp;

/**
 * ������ʽ
 * 
 * <p>
 * ��ʾ������Ϊֵ���ʽ������
 * 
 * @author Jeff Tang
 * 
 */
public interface OperateExpression extends ValueExpression {

	/**
	 * ��ò�����
	 * 
	 * @return ���ز�����
	 */
	public Operator getOperator();

	/**
	 * ���ֵ���ʽ�ĸ���
	 * 
	 * @return �����������ʽ�ĸ���
	 */
	public int getCount();

	/**
	 * ���ص�index��ֵ���ʽ
	 * 
	 * @param index
	 *            λ��
	 * @return ���ص�index��ֵ���ʽ
	 */
	public ValueExpression get(int index);
}
