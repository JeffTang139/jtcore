package org.eclipse.jt.core.spi.sql;

/**
 * ����δ�����쳣
 * 
 * @author Jeff Tang
 * 
 */
public class SQLFunctionUndefinedException extends SQLParseException {
	private static final long serialVersionUID = 1L;

	public SQLFunctionUndefinedException(int line, int col, String name) {
		super(line, col, "�Ҳ������� '" + name + "'");
	}

	public SQLFunctionUndefinedException(String name) {
		this(0, 0, name);
	}

	@Override
	public SQLErrorCode getErrorCode() {
		return SQLErrorCode.FUNC_UNDEFINED;
	}
}
