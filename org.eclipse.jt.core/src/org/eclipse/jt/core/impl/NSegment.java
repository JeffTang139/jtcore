package org.eclipse.jt.core.impl;

/**
 * Óï¾ä¶Î
 * 
 * @author Jeff Tang
 * 
 */
class NSegment implements NStatement {
	public static final NSegment EMPTY = new NSegment(Token.EMPTY, Token.EMPTY,
			new NStatement[] { NStatement.EMPTY });

	private final int startLine;
	private final int startCol;
	private final int endLine;
	private final int endCol;

	public final NStatement[] stmts;

	public NSegment(TextLocalizable start, TextLocalizable end,
			NStatement[] statements) {
		this.stmts = statements;
		this.startLine = start.startLine();
		this.startCol = start.startCol();
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

	public <T> void accept(T visitorContext, SQLVisitor<T> visitor) {
		visitor.visitSegment(visitorContext, this);
	}
	
	@Override
	public String toString() {
		return RenderVisitor.render(this);
	}
}
