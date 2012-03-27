package org.eclipse.jt.core.spi.sql;

/**
 * ���ϵ�Ҳ����쳣
 * 
 * @author Jeff Tang
 * 
 */
public class SQLRelationNotFoundException extends SQLParseException {
	private static final long serialVersionUID = 1L;

	public SQLRelationNotFoundException(int line, int col, String name) {
		super(line, col, "�Ҳ������ϵ'" + name + "'");
	}

	public SQLRelationNotFoundException(String name) {
		this(0, 0, name);
	}

	@Override
	public SQLErrorCode getErrorCode() {
		return SQLErrorCode.RELATION_NOT_FOUND;
	}
}
