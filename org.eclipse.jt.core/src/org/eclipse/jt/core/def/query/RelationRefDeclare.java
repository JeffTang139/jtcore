package org.eclipse.jt.core.def.query;

import org.eclipse.jt.core.def.NamedDeclare;
import org.eclipse.jt.core.def.exp.RelationColumnRefExpr;

/**
 * ��ϵ����
 * 
 * @see org.eclipse.jt.core.def.query.RelationRefDefine
 * 
 * @author Jeff Tang
 * 
 */
@SuppressWarnings({ "unused", "deprecation" })
public interface RelationRefDeclare extends RelationRefDefine,
		RelationJoinable, HierarchyOperatable, NamedDeclare,
		MoRelationRefDeclare {

	public RelationDeclare getTarget();

	/**
	 * �����ϵ�����ñ��ʽ
	 * 
	 * @param column
	 *            ��ϵ�ж���
	 * @return
	 */
	public RelationColumnRefExpr expOf(RelationColumnDefine column);

	/**
	 * �����ϵ�����ñ��ʽ
	 * 
	 * @param columnName
	 *            ��ϵ������
	 * @return
	 */
	public RelationColumnRefExpr expOf(String columnName);

}
