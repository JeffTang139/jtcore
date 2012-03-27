package org.eclipse.jt.core.impl;

/**
 * BREAKÓï¾ä
 * 
 * @author Jeff Tang
 * 
 */
class NBreakStmt implements NStatement {
	private final int startLine;
	private final int startCol;
	private final int endLine;
	private final int endCol;

	public NBreakStmt(Token start) {
		this.startLine = start.line;
		this.startCol = start.col;
		this.endLine = start.line;
		this.endCol = start.col + start.length;
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
		visitor.visitBreakStmt(visitorContext, this);
	}
}
