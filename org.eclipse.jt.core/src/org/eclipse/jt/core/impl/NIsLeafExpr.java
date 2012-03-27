package org.eclipse.jt.core.impl;

/**
 * IS LEAF½Úµã
 * 
 * @author Jeff Tang
 * 
 */
class NIsLeafExpr implements NConditionExpr {
	public final TString left;
	public final TString hier;

	public NIsLeafExpr(TString left, TString hier) {
		this.left = left;
		this.hier = hier;
	}

	public int startLine() {
		return this.left.line;
	}

	public int startCol() {
		return this.left.col;
	}

	public int endLine() {
		return this.hier.line;
	}

	public int endCol() {
		return this.hier.col + this.hier.length;
	}

	public <T> void accept(T visitorContext, SQLVisitor<T> visitor) {
		visitor.visitIsLeafExpr(visitorContext, this);
	}

	@Override
	public String toString() {
		return RenderVisitor.render(this);
	}
}
