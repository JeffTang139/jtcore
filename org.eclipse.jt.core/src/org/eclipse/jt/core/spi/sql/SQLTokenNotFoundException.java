package org.eclipse.jt.core.spi.sql;

/**
 * ·ûºÅÎ´ÕÒµ½
 * 
 * @author Jeff Tang
 * 
 */
public class SQLTokenNotFoundException extends SQLParseException {
	private static final long serialVersionUID = 1L;

	public SQLTokenNotFoundException(int line, int col, String token) {
		super(line, col, "È±ÉÙ·ûºÅ '" + token + "'");
	}

	public SQLTokenNotFoundException(String token) {
		this(0, 0, token);
	}

	@Override
	public SQLErrorCode getErrorCode() {
		return SQLErrorCode.TOKEN_NOT_FOUND;
	}
}
