package org.eclipse.jt.core.impl;


/**
 * DESCENDANTOF表达式节点
 * 
 * @author Jeff Tang
 * 
 */
class NDescendantOfExpr extends NHierarchyExpr {
	public final NValueExpr diff;
	public final boolean leOrEq;

	public NDescendantOfExpr(TString left, TString right, TString rel,
			NValueExpr diff, boolean leOrEq) {
		super(Keywords.DESCENDANTOF, left, right, rel);
		this.diff = diff;
		this.leOrEq = leOrEq;
	}

	@Override
	public int endLine() {
		if (this.diff != null)
			return this.diff.endLine();
		return super.endLine();
	}

	@Override
	public int endCol() {
		if (this.diff != null)
			return this.diff.endCol();
		return super.endCol();
	}
}
