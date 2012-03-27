package org.eclipse.jt.core.spi.sql;

/**
 * �Ҳ��������쳣
 * 
 * @author Jeff Tang
 * 
 */
public class SQLHierarchyNotFoundException extends SQLParseException {
	private static final long serialVersionUID = 1L;

	public SQLHierarchyNotFoundException(int line, int col, String hier) {
		super(line, col, "�Ҳ������ζ��� '" + hier + "'");
	}

	public SQLHierarchyNotFoundException(String hier) {
		this(0, 0, hier);
	}

	@Override
	public SQLErrorCode getErrorCode() {
		return SQLErrorCode.HIERARCHY_NOT_FOUND;
	}
}
