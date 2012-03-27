package org.eclipse.jt.core.spi.auth;

import org.eclipse.jt.core.type.GUID;

/**
 * 为用户授权时，获取所有可授权资源类别项的键<br>
 * 由于用户在不同的组织机构下，权限也不相同，所以获取所有可授权资源类别项时，还须指定组织机构。<br>
 * 返回所有可授权资源类别项。
 * 
 * <pre>
 * 使用示例：
 * key = new GetAuthorizedResCategoryItemForUserKey(userID, orgID);
 * context.getList(AuthorityResourceCategoryItem.class, key);
 * </pre>
 * 
 * @see org.eclipse.jt.core.spi.auth.GetAuthorizedResCategoryItemForActorKey
 * @author Jeff Tang 2009-11
 */
public final class GetAuthorizedResCategoryItemForUserKey extends
		GetAuthorizedResCategoryItemForActorKey {

	/**
	 * 新建获取所有可授权资源类别项的键
	 * 
	 * @param userID
	 *            用户ID，不能为空
	 * @param orgID
	 *            组织机构ID，可为空，为空代表默认关联的组织机构ID
	 */
	public GetAuthorizedResCategoryItemForUserKey(GUID userID, GUID orgID) {
		super(userID, orgID);
	}

}
