package org.eclipse.jt.core.spi.sql;

/**
 * ����Ϊ�����쳣
 * 
 * @author Jeff Tang
 * 
 */
public class SQLAliasUndefinedException extends SQLParseException {

	private static final long serialVersionUID = 1L;

	public SQLAliasUndefinedException(int line, int col, String alias) {
		super(line, col, "����δ���� '" + alias + "'");
	}

	@Override
	public SQLErrorCode getErrorCode() {
		return SQLErrorCode.ALIAS_UNDEFINED;
	}
}
