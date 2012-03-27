package org.eclipse.jt.core.impl;


/**
 * 空表达式节点
 * 
 * @author Jeff Tang
 * 
 */
class NNullExpr implements NValueExpr {
	private final int startLine;
	private final int startCol;
	private final int endLine;
	private final int endCol;

	public NNullExpr(Token token) {
		this.startLine = token.line;
		this.startCol = token.col;
		this.endLine = token.line;
		this.endCol = token.col + token.length;
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
		visitor.visitNullExpr(visitorContext, this);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof NNullExpr) {
			return true;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return 904350;
	}
	
	@Override
	public String toString() {
		return RenderVisitor.render(this);
	}
}
