package org.eclipse.jt.core.def.table;

/**
 * ��ֵ���ϵ
 * 
 * @see org.eclipse.jt.core.def.table.TableEquiRelationDefine
 * 
 * @author Jeff Tang
 */
@Deprecated
public interface TableEquiRelationDeclare extends TableEquiRelationDefine,
		TableRelationDeclare {

	public TableFieldDeclare getSelfField();

	public TableFieldDeclare getTargetField();

	/**
	 * ���ñ���ĵ�ֵ��ϵ�ֶ�
	 */
	public void setSelfField(TableFieldDefine selfField);

	/**
	 * ����Ŀ���ĵ�ֵ��ϵ�ֶ�
	 */
	public void setTargetField(TableFieldDefine targetField);
}
