package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.table.AsTable;
import org.eclipse.jt.core.def.table.AsTableField;
import org.eclipse.jt.core.type.GUID;


/**
 * ��ɫ������ʵ�嶨��<br>
 * ��ɫ������ʵ�嶨���Ӧ�ڽ�ɫ���������Ĵ洢�ṹ��
 * 
 * <pre>
 * ����                ����      �շ�
 * RECID            GUID     ��
 * actorID          GUID     ��
 * roleID           GUID     ��
 * priority         int      ��
 * </pre>
 * 
 * @author Jeff Tang 2009-12
 */
@AsTable
final class CoreAuthRAEntity {

	/**
	 * ��¼ID
	 */
	@AsTableField(isRecid = true)
	public GUID RECID;
	
	/**
	 * ������ID���������ɫ��
	 */
	@AsTableField(isRequired = true)
	public GUID actorID;
	
	/**
	 * ��ɫID
	 */
	@AsTableField(isRequired = true)
	public GUID roleID;
	
	/**
	 * ���ȼ�
	 */
	@AsTableField(isRequired = true)
	public int priority;
	 
}
