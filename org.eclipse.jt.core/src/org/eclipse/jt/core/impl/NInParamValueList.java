package org.eclipse.jt.core.impl;


/**
 * IN���ʽֵ�б�
 * 
 * @author Jeff Tang
 * 
 */
class NInParamValueList extends NInExprParam {
	public final NValueExpr[] values;

	public NInParamValueList(Token start, Token end, NValueExpr[] values) {
		super(start, end);
		this.values = values;
	}
}
