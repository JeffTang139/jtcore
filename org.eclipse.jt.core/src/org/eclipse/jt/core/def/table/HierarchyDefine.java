package org.eclipse.jt.core.def.table;

import org.eclipse.jt.core.def.NamedDefine;

/**
 * ���ζ���
 * 
 * @author Jeff Tang
 * 
 */
public interface HierarchyDefine extends NamedDefine {

	/**
	 * ��������
	 */
	public TableDefine getOwner();

	/**
	 * ������֧�ֵļ������
	 * 
	 * @return ������󼶴�
	 */
	public int getMaxLevel();

}
