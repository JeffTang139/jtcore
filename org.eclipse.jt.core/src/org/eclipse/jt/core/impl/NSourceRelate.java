package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.table.TableJoinType;

/**
 * 关系-主外键连接节点
 * 
 * @author Jeff Tang
 * 
 */
class NSourceRelate extends NSource {
	public final NSource source;
	public final TString table;
	public final TableJoinType joinType;
	public final TString rel;
	public final TString alias;

	public NSourceRelate(TableJoinType joinType, NSource source, TString table,
			TString rel, TString alias) {
		this.joinType = joinType;
		this.source = source;
		this.table = table;
		this.rel = rel;
		this.alias = alias;
	}

	public int startLine() {
		return this.source.startLine();
	}

	public int startCol() {
		return this.source.startCol();
	}

	public int endLine() {
		return this.alias != null ? this.alias.line : this.rel.line;
	}

	public int endCol() {
		return this.alias != null ? (this.alias.col + this.alias.length)
				: (this.rel.col + this.rel.length);
	}

	public <T> void accept(T visitorContext, SQLVisitor<T> visitor) {
		visitor.visitSourceRelate(visitorContext, this);
	}
}
