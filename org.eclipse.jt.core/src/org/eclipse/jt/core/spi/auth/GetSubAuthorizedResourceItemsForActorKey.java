package org.eclipse.jt.core.spi.auth;

import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.type.GUID;


/**
 * Ϊ��������Ȩʱ����ȡָ������Ȩ��Դ����¼���Դ���б�ļ�
 * 
 * @author Jeff Tang 2009-11
 */
@Deprecated
public abstract class GetSubAuthorizedResourceItemsForActorKey {

	/**
	 * ������ID
	 */
	public final GUID actorID;

	/**
	 * ��֯����ID��Ϊ�մ���Ĭ�Ϲ�������֯����ID
	 */
	public GUID orgID;

	/**
	 * ��Դ���ID
	 */
	public final GUID resourceCategoryID;

	/**
	 * ��ǰ��Ȩ��Դ�Ϊ�մ���ǰ��Դ��Ϊ����Ȩ��Դ��
	 */
	public GUID currentResID;

	/**
	 * �½���ȡָ������Ȩ��Դ����¼���Դ���б�ļ�
	 * 
	 * @param actorID
	 *            ������ID������Ϊ��
	 * @param resourceCategoryID
	 *            ��Դ���ID������Ϊ��
	 * @param rootAuthResItem
	 *            ��ǰ��Ȩ��Դ���Ϊ��
	 */
	protected GetSubAuthorizedResourceItemsForActorKey(GUID actorID,
			GUID orgID, GUID resourceCategoryID, GUID currentResID) {
		if (actorID == null) {
			throw new NullArgumentException("actorID");
		}
		if (resourceCategoryID == null) {
			throw new NullArgumentException("resourceCategoryID");
		}
		this.actorID = actorID;
		this.orgID = orgID;
		this.resourceCategoryID = resourceCategoryID;
		this.currentResID = currentResID;
	}

}
