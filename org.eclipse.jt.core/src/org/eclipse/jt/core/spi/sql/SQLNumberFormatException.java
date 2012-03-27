package org.eclipse.jt.core.spi.sql;

/**
 * 数字字面量格式不正确异常
 * 
 * @author Jeff Tang
 * 
 */
public class SQLNumberFormatException extends SQLParseException {
	private static final long serialVersionUID = 1L;

	public SQLNumberFormatException(int line, int col, String token) {
		super(line, col, "数值格式不正确 '" + token + "'");
	}

	@Override
	public SQLErrorCode getErrorCode() {
		return SQLErrorCode.VALUE_FORMAT;
	}
}
