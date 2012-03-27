package org.eclipse.jt.core.impl;


/**
 * 逻辑表达式节点
 * 
 * @author Jeff Tang
 * 
 */
class NLogicalExpr implements NConditionExpr {
	public enum Operator {
		AND {
			@Override
			public String toString() {
				return "AND";
			}

			@Override
			public int getPrec() {
				return 3;
			}
		},
		OR {
			@Override
			public String toString() {
				return "OR";
			}

			@Override
			public int getPrec() {
				return 1;
			}
		},
		NOT {
			@Override
			public String toString() {
				return "NOT";
			}

			@Override
			public int getPrec() {
				return 5;
			}
		};

		abstract int getPrec();
	}

	public final Operator op;
	public final NConditionExpr left;
	public final NConditionExpr right;

	public NLogicalExpr(Operator op, NConditionExpr left, NConditionExpr right) {
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
		if (this.op == Operator.NOT) {
			return this.left.endLine();
		}
		return this.right.endLine();
	}

	public int endCol() {
		if (this.op == Operator.NOT) {
			return this.left.endCol();
		}
		return this.right.endCol();
	}

	public <T> void accept(T visitorContext, SQLVisitor<T> visitor) {
		visitor.visitLogicalExpr(visitorContext, this);
	}

	static int getPrec(NConditionExpr expr) {
		if (expr instanceof NLogicalExpr) {
			return ((NLogicalExpr) expr).op.getPrec();
		}
		return 7;
	}

	@Override
	public String toString() {
		return RenderVisitor.render(this);
	}
}
