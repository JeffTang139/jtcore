package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.table.AsTable;
import org.eclipse.jt.core.def.table.AsTableField;
import org.eclipse.jt.core.type.GUID;


/**
 * �û�����֯����ӳ��ʵ�� �û�����֯����ӳ��ʵ�嶨���Ӧ���û�����֯����ӳ�������Ĵ洢�ṹ��
 * 
 * <pre>
 * ����                ����        �շ�
 * RECID            GUID       ��
 * actorID          GUID       ��
 * orgID            GUID       ��
 * </pre>
 * 
 * @author Jeff Tang 2010-01
 */
@AsTable
final class CoreAuthUOMEntity {

	/**
	 * ��¼ID
	 */
	@AsTableField(isRecid = true)
	public GUID RECID;

	/**
	 * ������ID
	 */
	@AsTableField(isRequired = true)
	public GUID actorID;

	/**
	 * ������ID
	 */
	@AsTableField(isRequired = true)
	public GUID orgID;

}
