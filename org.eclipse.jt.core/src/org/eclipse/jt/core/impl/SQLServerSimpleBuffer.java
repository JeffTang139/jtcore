package org.eclipse.jt.core.impl;

import java.util.List;

class SQLServerSimpleBuffer extends SqlBuffer {
	static final SQLServerSimpleBuffer EXIT = new SQLServerSimpleBuffer(
			"return");
	static final SQLServerSimpleBuffer BREAK = new SQLServerSimpleBuffer(
			"break");

	final String keyword;

	public SQLServerSimpleBuffer(String keyword) {
		this.keyword = keyword;
	}

	public int getLength() {
		return this.keyword.length() + 1;
	}

	public void writeTo(SqlStringBuffer sql, List<ParameterReserver> args) {
		sql.append(this.keyword).append(';');
	}
}
