package org.eclipse.jt.core.def.query;

import org.eclipse.jt.core.def.exp.PredicateExpression;

/**
 * 子查询定义
 * 
 * @see org.eclipse.jt.core.def.query.SubQueryDefine
 * 
 * @author Jeff Tang
 * 
 */
public interface SubQueryDeclare extends SubQueryDefine, SelectDeclare {

	/**
	 * 构造Exits谓词表达式
	 * 
	 * @return
	 */
	public PredicateExpression exists();

	/**
	 * 构造Not Exists谓词表达式
	 * 
	 * @return
	 */
	public PredicateExpression notExists();

}
