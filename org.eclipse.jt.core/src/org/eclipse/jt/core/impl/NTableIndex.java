package org.eclipse.jt.core.impl;

/**
 * 表索引节点
 * 
 * @author Jeff Tang
 * 
 */
class NTableIndex implements TextLocalizable {
	public static final NTableIndex EMPTY = new NTableIndex(
			Token.EMPTY,
			Token.EMPTY,
			TString.EMPTY,
			new NTableIndexField[] { new NTableIndexField(TString.EMPTY, false) },
			false);

	private final int startLine;
	private final int startCol;
	private final int endLine;
	private final int endCol;

	public final TString name;
	public final NTableIndexField[] fields;
	public final boolean unique;

	public NTableIndex(Token start, Token end, TString name,
			NTableIndexField[] fields, boolean unique) {
		this.name = name;
		this.fields = fields;
		this.unique = unique;
		this.startLine = start.line;
		this.startCol = start.col;
		this.endLine = end.endLine();
		this.endCol = end.endCol();
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

	final NTableIndex merge(NTableIndex index) {
		return index;
	}
}
