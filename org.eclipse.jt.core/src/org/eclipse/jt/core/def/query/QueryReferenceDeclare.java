package org.eclipse.jt.core.def.query;

import org.eclipse.jt.core.def.exp.SelectColumnRefExpr;

/**
 * 查询引用定义
 * 
 * @author Jeff Tang
 * 
 */
public interface QueryReferenceDeclare extends QueryReferenceDefine,
		RelationRefDeclare {

	public SelectDeclare getTarget();

	public SelectColumnRefExpr expOf(RelationColumnDefine column);

	public SelectColumnRefExpr expOf(String columnName);

}
