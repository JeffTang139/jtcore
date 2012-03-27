package org.eclipse.jt.core.spi.sql;

/**
 * �����ظ��쳣
 * 
 * @author Jeff Tang
 * 
 */
public class SQLAliasDuplicateException extends SQLParseException {
	private static final long serialVersionUID = 1L;

	public SQLAliasDuplicateException(int line, int col, String alias) {
		super(line, col, "�����ظ����� '" + alias + "'");
	}

	public SQLAliasDuplicateException(String alias) {
		this(0, 0, alias);
	}

	@Override
	public SQLErrorCode getErrorCode() {
		return SQLErrorCode.ALIAS_DUPLICATE;
	}
}
