package org.eclipse.jt.core.spi.sql;

/**
 * ��������ʽ�����쳣
 * 
 * @author Jeff Tang
 * 
 */
public class SQLValueFormatException extends SQLParseException {
	private static final long serialVersionUID = 1L;

	public SQLValueFormatException(int line, int col, String message) {
		super(line, col, message);
	}

	@Override
	public SQLErrorCode getErrorCode() {
		return SQLErrorCode.VALUE_FORMAT;
	}
}
