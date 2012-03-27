package org.eclipse.jt.core.def.exp;

import org.eclipse.jt.core.def.query.QueryReferenceDefine;
import org.eclipse.jt.core.def.query.SelectColumnDefine;

/**
 * 查询列引用表达式
 * 
 * @author Jeff Tang
 * 
 */
public interface SelectColumnRefExpr extends RelationColumnRefExpr {

	/**
	 * 获取查询列定义
	 */
	public SelectColumnDefine getColumn();

	/**
	 * 获取所在的查询引用定义
	 */
	public QueryReferenceDefine getReference();
}
