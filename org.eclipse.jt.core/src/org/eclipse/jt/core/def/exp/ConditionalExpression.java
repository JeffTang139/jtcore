package org.eclipse.jt.core.def.exp;

/**
 * �������ʽ���ӿ�<br>
 * �������ʽָ������Ϊ�������͵ı��ʽ.����Ϊ�Ƚ�Ԥ��,�߼������
 * 
 * @author Jeff Tang
 * 
 */
public interface ConditionalExpression {

	/**
	 * �Ƿ�ȡ��
	 * 
	 * @return
	 */
	public boolean isNot();

	/**
	 * ��ȡȡ��������
	 */
	public ConditionalExpression not();

	/**
	 * ��ȡ������
	 * 
	 * @param conditions
	 * @return
	 */
	public ConditionalExpression and(ConditionalExpression one,
			ConditionalExpression... others);

	/**
	 * ��ȡ������
	 * 
	 * @param conditions
	 * @return
	 */
	public ConditionalExpression or(ConditionalExpression one,
			ConditionalExpression... others);

	/**
	 * ����case
	 * 
	 * <pre>
	 * CASE WHEN current_condition THEN returnValue [...n] [ELSE defaultValue] END
	 * </pre>
	 * 
	 * @param returnValue
	 *            ֵ���ʽ
	 * @param others
	 *            ������ʽ��ֵ���ʽ��,�������Դ�Ĭ��ֵ
	 * @return
	 */
	public ValueExpression searchedCase(Object returnValue, Object... others);

}
