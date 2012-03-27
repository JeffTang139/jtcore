package org.eclipse.jt.core.spi.auth;

import org.eclipse.jt.core.type.GUID;

/**
 * 删除角色组织机构映射任务
 * 
 * <pre>
 * 使用示例：
 * task = new DeleteRoleOrganizationMappingTask(roleID, orgID);
 * context.handle(task);
 * </pre>
 * 
 * @see org.eclipse.jt.core.spi.auth.DeleteActorOrganizationMappingTask
 * @author Jeff Tang 2010-01
 */
public final class DeleteRoleOrganizationMappingTask extends
		DeleteActorOrganizationMappingTask {

	/**
	 * 新建删除角色组织机构映射任务
	 * 
	 * @param actorID
	 *            角色ID，不能为空
	 * @param orgID
	 *            组织机构ID，不能为空
	 */
	public DeleteRoleOrganizationMappingTask(GUID roleID, GUID orgID) {
		super(roleID, orgID);
	}

}
