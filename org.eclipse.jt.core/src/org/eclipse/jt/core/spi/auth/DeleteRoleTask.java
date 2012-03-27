package org.eclipse.jt.core.spi.auth;

import org.eclipse.jt.core.type.GUID;

/**
 * 删除角色任务
 * 
 * <pre>
 * 使用示例：
 * task = new DeleteRoleTask(roleID);
 * context.handle(task);
 * </pre>
 * 
 * @see org.eclipse.jt.core.spi.auth.DeleteActorTask
 * @author Jeff Tang 2009-11
 */
public final class DeleteRoleTask extends DeleteActorTask {

	/**
	 * 新建删除角色任务
	 * 
	 * @param roleID
	 *            角色ID，不能为空
	 */
	public DeleteRoleTask(GUID roleID) {
		super(roleID);
	}

}
