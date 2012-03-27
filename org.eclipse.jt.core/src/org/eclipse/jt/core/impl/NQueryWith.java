package org.eclipse.jt.core.impl;

/**
 * WITH×Ó¾ä½Úµã
 * 
 * @author Jeff Tang
 * 
 */
class NQueryWith implements TextLocalizable {
	public static final NQueryWith EMPTY = new NQueryWith(Token.EMPTY,
			TString.EMPTY, NQuerySpecific.EMPTY);

	private final int startLine;
	private final int startCol;
	private final int endLine;
	private final int endCol;

	public final TString name;
	public final NQuerySpecific query;

	public NQueryWith(Token start, TString name, NQuerySpecific query) {
		this.name = name;
		this.query = query;
		this.startLine = start.line;
		this.startCol = start.col;
		this.endLine = name.line;
		this.endCol = name.col + name.length;
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
