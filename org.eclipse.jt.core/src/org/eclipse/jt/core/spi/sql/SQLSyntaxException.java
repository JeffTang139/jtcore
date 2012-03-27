package org.eclipse.jt.core.spi.sql;

/**
 * Óï·¨´íÎó
 * 
 * @author Jeff Tang
 * 
 */
public class SQLSyntaxException extends SQLParseException {
	private static final long serialVersionUID = 1L;

	public SQLSyntaxException(int line, int col, String message) {
		super(line, col, message);
	}

	public SQLSyntaxException(String message) {
		this(0, 0, message);
	}
	
	public SQLSyntaxException() {
		this("Óï·¨´íÎó");
	}

	@Override
	public SQLErrorCode getErrorCode() {
		return SQLErrorCode.SYNTAX;
	}
}
