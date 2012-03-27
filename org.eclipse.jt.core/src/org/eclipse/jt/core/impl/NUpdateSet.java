package org.eclipse.jt.core.impl;


/**
 * SET×Ó¾ä½Úµã
 * 
 * @author Jeff Tang
 * 
 */
class NUpdateSet implements TextLocalizable {
	public static final NUpdateSet EMPTY = new NUpdateSet(Token.EMPTY,
			new NUpdateColumnValue[0]) {
		@Override
		public int endLine() {
			return 0;
		}

		@Override
		public int endCol() {
			return 0;
		}
	};

	private final int startLine;
	private final int startCol;

	public final NUpdateColumnValue[] columns;

	public NUpdateSet(Token start, NUpdateColumnValue[] columns) {
		this.columns = columns;
		this.startCol = start.col;
		this.startLine = start.line;
	}

	public int startLine() {
		return this.startLine;
	}

	public int startCol() {
		return this.startCol;
	}

	public int endLine() {
		return this.columns[this.columns.length - 1].endLine();
	}

	public int endCol() {
		return this.columns[this.columns.length - 1].endCol();
	}
}
