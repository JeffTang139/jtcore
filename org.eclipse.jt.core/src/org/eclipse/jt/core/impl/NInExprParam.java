package org.eclipse.jt.core.impl;


/**
 * IN表达式参数节点
 * 
 * @author Jeff Tang
 * 
 */
abstract class NInExprParam implements TextLocalizable {
	public static final NInExprParam EMPTY = new NInExprParam(Token.EMPTY,
			Token.EMPTY) {
	};

	private final int startLine;
	private final int startCol;
	private final int endLine;
	private final int endCol;

	public NInExprParam(Token start, Token end) {
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
