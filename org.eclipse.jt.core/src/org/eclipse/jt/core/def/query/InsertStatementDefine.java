package org.eclipse.jt.core.def.query;

/**
 * 插入语句定义
 * 
 * @author Jeff Tang
 * 
 */
public interface InsertStatementDefine extends ModifyStatementDefine {

	/**
	 * 返回定义插入值的查询语句
	 * 
	 * @return
	 */
	public DerivedQueryDefine getInsertValues();
}
