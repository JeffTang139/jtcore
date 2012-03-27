package org.eclipse.jt.core.impl;

/**
 * IN表达式字查询
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
