package org.eclipse.jt.core.spi.sql;

/**
 * �����ظ������쳣
 * 
 * @author Jeff Tang
 * 
 */
public class SQLNameRedefineException extends SQLParseException {
	private static final long serialVersionUID = 1L;

	public SQLNameRedefineException(int line, int col, String name) {
		super(line, col, "�����ظ����� '" + name + "'");
	}

	public SQLNameRedefineException(String name) {
		this(0, 0, name);
	}

	@Override
	public SQLErrorCode getErrorCode() {
		return SQLErrorCode.NAMED_REDEFINED;
	}
}
