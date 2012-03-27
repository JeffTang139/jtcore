package org.eclipse.jt.core.spi.sql;

/**
 * �������ظ��쳣
 * 
 * @author Jeff Tang
 * 
 */
public class SQLVariableDuplicateException extends SQLParseException {
	private static final long serialVersionUID = 1L;

	public SQLVariableDuplicateException(int line, int col, String name) {
		super(line, col, "�������ظ����� '" + name + "'");
	}

	public SQLVariableDuplicateException(String name) {
		this(0, 0, name);
	}

	@Override
	public SQLErrorCode getErrorCode() {
		return SQLErrorCode.VAR_DUPLICATE;
	}
}