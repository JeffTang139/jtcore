package org.eclipse.jt.core.impl;

/**
 * �Ӳ�ѯ��Ӧ�Ĳ�ѯ�����
 * 
 * @author Jeff Tang
 * 
 */
final class SubQueryColumnImpl extends
		SelectColumnImpl<SubQueryImpl, SubQueryColumnImpl> {

	SubQueryColumnImpl(SubQueryImpl owner, String name, ValueExpr expr) {
		super(owner, name, expr);
	}

}
