package org.eclipse.jt.core.spi.auth;

import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.type.GUID;


/**
 * Ϊ��������Ȩʱ����ȡ���п���Ȩ��Դ�����ļ�
 * 
 * @author Jeff Tang 2009-11
 */
public abstract class GetAuthorizedResCategoryItemForActorKey {

	/**
	 * ������ID
	 */
	public final GUID actorID;

	/**
	 * ��֯����ID��Ϊ�մ���Ĭ�Ϲ�������֯����ID
	 */
	public GUID orgID;

	/**
	 * �½���ȡ���п���Ȩ��Դ�����ļ�
	 * 
	 * @param actorID
	 *            ������ID������Ϊ��
	 * @param orgID
	 *            ��֯����ID����Ϊ�գ�Ϊ�մ���Ĭ�Ϲ�������֯����ID
	 */
	protected GetAuthorizedResCategoryItemForActorKey(GUID actorID, GUID orgID) {
		if (actorID == null) {
			throw new NullArgumentException("actorID");
		}
		this.actorID = actorID;
		this.orgID = orgID;
	}

}
