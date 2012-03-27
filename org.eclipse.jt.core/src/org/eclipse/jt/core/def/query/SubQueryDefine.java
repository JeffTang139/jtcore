package org.eclipse.jt.core.def.query;

/**
 * 子查询定义
 * 
 * <p>
 * 泛指在非from子句中使用的子查询结构.与DerivedQuery不同之处在于:可以使用其结构所在域的关系引用.
 * 
 * <p>
 * 子查询定义可以转换为值表达式
 * 
 * @see org.eclipse.jt.core.def.query.DerivedQueryDefine
 * 
 * @author Jeff Tang
 * 
 */
public interface SubQueryDefine extends SelectDefine {

	/**
	 * 获取子查询表达式
	 * 
	 * @return 值表达式
	 */
	public SubQueryExpression newExpression();

	/**
	 * 获取查询所在的DML域
	 * 
	 * @return DML语句定义
	 */
	public DMLDefine getOwner();
}
