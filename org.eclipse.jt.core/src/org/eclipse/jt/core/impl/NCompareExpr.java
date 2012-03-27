package org.eclipse.jt.core.impl;

/**
 * 比较运算节点
 * 
 * @author Jeff Tang
 * 
 */
class NCompareExpr implements NConditionExpr {
	public enum Operator {
		GT {
			@Override
			public String toString() {
				return ">";
			}
		},
		LT {
			@Override
			public String toString() {
				return "<";
			}
		},
		GE {
			@Override
			public String toString() {
				return ">=";
			}
		},
		LE {
			@Override
			public String toString() {
				return "<=";
			}
		},
		EQ {
			@Override
			public String toString() {
				return "=";
			}
		},
		NE {
			@Override
			public String toString() {
				return "<>";
			}
		};
	}

	public final Operator op;
	public final NValueExpr left;
	public final NValueExpr right;

	public NCompareExpr(Operator op, NValueExpr left, NValueExpr right) {
		this.op = op;
		this.left = left;
		this.right = right;
	}

	public int startLine() {
		return this.left.startLine();
	}

	public int startCol() {
		return this.left.startCol();
	}

	public int endLine() {
		return this.right.endLine();
	}

	public int endCol() {
		return this.right.endCol();
	}

	public <T> void accept(T visitorContext, SQLVisitor<T> visitor) {
		visitor.visitCompareExpr(visitorContext, this);
	}

	@Override
	public String toString() {
		return RenderVisitor.render(this);
	}
}
