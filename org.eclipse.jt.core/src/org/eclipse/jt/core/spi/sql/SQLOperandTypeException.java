package org.eclipse.jt.core.spi.sql;

/**
 * 操作数类型不匹配异常
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
