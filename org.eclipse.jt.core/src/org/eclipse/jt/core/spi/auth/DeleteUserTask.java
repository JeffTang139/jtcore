package org.eclipse.jt.core.spi.auth;

import org.eclipse.jt.core.type.GUID;

/**
 * 删除用户任务
 * 
 * <pre>
 * 使用示例：
 * task = new DeleteUserTask(userID);
 * context.handle(task);
 * </pre>
 * 
 * @see org.eclipse.jt.core.spi.auth.DeleteActorTask
 * @author Jeff Tang 2009-11
 */
public final class DeleteUserTask extends DeleteActorTask {

	/**
	 * 新建删除用户任务
	 * 
	 * @param roleID
	 *            用户ID，不能为空
	 */
	public DeleteUserTask(GUID userID) {
		super(userID);
	}

}
