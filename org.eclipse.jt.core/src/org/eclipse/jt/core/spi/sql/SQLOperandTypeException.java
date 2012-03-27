package org.eclipse.jt.core.spi.sql;

/**
 * ���������Ͳ�ƥ���쳣
 * 
 * @author Jeff Tang
 * 
 */
public class SQLOperandTypeException extends SQLParseException {
	private static final long serialVersionUID = 1L;

	public SQLOperandTypeException(int line, int col, String message) {
		super(line, col, message);
	}

	public SQLOperandTypeException(String message) {
		super(0, 0, message);
	}

	@Override
	public SQLErrorCode getErrorCode() {
		return SQLErrorCode.OPERAND_TYPE_INVALID;
	}
}
