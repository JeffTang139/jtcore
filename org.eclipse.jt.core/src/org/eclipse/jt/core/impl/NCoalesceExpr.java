package org.eclipse.jt.core.impl;

/**
 * COALESCE�����ڵ�
 * 
 * @author Jeff Tang
 * 
 */
class NCoalesceExpr implements NValueExpr {
	private final int startLine;
	private final int startCol;
	private final int endLine;
	private final int endCol;
	private final int hashCode;

	public final NValueExpr[] params;

	public NCoalesceExpr(Token start, Token end, NValueExpr[] params) {
		this.params = params;
		this.startLine = start.line;
		this.startCol = start.col;
		this.endLine = end.line;
		this.endCol = end.col + end.length;
		if (params == null) {
			this.hashCode = 0;
		} else {
			int hashCode = 0;
			for (NValueExpr expr : params) {
				hashCode <<= 1;
				hashCode ^= expr.hashCode();
			}
			this.hashCode = hashCode;
		}
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
		visitor.visitCoalesceExpr(visitorContext, this);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof NCoalesceExpr) {
			NCoalesceExpr expr = (NCoalesceExpr) obj;
			if (expr.params == null) {
				return this.params == null;
			}
			if (this.params == null || expr.params.length != this.params.length) {
				return false;
			}
			for (int i = 0, c = expr.params.length; i < c; i++) {
				NValueExpr a = expr.params[i];
				NValueExpr b = this.params[i];
				if (a == null && b != null || a != null && !a.equals(b)) {
					return false;
				}
			}
			return true;
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
