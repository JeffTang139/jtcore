package org.eclipse.jt.core.impl;

import java.util.ArrayList;

class NQuerySpecific implements NValueExpr {
	public static final NQuerySpecific EMPTY = new NQuerySpecific(
			NSelect.EMPTY, NFrom.EMPTY, null, null, null);

	private final int startLine;
	private final int startCol;
	private final int endLine;
	private final int endCol;

	public final NSelect select;
	public final NFrom from;
	public final NWhere where;
	public final NGroupBy group;
	public final NHaving having;
	ArrayList<NQuerySpecific> unions;
	boolean unionAll;

	public NQuerySpecific(NSelect select, NFrom from, NWhere where,
			NGroupBy group, NHaving having) {
		this.select = select;
		this.from = from;
		this.where = where;
		this.group = group;
		this.having = having;
		this.startLine = select.startLine();
		this.startCol = select.startCol();
		TextLocalizable n = from;
		if (having != null) {
			n = having;
		} else if (group != null) {
			n = group;
		} else if (where != null) {
			n = where;
		}
		this.endLine = n.endLine();
		this.endCol = n.endCol();
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
		visitor.visitQuerySpecific(visitorContext, this);
	}

	void union(NQuerySpecific q, boolean all) {
		if (this.unions == null) {
			this.unions = new ArrayList<NQuerySpecific>();
		}
		this.unions.add(q);
		q.unionAll = all;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj != null && obj instanceof NQuerySpecific) {
			NQuerySpecific s = (NQuerySpecific) obj;
			if (s.select.quantifier != this.select.quantifier) {
				return false;
			}
			if (s.select.columns.length != this.select.columns.length) {
				return false;
			}
			for (int i = 0, c = s.select.columns.length; i < c; i++) {
				if (s.select.columns[i].alias == null
						&& this.select.columns[i].alias != null
						|| s.select.columns[i].alias != null
						&& !s.select.columns[i].alias
								.equals(this.select.columns[i].alias)
						|| !s.select.columns[i].expr
								.equals(this.select.columns[i].expr)) {
					return false;
				}
			}
			if (s.from.sources.length != this.from.sources.length) {
				return false;
			}
			for (int i = 0, c = s.from.sources.length; i < c; i++) {
				if (!s.from.sources[i].equals(this.from.sources[i])) {
					return false;
				}
			}
			if (s.group == null) {
				if (this.group != null) {
					return false;
				}
			} else if (this.group != null) {
				if (s.group.option != this.group.option) {
					return false;
				}
				if (s.group.columns.length != this.group.columns.length) {
					return false;
				}
				for (int i = 0, c = s.group.columns.length; i < c; i++) {
					if (!s.group.columns[i].equals(this.group.columns[i])) {
						return false;
					}
				}
			}
			if (s.having == null) {
				if (this.having != null) {
					return false;
				}
			} else if (this.having != null) {
				if (!s.having.expr.equals(this.having.expr)) {
					return false;
				}
			}
			if (s.where == null) {
				if (this.where != null) {
					return false;
				}
			} else if (this.where != null) {
				if (!s.where.expr.equals(this.where.expr)) {
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
		for (int i = 0, c = this.select.columns.length; i < c; i++) {
			if (this.select.columns[i].alias != null) {
				hash ^= this.select.columns[i].alias.hashCode();
			}
			hash ^= this.select.columns[i].expr.hashCode();
		}
		for (int i = 0, c = this.from.sources.length; i < c; i++) {
			hash ^= this.from.sources[i].hashCode();
		}
		if (this.group != null) {
			for (int i = 0, c = this.group.columns.length; i < c; i++) {
				hash ^= this.group.columns[i].hashCode();
			}
		}
		if (this.having != null) {
			hash ^= this.having.expr.hashCode();
		}
		if (this.where != null) {
			hash ^= this.where.expr.hashCode();
		}
		return hash;
	}

	@Override
	public String toString() {
		return RenderVisitor.render(this);
	}
}
