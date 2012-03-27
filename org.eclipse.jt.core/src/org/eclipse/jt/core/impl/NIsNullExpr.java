package org.eclipse.jt.core.impl;


/**
 * IS表达式节点
 * 
 * @author Jeff Tang
 * 
 */
class NIsNullExpr implements NConditionExpr {
	private final int endLine;
	private final int endCol;

	public final NValueExpr value;
	public final boolean not;

	public NIsNullExpr(Token end, boolean not, NValueExpr value) {
		this.not = not;
		this.value = value;
		this.endLine = end.line;
		this.endCol = end.col + end.length;
	}

	public int startLine() {
		return this.value.startLine();
	}

	public int startCol() {
		return this.value.startCol();
	}

	public int endLine() {
		return this.endLine;
	}

	public int endCol() {
		return this.endCol;
	}

	public <T> void accept(T visitorContext, SQLVisitor<T> visitor) {
		visitor.visitIsNullExpr(visitorContext, this);
	}

	@Override
	public String toString() {
		return RenderVisitor.render(this);
	}
}
