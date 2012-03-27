package org.eclipse.jt.core.impl;


/**
 * CASE-WHEN½Úµã
 * 
 * @author Jeff Tang
 * 
 */
class NSimpleCaseWhen implements TextLocalizable {
	public static final NSimpleCaseWhen EMPTY = new NSimpleCaseWhen(
			NValueExpr.EMPTY, NValueExpr.EMPTY);

	public final NValueExpr value;
	public final NValueExpr returnValue;

	public NSimpleCaseWhen(NValueExpr value, NValueExpr returnValue) {
		this.value = value;
		this.returnValue = returnValue;
	}

	public int startLine() {
		if (this.value != null) {
			return this.value.startLine();
		}
		return 0;
	}

	public int startCol() {
		if (this.value != null) {
			return this.value.startCol();
		}
		return 0;
	}

	public int endLine() {
		if (this.returnValue != null) {
			return this.returnValue.endLine();
		}
		return 0;
	}

	public int endCol() {
		if (this.returnValue != null) {
			return this.returnValue.endCol();
		}
		return 0;
	}
}
