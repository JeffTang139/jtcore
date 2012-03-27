package org.eclipse.jt.core.spi.sql;

/**
 * 表定义没有找到
 * 
 * @author Jeff Tang
 * 
 */
public class SQLTableNotFoundException extends SQLParseException {
	private static final long serialVersionUID = 1L;

	public SQLTableNotFoundException(int line, int col, String table) {
		super(line, col, "找不到表定义 '" + table + "'");
	}

	@Override
	public SQLErrorCode getErrorCode() {
		return SQLErrorCode.TABLE_NOT_FOUND;
	}
}
