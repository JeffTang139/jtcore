package org.eclipse.jt.core.spi.sql;

/**
 * 不支持指定的操作异常
 * 
 * @author Jeff Tang
 * 
 */
public class SQLNotSupportedException extends SQLParseException {
	private static final long serialVersionUID = 1L;

	public SQLNotSupportedException(int line, int col, String message) {
		super(line, col, message);
	}

	public SQLNotSupportedException(String message) {
		this(0, 0, message);
	}

	public SQLNotSupportedException() {
		this(0, 0, "不支持该操作");
	}

	@Override
	public SQLErrorCode getErrorCode() {
		return SQLErrorCode.NOT_SUPPORT;
	}
}
