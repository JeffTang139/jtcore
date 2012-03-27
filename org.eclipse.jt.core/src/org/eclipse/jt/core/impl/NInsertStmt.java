package org.eclipse.jt.core.impl;

/**
 * INSERTÓï¾ä½Úµã
 * 
 * @author Jeff Tang
 * 
 */
class NInsertStmt implements NStatement {
	public static final NInsertStmt EMPTY = new NInsertStmt(NInsert.EMPTY,
			NInsertSource.EMPTY, null);

	public final NInsert insert;
	public final NInsertSource values;
	public final NReturning returning;

	public NInsertStmt(NInsert insert, NInsertSource values,
			NReturning returning) {
		this.insert = insert;
		this.values = values;
		this.returning = returning;
	}

	public int startLine() {
		return this.insert.startLine();
	}

	public int startCol() {
		return this.insert.startCol();
	}

	public int endLine() {
		return this.values.endLine();
	}

	public int endCol() {
		return this.values.endCol();
	}

	public <T> void accept(T visitorContext, SQLVisitor<T> visitor) {
		visitor.visitInsertStmt(visitorContext, this);
	}

	@Override
	public String toString() {
		return RenderVisitor.render(this);
	}
}
