package org.eclipse.jt.core.impl;

/**
 * IN���ʽ�ֲ�ѯ
 * 
 * @author Jeff Tang
 * 
 */
class NInParamSubQuery extends NInExprParam {
	public final NQuerySpecific query;

	public NInParamSubQuery(Token start, Token end, NQuerySpecific query) {
		super(start, end);
		this.query = query;
	}
}
