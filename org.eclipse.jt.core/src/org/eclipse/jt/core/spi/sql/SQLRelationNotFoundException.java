package org.eclipse.jt.core.spi.sql;

/**
 * 表关系找不到异常
 * 
 * @author Jeff Tang
 * 
 */
public class SQLRelationNotFoundException extends SQLParseException {
	private static final long serialVersionUID = 1L;

	public SQLRelationNotFoundException(int line, int col, String name) {
		super(line, col, "找不到表关系'" + name + "'");
	}

	public SQLRelationNotFoundException(String name) {
		this(0, 0, name);
	}

	@Override
	public SQLErrorCode getErrorCode() {
		return SQLErrorCode.RELATION_NOT_FOUND;
	}
}
