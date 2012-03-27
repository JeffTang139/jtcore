package org.eclipse.jt.core.impl;

/**
 * ²éÑ¯µ÷ÓÃ
 * 
 * @author Jeff Tang
 * 
 */
class NQueryInvoke implements TextLocalizable {
	private final int endLine;
	private final int endCol;

	public final TString name;
	public final NValueExpr[] params;

	public NQueryInvoke(Token end, TString name, NValueExpr[] params) {
		this.name = name;
		this.params = params;
		this.endLine = end.line;
		this.endCol = end.col + end.length;
	}

	public int startLine() {
		return this.name.line;
	}

	public int startCol() {
		return this.name.col;
	}

	public int endLine() {
		return this.endLine;
	}

	public int endCol() {
		return this.endCol;
	}
}
