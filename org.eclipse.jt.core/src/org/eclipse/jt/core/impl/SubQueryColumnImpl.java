package org.eclipse.jt.core.impl;

/**
 * 子查询对应的查询输出列
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
