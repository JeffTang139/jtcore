package org.eclipse.jt.core.spi.auth;

import org.eclipse.jt.core.User;
import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.invoke.SimpleTask;

/**
 * 登陆任务，尝试使用指定的用户名和密码登陆并注销当前用户。<BR>
 * 失败信息将通过信息报告返回<br>
 * 系统初始是在anonym用户下运行<br>
 * 使用anonym用户登陆等效于注销当前用户<br>
 * 
 * 注意：鉴于不同的业务登录的逻辑也不相同。框架只是提供了一个登录任务类，但没有提供相应的任务处理器，需要开发人员实现任务处理器。
 * 
 * <pre>
 * 使用示例：
 * task = new LoginTask(&quot;someuser&quot;, &quot;his password&quot;);
 * context.handle(task);
 * </pre>
 * 
 * @author Jeff Tang
 * 
 */
public final class LoginTask extends SimpleTask {

	/**
	 * 调试用户名，系统必须带有-Ddebugger=true参数启动，该用户才可以使用。<br>
	 * 该用户接收任何密码，权限无穷大。
	 */
	public static final String USER_NAME_DEBUGGER = User.USER_NAME_DEBUGGER;

	/**
	 * 匿名用户名
	 */
	public static final String USER_NAME_ANONYM = User.USER_NAME_ANONYM;

	/**
	 * 用户名
	 */
	public final String userName;

	/**
	 * 密码
	 */
	public final String password;

	/**
	 * 是否验证密码
	 */
	public final boolean validatePassword;

	/**
	 * 登录异常信息
	 */
	public String exceptionMessage;

	/**
	 * 新建登录任务
	 * 
	 * @param userName
	 *            登录用户名，不能为空
	 * @param password
	 *            登录密码，不能为空
	 */
	public LoginTask(String userName, String password) {
		if (userName == null) {
			throw new NullArgumentException("userName");
		}
		if (password == null) {
			throw new NullArgumentException("password");
		}
		this.userName = userName;
		this.password = password;
		this.validatePassword = true;
	}

	/**
	 * 新建登录任务<br>
	 * 不用密码登陆。
	 * 
	 * @param userName
	 *            登录用户名，不能为空
	 */
	public LoginTask(String userName) {
		if (userName == null) {
			throw new NullArgumentException("userName");
		}
		this.userName = userName;
		this.password = null;
		this.validatePassword = true;
	}

	/**
	 * 新建登录任务
	 * 
	 * @param userName
	 *            登录用户名，不能为空
	 * @param password
	 *            登录密码，不能为空
	 * @param validatePassword
	 *            是否验证密码
	 */
	public LoginTask(String userName, String password, boolean validatePassword) {
		if (userName == null) {
			throw new NullArgumentException("userName");
		}
		if (password == null) {
			throw new NullArgumentException("password");
		}
		this.userName = userName;
		this.password = password;
		this.validatePassword = validatePassword;
	}

	/**
	 * 新建登录任务<br>
	 * 不用密码登陆。
	 * 
	 * @param userName
	 *            登录用户名，不能为空
	 * @param validatePassword
	 *            是否验证密码
	 */
	public LoginTask(String userName, boolean validatePassword) {
		if (userName == null) {
			throw new NullArgumentException("userName");
		}
		this.userName = userName;
		this.password = null;
		this.validatePassword = validatePassword;
	}

}
