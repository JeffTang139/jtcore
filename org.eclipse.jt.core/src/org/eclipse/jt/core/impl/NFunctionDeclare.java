package org.eclipse.jt.core.impl;

class NFunctionDeclare implements NStatement {
	private final int startLine;
	private final int startCol;
	private final int endLine;
	private final int endCol;
	public final TString name;
	public final NDataType returnType;
	public final NParamDeclare[] params;
	public final NStatement[] stmts;

	public NFunctionDeclare(Token start, Token end, TString name,
			NParamDeclare[] params, NDataType returnType, NStatement[] stmts) {
		this.name = name;
		this.returnType = returnType;
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
		visitor.visitFunctionDeclare(visitorContext, this);
	}
}
