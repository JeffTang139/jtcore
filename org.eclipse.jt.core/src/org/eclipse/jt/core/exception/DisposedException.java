package org.eclipse.jt.core.exception;

/**
 * �����Ѿ����ٵĶ���ʱ���쳣
 * 
 * @author Jeff Tang
 * 
 */
public class DisposedException extends CoreException {
	private static final long serialVersionUID = 8138411932052305165L;

	public DisposedException() {
		super();
	}

	public DisposedException(String message) {
		super(message);
	}

	public DisposedException(String message, Throwable cause) {
		super(message, cause);
	}

	public DisposedException(Throwable cause) {
		super(cause);
	}
}
