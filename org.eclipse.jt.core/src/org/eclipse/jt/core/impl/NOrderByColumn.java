package org.eclipse.jt.core.impl;

/**
 * ORDER BYÁÐ½Úµã
 * 
 * @author Jeff Tang
 * 
 */
class NOrderByColumn implements TextLocalizable {
	private final int endLine;
	private final int endCol;

	public final TString columnName;
	public final NValueExpr column;
	public final boolean asc;

	public NOrderByColumn(TextLocalizable end, NValueExpr column, boolean asc) {
		this.column = column;
		this.asc = asc;
		this.columnName = null;
		this.endLine = end.endLine();
		this.endCol = end.endCol();
	}

	public NOrderByColumn(TextLocalizable end, TString columnName, boolean asc) {
		this.column = null;
		this.asc = asc;
		this.columnName = columnName;
		this.endLine = end.endLine();
		this.endCol = end.endCol();
	}

	public int startLine() {
		return this.column.startLine();
	}

	public int startCol() {
		return this.column.startCol();
	}

	public int endLine() {
		return this.endLine;
	}

	public int endCol() {
		return this.endCol;
	}
}
