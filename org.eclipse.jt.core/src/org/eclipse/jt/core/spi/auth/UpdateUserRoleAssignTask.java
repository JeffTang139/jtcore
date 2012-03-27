package org.eclipse.jt.core.spi.auth;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.invoke.SimpleTask;
import org.eclipse.jt.core.type.GUID;


/**
 * 更新用户角色分配任务
 * 
 * <pre>
 * task = new UpdateUserRoleAssignTask(userID);
 * task.assignRoleIDList.add(roleID);
 * task.priorityIndex = 1;
 * context.handle(task);
 * </pre>
 * 
 * @see org.eclipse.jt.core.invoke.SimpleTask
 * @author Jeff Tang 2009-11
 */
public final class UpdateUserRoleAssignTask extends SimpleTask {

	/**
	 * 用户ID
	 */
	public final GUID userID;

	/**
	 * 为用户分配的角色ID列表<br>
	 * 越先加入的优先级越高。
	 */
	public final List<GUID> assignActorIDList = new ArrayList<GUID>();

	/**
	 * 新建更新用户角色分配任务
	 * 
	 * @param userID
	 *            用户ID
	 */
	public UpdateUserRoleAssignTask(GUID userID) {
		if (userID == null) {
			throw new NullArgumentException("userID");
		}
		this.userID = userID;
	}

}
