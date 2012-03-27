package org.eclipse.jt.core.spi.sql;

/**
 * 变量名重复异常
 * 
 * @author Jeff Tang
 * 
 */
public class SQLVariableDuplicateException extends SQLParseException {
	private static final long serialVersionUID = 1L;

	public SQLVariableDuplicateException(int line, int col, String name) {
		super(line, col, "变量名重复定义 '" + name + "'");
	}

	public SQLVariableDuplicateException(String name) {
		this(0, 0, name);
	}

	@Override
	public SQLErrorCode getErrorCode() {
		return SQLErrorCode.VAR_DUPLICATE;
	}
}
