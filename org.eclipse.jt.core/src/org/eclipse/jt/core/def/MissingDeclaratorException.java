package org.eclipse.jt.core.def;

import org.eclipse.jt.core.misc.MissingObjectException;

/**
 * ’“≤ªµΩ
 * 
 * @author Jeff Tang
 * 
 */
public class MissingDeclaratorException extends MissingObjectException {

	private static final long serialVersionUID = 1L;

	public MissingDeclaratorException() {
		super();
	}

	public MissingDeclaratorException(String message, Throwable cause) {
		super(message, cause);
	}

	public MissingDeclaratorException(String message) {
		super(message);
	}

	public MissingDeclaratorException(Throwable cause) {
		super(cause);
	}
}
