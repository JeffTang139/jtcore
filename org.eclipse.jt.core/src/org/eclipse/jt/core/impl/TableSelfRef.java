package org.eclipse.jt.core.impl;

/**
 * �߼������������
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
		throw new UnsupportedOperationException("ֻ���ڱ��ϵ������ʹ�øñ��ʽ.");
	}

	@Override
	public final String getXMLTagName() {
		throw new UnsupportedOperationException();
	}

}
