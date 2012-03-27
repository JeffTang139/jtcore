package org.eclipse.jt.core.impl;

/**
 * INSERT�Ӳ�ѯ�ڵ�
 * 
 * @author Jeff Tang
 * 
 */
class NInsertSubQuery extends NInsertSource {
	public final NQuerySpecific query;

	public NInsertSubQuery(Token start, Token end, NQuerySpecific query) {
		super(start, end);
		this.query = query;
	}

	public <T> void accept(T visitorContext, SQLVisitor<T> visitor) {
		visitor.visitInsertSubQuery(visitorContext, this);
	}
}
