package org.eclipse.jt.core.impl;

/**
 * DELETEÓï¾ä½Úµã
 * 
 * @author Jeff Tang
 * 
 */
class NDeleteStmt implements NStatement {
	public final NDelete delete;
	public final NWhere where;
	public final NReturning returning;

	public NDeleteStmt(NDelete delete, NWhere where, NReturning returning) {
		this.delete = delete;
		this.where = where;
		this.returning = returning;
	}

	public int startLine() {
		return this.delete.startLine();
	}

	public int startCol() {
		return this.delete.startCol();
	}

	public int endLine() {
		if (this.where != null) {
			return this.where.endLine();
		}
		return this.delete.endLine();
	}

	public int endCol() {
		if (this.where != null) {
			return this.where.endCol();
		}
		return this.delete.endCol();
	}

	public <T> void accept(T visitorContext, SQLVisitor<T> visitor) {
		visitor.visitDeleteStmt(visitorContext, this);
	}

	@Override
	public String toString() {
		return RenderVisitor.render(this);
	}
}
