package org.eclipse.jt.core.impl;


/**
 * H_LV表达式节点
 * 
 * @author Jeff Tang
 * 
 */
class NHlvExpr implements NValueExpr {
	private final int startLine;
	private final int startCol;
	private final int endLine;
	private final int endCol;

	public final TString source;
	public final TString path;

	public NHlvExpr(Token start, Token end, TString source, TString path) {
		this.source = source;
		this.path = path;
		this.startLine = start.line;
		this.startCol = start.col;
		this.endLine = end.line;
		this.endCol = end.col + end.length;
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
		visitor.visitHlvExpr(visitorContext, this);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof NHlvExpr) {
			NHlvExpr expr = (NHlvExpr) obj;
			return expr.source.equals(this.source)
					&& expr.path.equals(this.path);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return (this.source.hashCode() >> 8) ^ this.path.hashCode();
	}

	@Override
	public String toString() {
		return RenderVisitor.render(this);
	}
}
