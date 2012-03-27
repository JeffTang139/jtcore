package org.eclipse.jt.core.spi.auth;

import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.invoke.SimpleTask;
import org.eclipse.jt.core.type.GUID;


/**
 * 更新用户密码任务
 * 
 * <pre>
 * 使用示例：
 * task = new UpdateUserPasswordTask(userID, newPassword);
 * context.handle(task);
 * </pre>
 * 
 * @see org.eclipse.jt.core.invoke.SimpleTask
 * @author Jeff Tang 2009-11
 */
public final class UpdateUserPasswordTask extends SimpleTask {

	/**
	 * 用户ID
	 */
	public final GUID userID;

	/**
	 * 新密码
	 */
	public final String newPassword;

	/**
	 * 新建更新用户密码任务
	 * 
	 * @param userID
	 *            用户ID，不能为空
	 * @param newPassword
	 *            新密码，不能为空
	 */
	public UpdateUserPasswordTask(GUID userID, String newPassword) {
		if (userID == null) {
			throw new NullArgumentException("userID");
		}
		this.userID = userID;
		if (newPassword == null || newPassword.length() == 0) {
			this.newPassword = "";
		} else {
			this.newPassword = newPassword;
		}
	}

}
