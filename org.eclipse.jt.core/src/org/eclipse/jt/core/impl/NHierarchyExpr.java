package org.eclipse.jt.core.impl;


/**
 * 级次运算节点
 * 
 * @author Jeff Tang
 * 
 */
class NHierarchyExpr implements NConditionExpr {
	public enum Keywords {
		CHILDOF, PARENTOF, ANCESTOROF, DESCENDANTOF, UNKNOWN
	}

	public final TString left;
	public final TString right;
	public final TString rel;
	public final Keywords keyword;

	public NHierarchyExpr(Keywords keyword, TString left, TString right,
			TString rel) {
		this.keyword = keyword;
		this.left = left;
		this.right = right;
		this.rel = rel;
	}

	public int startLine() {
		return this.left.line;
	}

	public int startCol() {
		return this.left.col;
	}

	public int endLine() {
		return this.right.line;
	}

	public int endCol() {
		return this.right.col + this.right.length;
	}

	public <T> void accept(T visitorContext, SQLVisitor<T> visitor) {
		visitor.visitHierarchyExpr(visitorContext, this);
	}

	@Override
	public String toString() {
		return RenderVisitor.render(this);
	}
}
