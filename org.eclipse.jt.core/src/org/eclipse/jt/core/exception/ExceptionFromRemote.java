package org.eclipse.jt.core.exception;

/**
 * 远程异常代理
 * 
 * @author Jeff Tang
 * 
 */
public class ExceptionFromRemote extends RuntimeException {
	private static final long serialVersionUID = 1L;

	private final String remoteExceptionClassName;

	public final String getRemoteExceptionClassName() {
		return this.remoteExceptionClassName;
	}

	public ExceptionFromRemote(String message, String remoteExceptionClassName,
			ExceptionFromRemote cause) {
		super(message, cause);
		this.remoteExceptionClassName = remoteExceptionClassName;
	}
}
