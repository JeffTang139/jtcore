package org.eclipse.jt.core.exception;

/**
 * Core所引发的异常的基类
 * 
 * @author Jeff Tang
 * 
 */
public abstract class CoreException extends RuntimeException {

	private static final long serialVersionUID = -4678113765059209215L;

	CoreException() {

	}

	CoreException(String message) {
		super(message);
	}

	CoreException(String message, Throwable cause) {
		super(message, cause);
	}

	CoreException(Throwable cause) {
		super(cause);
	}
}
