package org.eclipse.jt.core.impl;

/**
 * 列引用节点
 * 
 * @author Jeff Tang
 * 
 */
class NColumnRefExpr implements NValueExpr {
	private final int hashCode;

	public final TString source;
	public final TString field;

	public NColumnRefExpr(TString field, TString source) {
		this.source = source;
		this.field = field;
		this.hashCode = source.hashCode() << 8 ^ field.hashCode();
	}

	public int startLine() {
		return this.source.line;
	}

	public int startCol() {
		return this.source.col;
	}

	public int endLine() {
		return this.field.line;
	}

	public int endCol() {
		return this.field.col + this.field.length;
	}

	public <T> void accept(T visitorContext, SQLVisitor<T> visitor) {
		visitor.visitColumnRefExpr(visitorContext, this);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof NColumnRefExpr) {
			NColumnRefExpr ref = (NColumnRefExpr) obj;
			return ref.source.equals(this.source)
					&& ref.field.equals(this.field);
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
