package org.eclipse.jt.core.impl;

class SQLServerSelectColumnBuffer extends SQLServerExprBuffer {
	final String alias;

	public SQLServerSelectColumnBuffer(String alias) {
		this.alias = alias;
	}
}
