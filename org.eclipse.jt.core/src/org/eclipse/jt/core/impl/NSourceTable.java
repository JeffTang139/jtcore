package org.eclipse.jt.core.impl;

/**
 * 关系-表节点
 * 
 * @author Jeff Tang
 * 
 */
class NSourceTable extends NSource {
	public final NNameRef source;
	public final TString alias;
	public final boolean forUpdate;

	public NSourceTable(NNameRef source, TString alias, boolean forUpdate) {
		this.source = source;
		this.alias = alias;
		this.forUpdate = forUpdate;
	}

	public int startLine() {
		return this.source.startLine();
	}

	public int startCol() {
		return this.source.startCol();
	}

	public int endLine() {
		return this.alias != null ? this.alias.line : this.source.endLine();
	}

	public int endCol() {
		return this.alias != null ? (this.alias.col + this.alias.length)
				: this.source.endCol();
	}

	public <T> void accept(T visitorContext, SQLVisitor<T> visitor) {
		visitor.visitSourceTable(visitorContext, this);
	}

	@Override
	public String toString() {
		return RenderVisitor.render(this);
	}
}
