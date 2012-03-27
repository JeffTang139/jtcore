package org.eclipse.jt.core.spi.sql;

/**
 * δ��������쳣 (˵��ԴSQL�а����Ƿ���ʶ��)
 * 
 * @author Jeff Tang
 * 
 */
public class SQLTokenUndefinedException extends SQLParseException {
	private static final long serialVersionUID = 1L;

	@Override
	public SQLErrorCode getErrorCode() {
		return SQLErrorCode.TOKEN_UNDEFINED;
	}

	public SQLTokenUndefinedException(int line, int col, String token) {
		super(line, col, "�޷�ʶ����� '" + token + "'");
	}
}
