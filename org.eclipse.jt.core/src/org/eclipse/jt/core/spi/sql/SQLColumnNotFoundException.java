package org.eclipse.jt.core.spi.sql;

/**
 * 找不指定的列异常
 * 
 * @author Jeff Tang
 * 
 */
public class SQLColumnNotFoundException extends SQLParseException {
	private static final long serialVersionUID = 1L;

	public SQLColumnNotFoundException(int line, int col, String column) {
		super(line, col, "找不到列 '" + column + "'");
	}

	public SQLColumnNotFoundException(String column) {
		this(0, 0, column);
	}

	@Override
	public SQLErrorCode getErrorCode() {
		return SQLErrorCode.COLUMN_NOT_FOUND;
	}
}
