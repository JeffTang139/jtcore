package org.eclipse.jt.core.spi.sql;

/**
 * 别名重复异常
 * 
 * @author Jeff Tang
 * 
 */
public class SQLAliasDuplicateException extends SQLParseException {
	private static final long serialVersionUID = 1L;

	public SQLAliasDuplicateException(int line, int col, String alias) {
		super(line, col, "别名重复定义 '" + alias + "'");
	}

	public SQLAliasDuplicateException(String alias) {
		this(0, 0, alias);
	}

	@Override
	public SQLErrorCode getErrorCode() {
		return SQLErrorCode.ALIAS_DUPLICATE;
	}
}
