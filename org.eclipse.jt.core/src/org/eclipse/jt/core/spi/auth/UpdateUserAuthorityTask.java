package org.eclipse.jt.core.spi.auth;

import org.eclipse.jt.core.type.GUID;

/**
 * 更新用户授权任务<br>
 * 由于用户在不同的组织机构下，权限也不相同，所以在更新用户授权时，还须指定组织机构。
 * 
 * <pre>
 * 使用示例：
 * task = new UpdateUserAuthorityTask(roleID, orgID, resourceCategoryID);
 * task.authorityResourceTable.add(authorizedResourceItem);
 * context.handle(task);
 * </pre>
 * 
 * @see org.eclipse.jt.core.spi.auth.UpdateActorAuthorityTask
 * @author Jeff Tang 2009-11
 */
public final class UpdateUserAuthorityTask extends UpdateActorAuthorityTask {
	
	/**
	 * 新建更新用户授权任务
	 * 
	 * @param userID
	 *            用户ID，不能为空
	 * @param orgID
	 *            组织机构ID，可为空，为空代表默认关联的组织机构ID
	 * @param resourceCategoryID
	 *            资源类别ID，不可为空
	 */
	public UpdateUserAuthorityTask(GUID userID, GUID orgID, GUID resourceCategoryID) {
		super(userID, orgID, resourceCategoryID);
	}

}
