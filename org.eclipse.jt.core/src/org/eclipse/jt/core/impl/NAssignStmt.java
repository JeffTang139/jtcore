package org.eclipse.jt.core.impl;

/**
 * ¸³ÖµÓï¾ä
 * 
 * @author Jeff Tang
 * 
 */
class NAssignStmt implements NStatement {
	private final int startLine;
	private final int startCol;
	private final int endLine;
	private final int endCol;

	public final TString[] vars;
	public final NValueExpr[] values;
	public final NQueryStmt query;

	public NAssignStmt(TextLocalizable start, TextLocalizable end,
			TString[] vars, NValueExpr[] values) {
		this.vars = vars;
		this.values = values;
		this.query = null;
		this.startLine = start.startLine();
		this.startCol = start.startCol();
		this.endLine = end.endLine();
		this.endCol = end.endCol();
	}

	public NAssignStmt(TextLocalizable start, TString[] vars, NQueryStmt query) {
		this.vars = vars;
		this.query = query;
		this.values = null;
		this.startLine = start.startLine();
		this.startCol = start.startCol();
		this.endLine = query.endLine();
		this.endCol = query.endCol();
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
		visitor.visitAssignStmt(visitorContext, this);
	}
	
	@Override
	public String toString() {
		return RenderVisitor.render(this);
	}
}
