package org.eclipse.jt.core.spi.sql;

/**
 * 未定义符号异常 (说明源SQL中包含非法标识符)
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
		super(line, col, "无法识别符号 '" + token + "'");
	}
}
