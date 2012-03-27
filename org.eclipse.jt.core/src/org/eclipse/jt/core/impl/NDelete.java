package org.eclipse.jt.core.impl;


/**
 * DELETE×Ó¾ä½Úµã
 * 
 * @author Jeff Tang
 * 
 */
class NDelete implements TextLocalizable {
	public static final NDelete EMPTY = new NDelete(Token.EMPTY, NSource.EMPTY);

	private final int startLine;
	private final int startCol;

	public final NSource source;

	public NDelete(Token start, NSource source) {
		this.source = source;
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
		return this.source.endLine();
	}

	public int endCol() {
		return this.source.endCol();
	}
}
