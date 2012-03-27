package org.eclipse.jt.core.def.exp;

/**
 * ���ϱ��ʽ
 * 
 * @author Jeff Tang
 * 
 */
public interface CombinedExpression extends ConditionalExpression {
	/**
	 * �����Ƿ���������
	 * 
	 * @return �����Ƿ���������
	 */
	public boolean isAnd();

	/**
	 * ����������ʽ�ĸ���
	 * 
	 * @return �����������ʽ�ĸ���
	 */
	public int getCount();

	/**
	 * ���ص�index���������ʽ
	 * 
	 * @param index
	 *            λ��
	 * @return ���ص�index���������ʽ
	 */
	public ConditionalExpression get(int index);
}
