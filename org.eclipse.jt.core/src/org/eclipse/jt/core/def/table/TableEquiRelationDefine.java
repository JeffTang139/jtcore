package org.eclipse.jt.core.def.table;

/**
 * ��ֵ���ϵ
 * 
 * @author Jeff Tang
 * 
 */
@Deprecated
public interface TableEquiRelationDefine extends TableRelationDefine {

	/**
	 * ��ֵ�����е�ǰ����ֶζ���
	 * 
	 * @return
	 */
	public TableFieldDefine getSelfField();

	/**
	 * ��ֵ������Ŀ�����ֶζ���
	 * 
	 * @return
	 */
	public TableFieldDefine getTargetField();
}
