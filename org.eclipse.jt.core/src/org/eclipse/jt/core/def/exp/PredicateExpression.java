package org.eclipse.jt.core.def.exp;

/**
 * 谓词表达式
 * 
 * <p>
 * 表示运算结果为逻辑值的运算
 * 
 * @author Jeff Tang
 * 
 */
public interface PredicateExpression extends ConditionalExpression {

	/**
	 * 获得谓词
	 * 
	 * @return 返回谓词
	 */
	public Predicate getPredicate();

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
