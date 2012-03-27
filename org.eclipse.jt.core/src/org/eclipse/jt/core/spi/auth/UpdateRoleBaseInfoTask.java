package org.eclipse.jt.core.spi.auth;

import org.eclipse.jt.core.type.GUID;

/**
 * 更新角色基本信息任务
 * 
 * <pre>
 * 使用示例：
 * task = new UpdateRoleBaseInfoTask(roleID);
 * task.title = &quot;update role title&quot;;
 * context.handle(task);
 * </pre>
 * 
 * @see org.eclipse.jt.core.spi.auth.UpdateActorBaseInfoTask
 * @author Jeff Tang 2009-11
 */
public final class UpdateRoleBaseInfoTask extends UpdateActorBaseInfoTask {

	/**
	 * 新建更新角色基本信息任务
	 * 
	 * @param roleID
	 *            角色ID，不能为空
	 */
	public UpdateRoleBaseInfoTask(GUID roleID) {
		super(roleID);
	}

}
