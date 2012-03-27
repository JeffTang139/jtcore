package org.eclipse.jt.core.spi.sql;

/**
 * 找不到ORM异常
 * 
 * @author Jeff Tang
 * 
 */
public class SQLOrmNotFoundException extends SQLParseException {
	private static final long serialVersionUID = 1L;

	public SQLOrmNotFoundException(int line, int col, String orm) {
		super(line, col, "找不到ORM定义 '" + orm + "'");
	}

	public SQLOrmNotFoundException(String orm) {
		this(0, 0, orm);
	}

	@Override
	public SQLErrorCode getErrorCode() {
		return SQLErrorCode.ORM_NOT_FOUND;
	}
}
