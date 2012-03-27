package org.eclipse.jt.core.def.exp;

/**
 * 条件表达式基接口<br>
 * 条件表达式指运算结果为布尔类型的表达式.可能为比较预算,逻辑运算等
 * 
 * @author Jeff Tang
 * 
 */
public interface ConditionalExpression {

	/**
	 * 是否取反
	 * 
	 * @return
	 */
	public boolean isNot();

	/**
	 * 获取取反的条件
	 */
	public ConditionalExpression not();

	/**
	 * 获取与条件
	 * 
	 * @param conditions
	 * @return
	 */
	public ConditionalExpression and(ConditionalExpression one,
			ConditionalExpression... others);

	/**
	 * 获取或条件
	 * 
	 * @param conditions
	 * @return
	 */
	public ConditionalExpression or(ConditionalExpression one,
			ConditionalExpression... others);

	/**
	 * 搜索case
	 * 
	 * <pre>
	 * CASE WHEN current_condition THEN returnValue [...n] [ELSE defaultValue] END
	 * </pre>
	 * 
	 * @param returnValue
	 *            值表达式
	 * @param others
	 *            条件达式与值表达式对,及最后可以带默认值
	 * @return
	 */
	public ValueExpression searchedCase(Object returnValue, Object... others);

}
