package org.eclipse.jt.core.impl;

/**
 * WHERE½Úµã
 * 
 * @author Jeff Tang
 * 
 */
class NWhere implements TextLocalizable {
	private final int startLine;
	private final int startCol;

	public final NConditionExpr expr;
	public final TString cursor;

	public NWhere(Token start, NConditionExpr expr) {
		this.expr = expr;
		this.cursor = null;
		this.startLine = start.line;
		this.startCol = start.col;
	}

	public NWhere(Token start, TString cursor) {
		this.cursor = cursor;
		this.expr = null;
		this.startLine = start.line;
		this.startCol = start.col;
	}

	public int startLine() {
		return this.startLine;
	}

	public int startCol() {
		return this.startCol;
	}

	public int endLine() {
		if (this.expr != null) {
			return this.expr.endLine();
		}
		return this.cursor.endLine();
	}

	public int endCol() {
		if (this.expr != null) {
			return this.expr.endCol();
		}
		return this.cursor.endCol();
	}
}
