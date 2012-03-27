package org.eclipse.jt.core.impl;

/**
 * WHILEÓï¾ä
 * 
 * @author Jeff Tang
 * 
 */
class NWhileStmt implements NStatement {
	private final int startLine;
	private final int startCol;
	public final NConditionExpr condition;
	public final NStatement stmt;

	public NWhileStmt(Token start, NConditionExpr condition, NStatement stmt) {
		this.condition = condition;
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
		visitor.visitWhileStmt(visitorContext, this);
	}
	
	@Override
	public String toString() {
		return RenderVisitor.render(this);
	}
}
