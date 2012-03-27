package org.eclipse.jt.core.spi.sql;

/**
 * ������������ʽ����ȷ�쳣
 * 
 * @author Jeff Tang
 * 
 */
public class SQLNumberFormatException extends SQLParseException {
	private static final long serialVersionUID = 1L;

	public SQLNumberFormatException(int line, int col, String token) {
		super(line, col, "��ֵ��ʽ����ȷ '" + token + "'");
	}

	@Override
	public SQLErrorCode getErrorCode() {
		return SQLErrorCode.VALUE_FORMAT;
	}
}
