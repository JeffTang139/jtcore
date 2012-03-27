package org.eclipse.jt.core.impl;

/**
 * 逻辑表自身表引用
 * 
 * @author Jeff Tang
 * 
 */
final class TableSelfRef extends StandaloneTableRef {

	TableSelfRef(TableDefineImpl target) {
		super(target.name, target);
	}

	public final TableUsage tableUsage() {
		throw new UnsupportedOperationException();
	}

	public final void formatFieldRef(SqlBuilder sql, TableFieldDefineImpl field) {
		throw new UnsupportedOperationException("只能在表关系定义中使用该表达式.");
	}

	@Override
	public final String getXMLTagName() {
		throw new UnsupportedOperationException();
	}

}
