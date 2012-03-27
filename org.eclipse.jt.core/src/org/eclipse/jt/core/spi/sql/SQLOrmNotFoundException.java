package org.eclipse.jt.core.spi.sql;

/**
 * �Ҳ���ORM�쳣
 * 
 * @author Jeff Tang
 * 
 */
public class SQLOrmNotFoundException extends SQLParseException {
	private static final long serialVersionUID = 1L;

	public SQLOrmNotFoundException(int line, int col, String orm) {
		super(line, col, "�Ҳ���ORM���� '" + orm + "'");
	}

	public SQLOrmNotFoundException(String orm) {
		this(0, 0, orm);
	}

	@Override
	public SQLErrorCode getErrorCode() {
		return SQLErrorCode.ORM_NOT_FOUND;
	}
}
