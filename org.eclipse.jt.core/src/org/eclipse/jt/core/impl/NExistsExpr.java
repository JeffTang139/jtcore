package org.eclipse.jt.core.impl;

/**
 * EXISTSÎ½´Ê½Úµã
 * 
 * @author Jeff Tang
 * 
 */
class NExistsExpr implements NConditionExpr {
	private final int startLine;
	private final int startCol;
	private final int endLine;
	private final int endCol;

	public final NQuerySpecific query;

	public NExistsExpr(Token start, Token end, NQuerySpecific query) {
		this.query = query;
		this.startLine = start.line;
		this.startCol = start.col + start.length;
		this.endLine = end.line;
		this.endCol = end.col + end.length;
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
		visitor.visitExistsExpr(visitorContext, this);
	}

	@Override
	public String toString() {
		return RenderVisitor.render(this);
	}
}
