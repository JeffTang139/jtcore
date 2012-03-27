package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.spi.sql.SQLSyntaxException;

/**
 * INSERT VALUES/SUB-QUERY×Ó¾ä½Úµã
 * 
 * @author Jeff Tang
 * 
 */
abstract class NInsertSource implements TextLocalizable, SQLVisitable {
	public static final NInsertSource EMPTY = new NInsertSource(Token.EMPTY,
			Token.EMPTY) {
		public <T> void accept(T visitorContext, SQLVisitor<T> visitor) {
			throw new SQLSyntaxException();
		}
	};

	private final int startLine;
	private final int startCol;
	private final int endLine;
	private final int endCol;

	public NInsertSource(Token start, Token end) {
		this.startLine = start.line;
		this.startCol = start.col;
		this.endLine = end.line;
		this.endCol = end.col + end.length;
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

	@Override
	public String toString() {
		return RenderVisitor.render(this);
	}
}
