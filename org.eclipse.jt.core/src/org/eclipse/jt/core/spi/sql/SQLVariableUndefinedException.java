package org.eclipse.jt.core.spi.sql;

/**
 * 使用了没有定义的变量名
 * 
 * @author Jeff Tang
 * 
 */
public class SQLVariableUndefinedException extends SQLParseException {
	private static final long serialVersionUID = 1L;

	public SQLVariableUndefinedException(int line, int col, String name) {
		super(line, col, "变量未定义 '" + name + "'");
	}

	@Override
	public SQLErrorCode getErrorCode() {
		return SQLErrorCode.VAR_UNDEFINED;
	}
}
