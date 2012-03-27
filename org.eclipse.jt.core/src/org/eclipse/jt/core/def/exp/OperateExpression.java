package org.eclipse.jt.core.def.exp;

/**
 * 运算表达式
 * 
 * <p>
 * 表示运算结果为值表达式的运算
 * 
 * @author Jeff Tang
 * 
 */
public interface OperateExpression extends ValueExpression {

	/**
	 * 获得操作符
	 * 
	 * @return 返回操作符
	 */
	public Operator getOperator();

	/**
	 * 获得值表达式的个数
	 * 
	 * @return 返回条件表达式的个数
	 */
	public int getCount();

	/**
	 * 返回第index个值表达式
	 * 
	 * @param index
	 *            位置
	 * @return 返回第index个值表达式
	 */
	public ValueExpression get(int index);
}
