package org.eclipse.jt.core.def.table;

import org.eclipse.jt.core.def.NamedDeclare;

/**
 * ���ζ���
 * 
 * @author Jeff Tang
 * 
 */
public interface HierarchyDeclare extends HierarchyDefine, NamedDeclare {

	/**
	 * ��������
	 */
	public TableDeclare getOwner();

	/**
	 * �������֧�ֵļ���
	 */
	public void setMaxLevel(int maxLevel);

}
