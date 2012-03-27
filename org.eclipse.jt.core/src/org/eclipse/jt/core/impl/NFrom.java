package org.eclipse.jt.core.impl;


/**
 * FROM½Úµã
 * 
 * @author Jeff Tang
 * 
 */
class NFrom implements TextLocalizable {
	public static final NFrom EMPTY = new NFrom(Token.EMPTY, new NSource[0]) {
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

	public final NSource[] sources;

	public NFrom(Token start, NSource[] sources) {
		this.sources = sources;
		this.startLine = start.line;
		this.startCol = start.col + start.length;
	}

	public int startLine() {
		return this.startLine;
	}

	public int startCol() {
		return this.startCol;
	}

	public int endLine() {
		return this.sources[this.sources.length - 1].endLine();
	}

	public int endCol() {
		return this.sources[this.sources.length - 1].endCol();
	}
}
