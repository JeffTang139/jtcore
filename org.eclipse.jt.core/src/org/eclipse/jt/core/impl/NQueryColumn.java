package org.eclipse.jt.core.impl;

/**
 * SELECT输出列节点
 * 
 * @author Jeff Tang
 * 
 */
class NQueryColumn implements TextLocalizable {
	public static final NQueryColumn EMPTY = new NQueryColumn(NValueExpr.EMPTY,
			TString.EMPTY);

	public final NValueExpr expr;
	public final TString alias;

	public NQueryColumn(NValueExpr expr, TString alias) {
		this.alias = alias;
		this.expr = expr;
	}

	public int startLine() {
		return this.expr.startLine();
	}

	public int startCol() {
		return this.expr.startCol();
	}

	public int endLine() {
		return this.alias != null ? this.alias.line : this.expr.endLine();
	}

	public int endCol() {
		return this.alias != null ? (this.alias.col + this.alias.length)
				: this.expr.endCol();
	}
}
