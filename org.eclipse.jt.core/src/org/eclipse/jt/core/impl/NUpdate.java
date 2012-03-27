package org.eclipse.jt.core.impl;


/**
 * UPDATE×Ó¾ä½Úµã
 * 
 * @author Jeff Tang
 * 
 */
class NUpdate implements TextLocalizable {
	public static final NUpdate EMPTY = new NUpdate(Token.EMPTY, NSource.EMPTY);

	private final int startLine;
	private final int startCol;

	public final NSource source;

	public NUpdate(Token start, NSource source) {
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
