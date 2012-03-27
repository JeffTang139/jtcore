package org.eclipse.jt.core.exception;

public final class DataSourceInitializationException extends CoreException {

	private static final long serialVersionUID = -4513307966597262304L;

	public DataSourceInitializationException(String msg) {
		super(msg);
	}

	public DataSourceInitializationException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
