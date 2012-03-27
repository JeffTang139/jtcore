package org.eclipse.jt.core.impl;

/**
 * LOOPÓï¾ä
 * 
 * @author Jeff Tang
 * 
 */
class NLoopStmt implements NStatement {
	private final int startLine;
	private final int startCol;
	public final NStatement stmt;

	public NLoopStmt(Token start, NStatement stmt) {
		this.stmt = stmt;
		this.startLine = start.line;
		this.startCol = start.col + start.length;
	}

	public int startLine() {
		return this.startLine;
	}

	public int startCol() {
		return this.startCol;
	}

	public int endLine() {
		return this.stmt.endLine();
	}

	public int endCol() {
		return this.stmt.endCol();
	}

	public <T> void accept(T visitorContext, SQLVisitor<T> visitor) {
		visitor.visitLoopStmt(visitorContext, this);
	}
	
	@Override
	public String toString() {
		return RenderVisitor.render(this);
	}
}
