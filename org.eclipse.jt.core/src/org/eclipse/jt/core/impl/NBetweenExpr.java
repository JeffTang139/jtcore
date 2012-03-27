package org.eclipse.jt.core.impl;


/**
 * BETWEEN表达式节点
 * 
 * @author Jeff Tang
 * 
 */
class NBetweenExpr implements NConditionExpr {
	public final boolean not;
	public final NValueExpr value;
	public final NValueExpr left;
	public final NValueExpr right;

	public NBetweenExpr(boolean not, NValueExpr value, NValueExpr left,
			NValueExpr right) {
		this.not = not;
		this.value = value;
		this.left = left;
		this.right = right;
	}

	public int startLine() {
		return this.value.startLine();
	}

	public int startCol() {
		return this.value.startCol();
	}

	public int endLine() {
		return this.right.endLine();
	}

	public int endCol() {
		return this.right.endCol();
	}

	public <T> void accept(T visitorContext, SQLVisitor<T> visitor) {
		visitor.visitBetweenExpr(visitorContext, this);
	}

	@Override
	public String toString() {
		return RenderVisitor.render(this);
	}
}
