package org.eclipse.jt.core.impl;

/**
 * ��ϵ-��ѯ�ڵ�
 * 
 * @author Jeff Tang
 * 
 */
class NSourceSubQuery extends NSource {
	private final int startLine;
	private final int startCol;
	public final NQuerySpecific query;
	public final TString alias;

	public NSourceSubQuery(Token start, NQuerySpecific query, TString alias) {
		this.query = query;
		this.alias = alias;
		this.startLine = start.line;
		this.startCol = start.col;
	}

	public int startLine() {
		return this.startLine;
	}

	public int startCol() {
		return this.startCol;
	}

	public int endLine() {
		return this.alias.line;
	}

	public int endCol() {
		return this.alias.col + this.alias.length;
	}

	public <T> void accept(T visitorContext, SQLVisitor<T> visitor) {
		visitor.visitSourceSubQuery(visitorContext, this);
	}
}
