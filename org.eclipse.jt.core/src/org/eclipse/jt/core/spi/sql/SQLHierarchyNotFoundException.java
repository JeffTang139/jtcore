package org.eclipse.jt.core.spi.sql;

/**
 * 找不到级次异常
 * 
 * @author Jeff Tang
 * 
 */
public class SQLHierarchyNotFoundException extends SQLParseException {
	private static final long serialVersionUID = 1L;

	public SQLHierarchyNotFoundException(int line, int col, String hier) {
		super(line, col, "找不到级次定义 '" + hier + "'");
	}

	public SQLHierarchyNotFoundException(String hier) {
		this(0, 0, hier);
	}

	@Override
	public SQLErrorCode getErrorCode() {
		return SQLErrorCode.HIERARCHY_NOT_FOUND;
	}
}
