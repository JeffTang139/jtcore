package org.eclipse.jt.core.spi.auth;

import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.type.GUID;


/**
 * 为访问者授权时，获取指定可授权资源项的下级资源项列表的键
 * 
 * @author Jeff Tang 2009-11
 */
@Deprecated
public abstract class GetSubAuthorizedResourceItemsForActorKey {

	/**
	 * 访问者ID
	 */
	public final GUID actorID;

	/**
	 * 组织机构ID，为空代表默认关联的组织机构ID
	 */
	public GUID orgID;

	/**
	 * 资源类别ID
	 */
	public final GUID resourceCategoryID;

	/**
	 * 当前授权资源项，为空代表当前资源项为根授权资源项
	 */
	public GUID currentResID;

	/**
	 * 新建获取指定可授权资源项的下级资源项列表的键
	 * 
	 * @param actorID
	 *            访问者ID，不可为空
	 * @param resourceCategoryID
	 *            资源类别ID，不可为空
	 * @param rootAuthResItem
	 *            当前授权资源项，可为空
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
