package org.eclipse.jt.core.spi.auth;

import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.type.GUID;


/**
 * 为访问者授权时，获取所有可授权资源类别项的键
 * 
 * @author Jeff Tang 2009-11
 */
public abstract class GetAuthorizedResCategoryItemForActorKey {

	/**
	 * 访问者ID
	 */
	public final GUID actorID;

	/**
	 * 组织机构ID，为空代表默认关联的组织机构ID
	 */
	public GUID orgID;

	/**
	 * 新建获取所有可授权资源类别项的键
	 * 
	 * @param actorID
	 *            访问者ID，不能为空
	 * @param orgID
	 *            组织机构ID，可为空，为空代表默认关联的组织机构ID
	 */
	protected GetAuthorizedResCategoryItemForActorKey(GUID actorID, GUID orgID) {
		if (actorID == null) {
			throw new NullArgumentException("actorID");
		}
		this.actorID = actorID;
		this.orgID = orgID;
	}

}
