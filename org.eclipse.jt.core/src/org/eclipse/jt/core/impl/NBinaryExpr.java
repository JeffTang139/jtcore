package org.eclipse.jt.core.impl;

/**
 * 算术表达式节点
 * 
 * @author Jeff Tang
 * 
 */
class NBinaryExpr implements NValueExpr {
	public enum Operator {
		COMBINE {
			@Override
			public String toString() {
				return "||";
			}

			@Override
			public int getPrec() {
				return 1;
			}
		},
		ADD {
			@Override
			public String toString() {
				return "+";
			}

			@Override
			public int getPrec() {
				return 1;
			}
		},
		SUB {
			@Override
			public String toString() {
				return "-";
			}

			@Override
			public int getPrec() {
				return 1;
			}
		},
		MUL {
			@Override
			public String toString() {
				return "*";
			}

			@Override
			public int getPrec() {
				return 3;
			}
		},
		DIV {
			@Override
			public String toString() {
				return "/";
			}

			@Override
			public int getPrec() {
				return 3;
			}
		},
		MOD {
			@Override
			public String toString() {
				return "%";
			}

			@Override
			public int getPrec() {
				return 3;
			}
		};

		abstract int getPrec();
	}

	private final int hashCode;
	public final Operator op;
	public final NValueExpr left;
	public final NValueExpr right;

	public NBinaryExpr(Operator op, NValueExpr left, NValueExpr right) {
		this.op = op;
		this.left = left;
		this.right = right;
		int hashCode = this.op.hashCode();
		if (this.left != null) {
			hashCode <<= 8;
			hashCode ^= this.left.hashCode();
		}
		if (this.right != null) {
			hashCode <<= 8;
			hashCode ^= this.right.hashCode();
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
		return this.right.endLine();
	}

	public int endCol() {
		return this.right.endCol();
	}

	public <T> void accept(T visitorContext, SQLVisitor<T> visitor) {
		visitor.visitBinaryExpr(visitorContext, this);
	}

	static int getPrec(NValueExpr expr) {
		if (expr instanceof NBinaryExpr) {
			return ((NBinaryExpr) expr).op.getPrec();
		} else if (expr instanceof NNegativeExpr) {
			return 5;
		} else if (expr instanceof NQuerySpecific
				|| expr instanceof NSimpleCaseExpr) {
			return 0;
		}
		return 6;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof NBinaryExpr) {
			NBinaryExpr expr = (NBinaryExpr) obj;
			if (expr.op != this.op) {
				return false;
			}
			if (expr.left == null) {
				if (this.left != null) {
					return false;
				}
			} else if (!expr.left.equals(this.left)) {
				return false;
			}
			if (expr.right == null) {
				return this.right == null;
			}
			return expr.right.equals(this.right);
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
