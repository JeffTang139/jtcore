package org.eclipse.jt.core.impl;

class NQueryStmt implements NStatement {
	public static final NQueryStmt EMPTY = new NQueryStmt(Token.EMPTY, null,
			NQuerySpecific.EMPTY, null);

	private final int startLine;
	private final int startCol;

	public final NQueryWith[] predefines;
	public final NQuerySpecific expr;
	public final NOrderBy order;

	public NQueryStmt(Token start, NQueryWith[] predefines,
			NQuerySpecific expr, NOrderBy order) {
		this.predefines = predefines;
		this.expr = expr;
		this.order = order;
		if (start != null) {
			this.startLine = start.line;
			this.startCol = start.col;
		} else {
			this.startLine = expr.startLine();
			this.startCol = expr.startCol();
		}
	}

	public int startLine() {
		return this.startLine;
	}

	public int startCol() {
		return this.startCol;
	}

	public int endLine() {
		if (this.order != null) {
			return this.order.endLine();
		}
		return this.expr.endLine();
	}

	public int endCol() {
		if (this.order != null) {
			return this.order.endCol();
		}
		return this.expr.endCol();
	}

	final NQuerySpecific getMasterSelect() {
		return this.expr;
	}

	public <T> void accept(T visitorContext, SQLVisitor<T> visitor) {
		visitor.visitQueryStmt(visitorContext, this);
	}

	@Override
	public String toString() {
		return RenderVisitor.render(this);
	}
}
