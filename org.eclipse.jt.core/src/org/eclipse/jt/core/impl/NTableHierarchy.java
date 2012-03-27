package org.eclipse.jt.core.impl;


class NTableHierarchy implements TextLocalizable {
	public static final NTableHierarchy EMPTY = new NTableHierarchy(
			Token.EMPTY, TString.EMPTY, TInt.EMPTY);

	private final int endLine;
	private final int endCol;

	public final TString name;
	public final TInt limit;

	public NTableHierarchy(Token end, TString name, TInt limit) {
		this.name = name;
		this.limit = limit;
		this.endLine = end.endLine();
		this.endCol = end.endCol();
	}

	public int startLine() {
		return this.name.startLine();
	}

	public int startCol() {
		return this.name.startCol();
	}

	public int endLine() {
		return this.endLine;
	}

	public int endCol() {
		return this.endCol;
	}
}
