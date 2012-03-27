package org.eclipse.jt.core.def.query;

import org.eclipse.jt.core.def.exp.TableFieldRefExpr;

/**
 * 更新语句定义
 * 
 * @see org.eclipse.jt.core.def.query.ModifyStatementDefine
 * 
 * @author Jeff Tang
 */
public interface ModifyStatementDeclare extends ModifyStatementDefine,
		StatementDeclare, RelationRefDomainDeclare {

	/**
	 * 构造子查询定义
	 * 
	 * @return
	 */
	public SubQueryDeclare newSubQuery();

	/**
	 * 构造导出查询定义,用于from子句使用
	 * 
	 * @return
	 */
	public DerivedQueryDeclare newDerivedQuery();

	/**
	 * 创建字段引用表达式
	 * 
	 * @param field
	 * @return
	 */
	public TableFieldRefExpr expOf(RelationColumnDefine column);

	/**
	 * 创建字段引用表达式
	 * 
	 * @param columnName
	 * @return
	 */
	public TableFieldRefExpr expOf(String columnName);
}
