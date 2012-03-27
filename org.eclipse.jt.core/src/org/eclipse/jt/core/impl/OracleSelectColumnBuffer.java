package org.eclipse.jt.core.impl;

class OracleSelectColumnBuffer extends OracleExprBuffer {
	final String alias;

	public OracleSelectColumnBuffer(String alias) {
		this.alias = OracleExprBuffer.quote(alias);
	}
}
