package org.eclipse.jt.core.impl;

class NTablePartition implements TextLocalizable {
	private final int startLine;
	private final int startCol;

	public final TString[] fields;
	public final TInt size;
	public final TInt limit;

	public NTablePartition(Token start, TString[] fields, TInt size, TInt limit) {
		this.fields = fields;
		this.size = size;
		this.limit = limit;
		this.startLine = start.startLine();
		this.startCol = start.startCol();
	}

	public int startLine() {
		return this.startLine;
	}

	public int startCol() {
		return this.startCol;
	}

	public int endLine() {
		return this.limit.endLine();
	}

	public int endCol() {
		return this.limit.endCol();
	}
}
