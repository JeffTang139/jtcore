package org.eclipse.jt.core.impl;

/**
 * SELECT½Úµã
 * 
 * @author Jeff Tang
 * 
 */
class NSelect implements TextLocalizable {
	public static final NSelect EMPTY = new NSelect(Token.EMPTY,
			SetQuantifier.ALL, new NQueryColumn[0]) {
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

	public final NQueryColumn[] columns;
	public final SetQuantifier quantifier;

	public NSelect(Token start, SetQuantifier quantifier, NQueryColumn[] columns) {
		this.quantifier = quantifier;
		this.columns = columns;
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
		return this.columns[this.columns.length - 1].endLine();
	}

	public int endCol() {
		return this.columns[this.columns.length - 1].endCol();
	}
}
