package org.eclipse.jt.core.impl;

/**
 * UPDATE×Ö¶Î¸³Öµ½Úµã
 * 
 * @author Jeff Tang
 * 
 */
class NUpdateColumnValue implements TextLocalizable {
	public static final NUpdateColumnValue EMPTY = new NUpdateColumnValue(
			TString.EMPTY, NValueExpr.EMPTY);

	public final TString column;
	public final NValueExpr value;

	public NUpdateColumnValue(TString column, NValueExpr value) {
		this.column = column;
		this.value = value;
	}

	public int startLine() {
		return this.column.line;
	}

	public int startCol() {
		return this.column.col;
	}

	public int endLine() {
		return this.value.endLine();
	}

	public int endCol() {
		return this.value.endCol();
	}
}
