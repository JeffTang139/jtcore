package org.eclipse.jt.core.def.exp;

/**
 * ν�ʱ��ʽ
 * 
 * <p>
 * ��ʾ������Ϊ�߼�ֵ������
 * 
 * @author Jeff Tang
 * 
 */
public interface PredicateExpression extends ConditionalExpression {

	/**
	 * ���ν��
	 * 
	 * @return ����ν��
	 */
	public Predicate getPredicate();

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
