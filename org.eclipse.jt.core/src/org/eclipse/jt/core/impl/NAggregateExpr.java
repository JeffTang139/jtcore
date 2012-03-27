package org.eclipse.jt.core.impl;

/**
 * 聚合运算表达式
 * 
 * @author Jeff Tang
 * 
 */
class NAggregateExpr implements NValueExpr {
	public enum Func {
		COUNT, SUM, AVG, MIN, MAX
	}

	private final int startLine;
	private final int startCol;
	private final int endLine;
	private final int endCol;
	private final int hashCode;

	public final Func func;
	public final NValueExpr expr;
	public final SetQuantifier quantifier;

	public NAggregateExpr(TSetFunction func, Token end, NValueExpr expr,
			SetQuantifier quantifier) {
		this.func = func.value;
		this.expr = expr;
		this.quantifier = quantifier;
		this.startLine = func.startLine();
		this.startCol = func.startCol();
		this.endLine = end.line;
		this.endCol = end.col + end.length;
		int hashCode = (func.hashCode() << 8) ^ quantifier.hashCode();
		if (expr != null) {
			hashCode <<= 8;
			hashCode ^= expr.hashCode();
		}
		this.hashCode = hashCode;
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
		visitor.visitAggregateExpr(visitorContext, this);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof NAggregateExpr) {
			NAggregateExpr expr = (NAggregateExpr) obj;
			if (expr.func != this.func) {
				return false;
			}
			if (expr.quantifier != this.quantifier) {
				return false;
			}
			if (expr.expr == null) {
				return this.expr == null;
			}
			return expr.expr.equals(this.expr);
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
