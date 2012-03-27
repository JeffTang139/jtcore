package org.eclipse.jt.core.spi.auth;

import org.eclipse.jt.core.User;
import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.invoke.SimpleTask;

/**
 * 切换会话用户任务<br>
 * 通常情况下，用户在业务逻辑层执行完登录逻辑后，需通过该任务将用户设置为会话的当前用户。
 * 
 * <pre>
 * 使用示例：
 * context.handle(new ChangeSessionUserTask(user));
 * </pre>
 * 
 * @see org.eclipse.jt.core.invoke.SimpleTask
 * @author Jeff Tang 2009-12
 */
public final class ChangeSessionUserTask extends SimpleTask {

	/**
	 * 用户对象
	 */
	public final User user;

	/**
	 * 新建切换会话用户任务
	 * 
	 * @param user
	 *            用户
	 */
	public ChangeSessionUserTask(User user) {
		if (user == null) {
			throw new NullArgumentException("user");
		}
		this.user = user;
	}

}
