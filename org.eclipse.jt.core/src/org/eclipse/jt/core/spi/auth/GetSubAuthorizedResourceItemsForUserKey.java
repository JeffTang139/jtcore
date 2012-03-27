package org.eclipse.jt.core.spi.auth;

import org.eclipse.jt.core.type.GUID;

/**
 * 为用户授权时，获取指定可授权资源项的下级资源项列表的键<br>
 * 由于用户在不同的组织机构下，权限也不相同，所以获取下级资源项时，还须指定组织机构。<br>
 * 返回可授权资源项列表。
 * 
 * <pre>
 * 使用示例：
 * key = new GetSubAuthorizedResourceItemsForUserKey(userID, orgID, resourceCategoryID, currentResID);
 * context.getList(AuthorizedResourceItem.class, key);
 * </pre>
 * 
 * @see org.eclipse.jt.core.spi.auth.GetSubAuthorizedResourceItemsForActorKey
 * @author Jeff Tang 2009-11
 */
@Deprecated
public final class GetSubAuthorizedResourceItemsForUserKey extends
		GetSubAuthorizedResourceItemsForActorKey {

	/**
	 * 新建获取指定可授权资源项的下级资源项列表的键
	 * 
	 * @param roleID
	 *            角色ID，不能为空
	 * @param orgID
	 *            组织机构ID，可为空，为空表示全局组织机构
	 * @param resourceCategoryID
	 *            资源类别ID，不可为空
	 * @param currentResID
	 *            当前授权资源项，可为空
	 */
	public GetSubAuthorizedResourceItemsForUserKey(GUID userID, GUID orgID,
			GUID resourceCategoryID, GUID currentResID) {
		super(userID, orgID, resourceCategoryID, currentResID);
	}

}
