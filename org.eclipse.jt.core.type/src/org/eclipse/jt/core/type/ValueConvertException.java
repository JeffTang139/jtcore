package org.eclipse.jt.core.type;

/**
 * ×ª»»Òì³£
 * 
 * @author Jeff Tang
 * 
 */
public class ValueConvertException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2383783117905504434L;

	public ValueConvertException() {
		// do nothing
	}

	public ValueConvertException(String message) {
		super(message);
	}

	public ValueConvertException(Throwable cause) {
		super(cause);
	}
}
