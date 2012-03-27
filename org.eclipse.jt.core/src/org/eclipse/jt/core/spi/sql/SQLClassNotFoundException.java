package org.eclipse.jt.core.spi.sql;

/**
 * 类型找不到异常
 * 
 * @author Jeff Tang
 * 
 */
public class SQLClassNotFoundException extends SQLParseException {
	private static final long serialVersionUID = 1L;

	public SQLClassNotFoundException(int line, int col, String className) {
		super(line, col, "找不到类型定义 '" + className + "'");
	}

	public SQLClassNotFoundException(String className) {
		this(0, 0, className);
	}

	@Override
	public SQLErrorCode getErrorCode() {
		return SQLErrorCode.CLASS_NOT_FOUND;
	}
}
