package org.eclipse.jt.core.impl;


/**
 * 字符串比较谓词节点
 * 
 * @author Jeff Tang
 * 
 */
class NStrCompareExpr implements NConditionExpr {
	public enum Keywords {
		STARTS_WITH, ENDS_WITH, CONTAINS, LIKE
	}

	public final NValueExpr first;
	public final NValueExpr second;
	public final boolean not;
	public final Keywords keyword;

	public NStrCompareExpr(Keywords keyword, NValueExpr first,
			NValueExpr second, boolean not) {
		this.first = first;
		this.second = second;
		this.not = not;
		this.keyword = keyword;
	}

	public int startLine() {
		return this.first.startLine();
	}

	public int startCol() {
		return this.first.startCol();
	}

	public int endLine() {
		return this.second.endLine();
	}

	public int endCol() {
		return this.second.endCol();
	}

	public <T> void accept(T visitorContext, SQLVisitor<T> visitor) {
		visitor.visitStrCompareExpr(visitorContext, this);
	}

	@Override
	public String toString() {
		return RenderVisitor.render(this);
	}
}
