package org.eclipse.jt.core.impl;

class NSearchedCaseExpr implements NValueExpr {
	private final int startLine;
	private final int startCol;
	private final int endLine;
	private final int endCol;

	public final NSearchedCaseWhen[] branches;
	public final NValueExpr elseBranch;

	public NSearchedCaseExpr(Token start, Token end,
			NSearchedCaseWhen[] branches, NValueExpr elseBranch) {
		this.branches = branches;
		this.elseBranch = elseBranch;
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

	public <T> void accept(T visitorContext, SQLVisitor<T> visitor) {
		visitor.visitSearchedCaseExpr(visitorContext, this);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj != null && obj instanceof NSearchedCaseExpr) {
			NSearchedCaseExpr e = (NSearchedCaseExpr) obj;
			if (e.elseBranch == null) {
				if (this.elseBranch != null) {
					return false;
				}
			} else if (!e.elseBranch.equals(this.elseBranch)) {
				return false;
			}
			if (e.branches == null) {
				return this.branches == null;
			}
			if (e.branches.length != this.branches.length) {
				return false;
			}
			for (int i = 0, c = this.branches.length; i < c; i++) {
				if (!e.branches[i].condition.equals(this.branches[i].condition)
						|| !e.branches[i].returnValue
								.equals(this.branches[i].returnValue)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		for (NSearchedCaseWhen w : this.branches) {
			hash ^= w.condition.hashCode() << 16;
			hash ^= w.returnValue.hashCode();
		}
		if (this.elseBranch != null) {
			hash ^= this.elseBranch.hashCode();
		}
		return hash;
	}

	@Override
	public String toString() {
		return RenderVisitor.render(this);
	}
}
