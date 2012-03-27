package org.eclipse.jt.core.exception;

public final class SessionDisposedException extends DisposedException {

	private static final long serialVersionUID = 1L;

	/**
	 * 关闭的相关情况
	 * 
	 * @author Jeff Tang
	 * 
	 */
	public enum SessionDisposedKind {
		/**
		 * 一般关闭
		 */
		NORMAL,
		/**
		 * 早先的会话，（不是本次D&A启动后的Session）
		 */
		OBSOLETE,
		/**
		 * 登陆用户失效
		 */
		USERINVALID,
	}

	/**
	 * 不是本次D&A启动后的Session
	 */
	public final SessionDisposedKind kind;
	/**
	 * 不是本次D&A启动后的Session，兼容2.5以前的版本
	 */
	@Deprecated
	public final boolean obsolete;

	public SessionDisposedException(SessionDisposedKind kind) {
		super("会话已经销毁或过期");
		this.kind = kind;
		this.obsolete = kind == SessionDisposedKind.OBSOLETE;
	}
}
