package org.eclipse.jt.core.impl;

class NSearchedCaseWhen implements TextLocalizable {
	public static final NSimpleCaseWhen EMPTY = new NSimpleCaseWhen(
			NValueExpr.EMPTY, NValueExpr.EMPTY);

	public final NConditionExpr condition;
	public final NValueExpr returnValue;

	public NSearchedCaseWhen(NConditionExpr condition, NValueExpr returnValue) {
		this.condition = condition;
		this.returnValue = returnValue;
	}

	public int startLine() {
		return this.condition.startLine();
	}

	public int startCol() {
		return this.condition.startCol();
	}

	public int endLine() {
		return this.condition.endLine();
	}

	public int endCol() {
		return this.condition.endCol();
	}
}
