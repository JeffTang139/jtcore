package org.eclipse.jt.core.impl;

/**
 * ORDER BY½Úµã
 * 
 * @author Jeff Tang
 * 
 */
class NOrderBy implements TextLocalizable {
	private final int startLine;
	private final int startCol;
	private final int endLine;
	private final int endCol;

	public final NOrderByColumn[] columns;

	public NOrderBy(Token start, NOrderByColumn[] columns) {
		this.columns = columns;
		this.startLine = start.line;
		this.startCol = start.col;
		NOrderByColumn c = columns[columns.length - 1];
		this.endLine = c.endLine();
		this.endCol = c.endCol();
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
