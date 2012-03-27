package org.eclipse.jt.core.impl;

/**
 * FOREACHÓï¾ä
 * 
 * @author Jeff Tang
 * 
 */
class NForeachStmt implements NStatement {
	private final int startLine;
	private final int startCol;
	public final TString var;
	public final NQueryStmt query;
	public final NQueryInvoke call;
	public final NStatement stmt;

	public NForeachStmt(Token start, TString var, NQueryStmt query,
			NStatement stmt) {
		this.var = var;
		this.query = query;
		this.stmt = stmt;
		this.call = null;
		this.startLine = start.line;
		this.startCol = start.col + start.length;
	}

	public NForeachStmt(Token start, TString var, NQueryInvoke call,
			NStatement stmt) {
		this.var = var;
		this.stmt = stmt;
		this.call = call;
		this.query = null;
		this.startLine = start.line;
		this.startCol = start.col + start.length;
	}

	public int startLine() {
		return this.startLine;
	}

	public int startCol() {
		return this.startCol;
	}

	public int endLine() {
		return this.stmt.endLine();
	}

	public int endCol() {
		return this.stmt.endCol();
	}

	public <T> void accept(T visitorContext, SQLVisitor<T> visitor) {
		visitor.visitForeachStmt(visitorContext, this);
	}
	
	@Override
	public String toString() {
		return RenderVisitor.render(this);
	}
}
