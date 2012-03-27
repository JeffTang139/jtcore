package org.eclipse.jt.core.impl;


/**
 * H_AID表达式节点
 * 
 * @author Jeff Tang
 * 
 */
class NHaidExpr implements NValueExpr {
	private final int startLine;
	private final int startCol;
	private final int endLine;
	private final int endCol;
	private final int hashCode;

	public final TString source;
	public final TString path;
	public final NValueExpr offset;
	public final boolean relOrAbs;

	public NHaidExpr(Token start, Token end, TString source, TString path,
			NValueExpr offset, boolean relOrAbs/*
												 * true表示相对，false表示绝对
												 */) {
		this.source = source;
		this.path = path;
		this.offset = offset;
		this.relOrAbs = relOrAbs;
		this.startLine = start.line;
		this.startCol = start.col;
		this.endLine = end.line;
		this.endCol = end.col + end.length;
		int hashCode = (source.hashCode() << 8) ^ path.hashCode();
		if (offset != null) {
			hashCode <<= 8;
			hashCode ^= offset.hashCode();
			hashCode ^= relOrAbs ? Boolean.TRUE.hashCode() : Boolean.FALSE
					.hashCode();
		}
		this.hashCode = hashCode;
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
		visitor.visitHaidExpr(visitorContext, this);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof NHaidExpr) {
			NHaidExpr expr = (NHaidExpr) obj;
			if (!expr.source.equals(this.source)
					|| !expr.path.equals(this.path)) {
				return false;
			}
			if (expr.offset == null) {
				return this.offset == null;
			}
			return expr.offset.equals(this.offset)
					&& expr.relOrAbs == this.relOrAbs;
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
