package org.eclipse.jt.core.impl;

/**
 * RETURNÓï¾ä
 * 
 * @author Jeff Tang
 * 
 */
class NReturnStmt implements NStatement {
	private final int startLine;
	private final int startCol;
	private final int endLine;
	private final int endCol;
	public final NValueExpr expr;

	public NReturnStmt(Token start, NValueExpr expr) {
		this.expr = expr;
		this.startLine = start.line;
		this.startCol = start.col;
		if (expr != null) {
			this.endLine = expr.endLine();
			this.endCol = expr.endCol();
		} else {
			this.endLine = start.line;
			this.endCol = start.col + start.length;
		}
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

	public <T> void accept(T visitorContext, SQLVisitor<T> visitor) {
		visitor.visitReturnStmt(visitorContext, this);
	}
}
