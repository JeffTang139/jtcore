package org.eclipse.jt.core.def.query;

import org.eclipse.jt.core.def.exp.SelectColumnRefExpr;

/**
 * ��ѯ���ö���
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
