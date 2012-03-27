package org.eclipse.jt.core.impl;


/**
 * 级次路径运算节点
 * 
 * @author Jeff Tang
 * 
 */
class NPathExpr implements NConditionExpr {
	public final static NPathExpr EMPTY = new NPathExpr(
			NHierarchyExpr.Keywords.UNKNOWN, TString.EMPTY, TString.EMPTY,
			TString.EMPTY, TString.EMPTY, null);

	public final TString t1;
	public final TString t2;
	public final TString f1;
	public final TString f2;
	public final NHierarchyExpr.Keywords keyword;
	public final NValueExpr diff;

	public NPathExpr(NHierarchyExpr.Keywords keyword, TString t1, TString t2,
			TString f1, TString f2, NValueExpr diff) {
		this.keyword = keyword;
		this.t1 = t1;
		this.t2 = t2;
		this.f1 = f1;
		this.f2 = f2;
		this.diff = diff;
	}

	public int startLine() {
		return this.t1.line;
	}

	public int startCol() {
		return this.t1.col;
	}

	public int endLine() {
		if (this.diff != null) {
			return this.diff.endLine();
		}
		return this.f2.line;
	}

	public int endCol() {
		if (this.diff != null) {
			return this.diff.endCol();
		}
		return this.f2.col + this.f2.length;
	}

	public <T> void accept(T visitorContext, SQLVisitor<T> visitor) {
		visitor.visitPathExpr(visitorContext, this);
	}

	@Override
	public String toString() {
		return RenderVisitor.render(this);
	}
}
