package org.eclipse.jt.core.impl;

/**
 * ¹ý³ÌÉùÃ÷
 * 
 * @author Jeff Tang
 * 
 */
class NProcedureDeclare implements NStatement {
	private final int startLine;
	private final int startCol;
	private final int endLine;
	private final int endCol;
	public final TString name;
	public final NParamDeclare[] params;
	public final NStatement[] stmts;

	public NProcedureDeclare(Token start, Token end, TString name,
			NParamDeclare[] params, NStatement[] stmts) {
		this.name = name;
		this.params = params;
		this.stmts = stmts;
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
		visitor.visitProcedureDeclare(visitorContext, this);
	}

	@Override
	public String toString() {
		return RenderVisitor.render(this);
	}
}
