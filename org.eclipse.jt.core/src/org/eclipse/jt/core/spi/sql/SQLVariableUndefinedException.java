package org.eclipse.jt.core.spi.sql;

/**
 * ʹ����û�ж���ı�����
 * 
 * @author Jeff Tang
 * 
 */
public class SQLVariableUndefinedException extends SQLParseException {
	private static final long serialVersionUID = 1L;

	public SQLVariableUndefinedException(int line, int col, String name) {
		super(line, col, "����δ���� '" + name + "'");
	}

	@Override
	public SQLErrorCode getErrorCode() {
		return SQLErrorCode.VAR_UNDEFINED;
	}
}
