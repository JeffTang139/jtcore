package org.eclipse.jt.core.spi.sql;

/**
 * �Ҳ�ָ�������쳣
 * 
 * @author Jeff Tang
 * 
 */
public class SQLColumnNotFoundException extends SQLParseException {
	private static final long serialVersionUID = 1L;

	public SQLColumnNotFoundException(int line, int col, String column) {
		super(line, col, "�Ҳ����� '" + column + "'");
	}

	public SQLColumnNotFoundException(String column) {
		this(0, 0, column);
	}

	@Override
	public SQLErrorCode getErrorCode() {
		return SQLErrorCode.COLUMN_NOT_FOUND;
	}
}
