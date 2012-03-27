package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.table.AsTable;
import org.eclipse.jt.core.def.table.AsTableField;
import org.eclipse.jt.core.type.GUID;


/**
 * ACLʵ�嶨��<br>
 * ACL����������ĳ��������ĳ��֯�����¶�ĳ����Դ�е�ĳ����Դӵ��ʲôȨ�ޡ�<br>
 * ACLʵ�嶨���Ӧ��ACL�����Ĵ洢�ṹ��
 * 
 * <pre>
 * ����                ����      �շ�
 * RECID            GUID     ��
 * actorID          GUID     ��
 * orgID            GUID     ��
 * resCategoryID    GUID     ��
 * resourceID       GUID     ��
 * authorityCode    int      ��
 * </pre>
 * 
 * @author Jeff Tang 2009-12
 */
@AsTable
final class CoreAuthACLEntity {

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
	 * ��֯����ID
	 */
	@AsTableField(isRequired = true)
	public GUID orgID;

	/**
	 * ��Դ���ID
	 */
	@AsTableField(isRequired = true)
	public GUID resCategoryID;

	/**
	 * ��ԴID
	 */
	@AsTableField(isRequired = true)
	public GUID resourceID;

	/**
	 * ��Ȩ��Ϣ����
	 */
	@AsTableField(isRequired = true)
	public int authorityCode;
	
	/**
	 * ����Դ��GUID
	 */
	static final GUID ROOT_RESOURCE_GUID = GUID.valueOf(0xAAAAAAAAAAAAAAAAL,
			0xAAAAAAAAAAAAAAAAL);

}
