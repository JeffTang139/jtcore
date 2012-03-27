package org.eclipse.jt.core.impl;

abstract class NDmlDeclare implements NStatement {
	private final int startLine;
	private final int startCol;
	private final int endLine;
	private final int endCol;

	public final NParamDeclare[] params;
	public final TString name;

	public NDmlDeclare(Token start, Token end, TString name,
			NParamDeclare[] params) {
		this.name = name;
		this.params = params;
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
}
