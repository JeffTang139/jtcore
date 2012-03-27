package org.eclipse.jt.core.exception;

/**
 * Core所引发的异常的基类
 * 
 * @author Jeff Tang
 * 
 */
public final class AbortException extends CoreException {
	private static final long serialVersionUID = -1L;

	public AbortException() {
	}

	public AbortException(Throwable cause) {
		super(cause);
	}
}
