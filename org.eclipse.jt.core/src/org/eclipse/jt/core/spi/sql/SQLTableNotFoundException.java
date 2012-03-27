package org.eclipse.jt.core.spi.sql;

/**
 * ����û���ҵ�
 * 
 * @author Jeff Tang
 * 
 */
public class SQLTableNotFoundException extends SQLParseException {
	private static final long serialVersionUID = 1L;

	public SQLTableNotFoundException(int line, int col, String table) {
		super(line, col, "�Ҳ������� '" + table + "'");
	}

	@Override
	public SQLErrorCode getErrorCode() {
		return SQLErrorCode.TABLE_NOT_FOUND;
	}
}
