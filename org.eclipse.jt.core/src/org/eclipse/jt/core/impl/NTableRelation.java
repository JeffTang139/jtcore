package org.eclipse.jt.core.impl;

class NTableRelation implements TextLocalizable {
	public static final NTableRelation EMPTY = new NTableRelation(
			TString.EMPTY, TString.EMPTY, NConditionExpr.EMPTY);

	public final TString name;
	public final TString target;
	public final NConditionExpr expr;

	public NTableRelation(TString name, TString target, NConditionExpr expr) {
		this.name = name;
		this.target = target;
		this.expr = expr;
	}

	public int startLine() {
		return this.name.startLine();
	}

	public int startCol() {
		return this.name.startCol();
	}

	public int endLine() {
		return this.expr.endLine();
	}

	public int endCol() {
		return this.expr.endCol();
	}
}
