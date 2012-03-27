package org.eclipse.jt.core.spi.sql;

/**
 * ��֧��ָ���Ĳ����쳣
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
		this(0, 0, "��֧�ָò���");
	}

	@Override
	public SQLErrorCode getErrorCode() {
		return SQLErrorCode.NOT_SUPPORT;
	}
}
