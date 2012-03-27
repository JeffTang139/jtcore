package org.eclipse.jt.core.spi.sql;

/**
 * �����Ҳ����쳣
 * 
 * @author Jeff Tang
 * 
 */
public class SQLClassNotFoundException extends SQLParseException {
	private static final long serialVersionUID = 1L;

	public SQLClassNotFoundException(int line, int col, String className) {
		super(line, col, "�Ҳ������Ͷ��� '" + className + "'");
	}

	public SQLClassNotFoundException(String className) {
		this(0, 0, className);
	}

	@Override
	public SQLErrorCode getErrorCode() {
		return SQLErrorCode.CLASS_NOT_FOUND;
	}
}
