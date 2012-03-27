package org.eclipse.jt.core.impl;

import java.util.List;

class OracleSimpleBuffer extends SqlBuffer {
	static final OracleSimpleBuffer EXIT = new OracleSimpleBuffer("return");
	static final OracleSimpleBuffer BREAK = new OracleSimpleBuffer("break");

	final String keyword;

	public OracleSimpleBuffer(String keyword) {
		this.keyword = keyword;
	}

	public void writeTo(SqlStringBuffer sql, List<ParameterReserver> args) {
		sql.append(this.keyword).append(';');
	}
}
