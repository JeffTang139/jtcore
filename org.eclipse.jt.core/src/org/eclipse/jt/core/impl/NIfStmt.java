package org.eclipse.jt.core.impl;

/**
 * IFÓï¾ä
 * 
 * @author Jeff Tang
 * 
 */
class NIfStmt implements NStatement {
	private final int startLine;
	private final int startCol;
	public final NConditionExpr condition;
	public final NStatement trueBranch;
	public final NStatement falseBranch;

	public NIfStmt(Token start, NConditionExpr c, NStatement t, NStatement f) {
		this.condition = c;
		this.trueBranch = t;
		this.falseBranch = f;
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
		if (this.falseBranch != null) {
			return this.falseBranch.endLine();
		}
		return this.trueBranch.endLine();
	}

	public int endCol() {
		if (this.falseBranch != null) {
			return this.falseBranch.endCol();
		}
		return this.trueBranch.endCol();
	}

	public <T> void accept(T visitorContext, SQLVisitor<T> visitor) {
		visitor.visitIfStmt(visitorContext, this);
	}

	@Override
	public String toString() {
		return RenderVisitor.render(this);
	}
}
