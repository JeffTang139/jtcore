package org.eclipse.jt.core.impl;


/**
 * HAVING½Úµã
 * 
 * @author Jeff Tang
 * 
 */
class NHaving implements TextLocalizable {
	private final int startLine;
	private final int startCol;
	private final int endLine;
	private final int endCol;

	public final NConditionExpr expr;

	public NHaving(Token start, NConditionExpr expr) {
		this.expr = expr;
		this.startLine = start.line;
		this.startCol = start.col;
		this.endLine = expr.endLine();
		this.endCol = expr.endCol();
	}

	public int startLine() {
		return this.startLine;
	}

	public int startCol() {
		return this.startCol;
	}

	public int endLine() {
		return this.endLine;
	}

	public int endCol() {
		return this.endCol;
	}
}
