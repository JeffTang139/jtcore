package org.eclipse.jt.core.impl;


/**
 * 负运算表达式节点
 * 
 * @author Jeff Tang
 * 
 */
class NNegativeExpr implements NValueExpr {
	private final int hashCode;
	public final NValueExpr left;

	public NNegativeExpr(NValueExpr left) {
		this.left = left;
		int hashCode = 11387;
		if (left != null) {
			hashCode ^= left.hashCode();
		}
		this.hashCode = hashCode;
	}

	public int startLine() {
		return this.left.startLine();
	}

	public int startCol() {
		return this.left.startCol();
	}

	public int endLine() {
		return this.left.endLine();
	}

	public int endCol() {
		return this.left.endCol();
	}

	public <T> void accept(T visitorContext, SQLVisitor<T> visitor) {
		visitor.visitNegativeExpr(visitorContext, this);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof NNegativeExpr) {
			NNegativeExpr expr = (NNegativeExpr) obj;
			if (expr.left == null) {
				return this.left == null;
			}
			return expr.left.equals(this.left);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.hashCode;
	}

	@Override
	public String toString() {
		return RenderVisitor.render(this);
	}
}
