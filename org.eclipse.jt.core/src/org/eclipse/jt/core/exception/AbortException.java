package org.eclipse.jt.core.exception;

/**
 * Core���������쳣�Ļ���
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
