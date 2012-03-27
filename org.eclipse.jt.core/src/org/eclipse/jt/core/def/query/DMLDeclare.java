package org.eclipse.jt.core.def.query;

/**
 * DML语句定义
 * 
 * @see org.eclipse.jt.core.def.query.DMLDefine
 * 
 * @author Jeff Tang
 */
public interface DMLDeclare extends DMLDefine {

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

}
