package org.eclipse.jt.core.impl;

/**
 * INSERT×Ó¾ä½Úµã
 * 
 * @author Jeff Tang
 * 
 */
class NInsert implements TextLocalizable {
	public static final NInsert EMPTY = new NInsert(Token.EMPTY,
			NNameRef.EMPTY);

	private final int startLine;
	private final int startCol;

	public final NNameRef table;

	public NInsert(Token start, NNameRef table) {
		this.table = table;
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
		return this.table.endLine();
	}

	public int endCol() {
		return this.table.endCol();
	}
}
