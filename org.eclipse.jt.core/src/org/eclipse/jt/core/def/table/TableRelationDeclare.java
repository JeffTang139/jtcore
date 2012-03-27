package org.eclipse.jt.core.def.table;

import org.eclipse.jt.core.def.exp.ConditionalExpression;

/**
 * �����õı��ϵ����
 * 
 * @author Jeff Tang
 * 
 */
public interface TableRelationDeclare extends TableRelationDefine,
		TableReferenceDeclare {

	/**
	 * ��ù�ϵ��������
	 * 
	 * @return ���ر���
	 */
	public TableDeclare getOwner();

	/**
	 * ������������
	 */
	public void setJoinCondition(ConditionalExpression condition);

	/**
	 * ���ù�ϵ����
	 */
	public void setRelationType(TableRelationType type);

	public TableFieldDeclare getEquiRelationSelfField();

	public TableFieldDeclare getEquiRelationTargetField();

	@Deprecated
	public TableEquiRelationDeclare castAsEquiRelation();
}
