package org.eclipse.jt.core.impl;


/**
 * IN表达式节点
 * 
 * @author Jeff Tang
 * 
 */
class NInExpr implements NConditionExpr {
	public final boolean not;
	public final NValueExpr value;
	public final NInExprParam param;

	public NInExpr(boolean not, NValueExpr value, NInExprParam param) {
		this.not = not;
		this.value = value;
		this.param = param;
	}

	public int startLine() {
		return this.value.startLine();
	}

	public int startCol() {
		return this.value.startCol();
	}

	public int endLine() {
		return this.param.endLine();
	}

	public int endCol() {
		return this.param.endCol();
	}

	public <T> void accept(T visitorContext, SQLVisitor<T> visitor) {
		visitor.visitInExpr(visitorContext, this);
	}

	@Override
	public String toString() {
		return RenderVisitor.render(this);
	}
}
