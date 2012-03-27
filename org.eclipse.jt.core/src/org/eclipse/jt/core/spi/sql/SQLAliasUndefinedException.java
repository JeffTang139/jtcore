package org.eclipse.jt.core.spi.sql;

/**
 * 别名为定义异常
 * 
 * @author Jeff Tang
 * 
 */
public class SQLAliasUndefinedException extends SQLParseException {

	private static final long serialVersionUID = 1L;

	public SQLAliasUndefinedException(int line, int col, String alias) {
		super(line, col, "别名未定义 '" + alias + "'");
	}

	@Override
	public SQLErrorCode getErrorCode() {
		return SQLErrorCode.ALIAS_UNDEFINED;
	}
}
