package org.eclipse.jt.core.def;

import org.eclipse.jt.core.misc.MissingObjectException;

/**
 * ’“≤ªµΩ
 * 
 * @author Jeff Tang
 * 
 */
public class MissingDefineException extends MissingObjectException {

	private static final long serialVersionUID = 1L;

	public MissingDefineException() {
		super();
	}

	public MissingDefineException(String message, Throwable cause) {
		super(message, cause);
	}

	public MissingDefineException(String message) {
		super(message);
	}

	public MissingDefineException(Throwable cause) {
		super(cause);
	}
}
