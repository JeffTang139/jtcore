package org.eclipse.jt.core.spi.sql;

/**
 * SQL分析异常 (包括词法分析和语义分析)
 * 
 * @author Jeff Tang
 * 
 */
public abstract class SQLParseException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public int line;
	public int col;

	public SQLParseException(int line, int col, String message) {
		super(message);
		this.line = line;
		this.col = col;
	}

	public SQLParseException(int line, int col, Throwable ex) {
		super(ex);
		this.line = line;
		this.col = col;
	}

	public SQLParseException(int line, int col, String message, Throwable ex) {
		super(message, ex);
		this.line = line;
		this.col = col;
	}

	public SQLParseException(Throwable ex) {
		super(ex);
	}

	public SQLParseException(String message, Throwable ex) {
		super(message, ex);
	}

	public abstract SQLErrorCode getErrorCode();
}
