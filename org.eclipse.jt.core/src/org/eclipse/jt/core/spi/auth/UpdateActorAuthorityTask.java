package org.eclipse.jt.core.spi.auth;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jt.core.auth.AuthorizedResourceItem;
import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.invoke.SimpleTask;
import org.eclipse.jt.core.type.GUID;


/**
 * ���·�������Ȩ����
 * 
 * @see org.eclipse.jt.core.invoke.SimpleTask
 * @author Jeff Tang 2009-11
 */
public abstract class UpdateActorAuthorityTask extends SimpleTask {

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
	 * ��Ҫ�ύ�޸ĵ���Ȩ��Դ���б�
	 */
	public final List<AuthorizedResourceItem> authorityResourceTable = new ArrayList<AuthorizedResourceItem>();

	/**
	 * �½����·�������Ȩ����
	 * 
	 * @param actorID
	 *            ������ID������Ϊ��
	 * @param orgID
	 *            ��֯����ID����Ϊ�գ�Ϊ�մ���Ĭ�Ϲ�������֯����ID
	 * @param resourceCategoryID
	 *            ��Դ���ID������Ϊ��
	 */
	protected UpdateActorAuthorityTask(GUID actorID, GUID orgID,
			GUID resourceCategoryID) {
		if (actorID == null) {
			throw new NullArgumentException("actorID");
		}
		if (resourceCategoryID == null) {
			throw new NullArgumentException("resourceCategoryID");
		}
		this.actorID = actorID;
		this.orgID = orgID;
		this.resourceCategoryID = resourceCategoryID;
	}

}
