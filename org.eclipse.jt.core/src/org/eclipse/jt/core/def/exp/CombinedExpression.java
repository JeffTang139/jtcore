package org.eclipse.jt.core.def.exp;

/**
 * 联合表达式
 * 
 * @author Jeff Tang
 * 
 */
public interface CombinedExpression extends ConditionalExpression {
	/**
	 * 返回是否是与联合
	 * 
	 * @return 返回是否是与联合
	 */
	public boolean isAnd();

	/**
	 * 获得条件表达式的个数
	 * 
	 * @return 返回条件表达式的个数
	 */
	public int getCount();

	/**
	 * 返回第index个条件表达式
	 * 
	 * @param index
	 *            位置
	 * @return 返回第index个条件表达式
	 */
	public ConditionalExpression get(int index);
}
