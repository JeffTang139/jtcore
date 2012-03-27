package org.eclipse.jt.core.impl;

/**
 * UPDATEÓï¾ä½Úµã
 * 
 * @author Jeff Tang
 * 
 */
class NUpdateStmt implements NStatement {
	public static final NUpdateStmt EMPTY = new NUpdateStmt(NUpdate.EMPTY,
			NUpdateSet.EMPTY, null, null);

	public final NUpdate update;
	public final NUpdateSet set;
	public final NWhere where;
	public final NReturning returning;

	public NUpdateStmt(NUpdate update, NUpdateSet set, NWhere where,
			NReturning returning) {
		this.update = update;
		this.set = set;
		this.where = where;
		this.returning = returning;
	}

	public int startLine() {
		return this.update.startLine();
	}

	public int startCol() {
		return this.update.startCol();
	}

	public int endLine() {
		if (this.where != null) {
			return this.where.endLine();
		}
		return this.set.endLine();
	}

	public int endCol() {
		if (this.where != null) {
			return this.where.endCol();
		}
		return this.set.endCol();
	}

	public <T> void accept(T visitorContext, SQLVisitor<T> visitor) {
		visitor.visitUpdateStmt(visitorContext, this);
	}

	@Override
	public String toString() {
		return RenderVisitor.render(this);
	}
}
