package org.eclipse.jt.core.impl;

/**
 * 返回中间值
 * 
 * @author Jeff Tang
 * 
 */
class NReturning implements TextLocalizable {
	private final int startLine;
	private final int startCol;
	public final TString env;
	public final TString var;

	public NReturning(Token start, TString env, TString var) {
		this.env = env;
		this.var = var;
		this.startLine = start.line;
		this.startCol = start.col;
	}

	public int startLine() {
		return this.startLine;
	}

	public int startCol() {
		return this.startCol;
	}

	public int endLine() {
		return this.var.line;
	}

	public int endCol() {
		return this.var.line + this.var.length;
	}
}
