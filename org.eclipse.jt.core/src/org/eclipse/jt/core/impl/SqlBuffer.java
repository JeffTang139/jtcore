package org.eclipse.jt.core.impl;

public abstract class SqlBuffer implements ISqlBuffer {
	@Override
	public String toString() {
		SqlStringBuffer sql = new SqlStringBuffer();
		writeTo(sql, null);
		return sql.toString();
	}
}
