package org.eclipse.jt.core.impl;

/**
 * PreparedStatementִ����
 * 
 * @author Jeff Tang
 * 
 */
class CommonExecutor extends PsExecutor<Sql> {

	CommonExecutor(DBAdapterImpl dbAdapter, Sql sql) {
		super(dbAdapter, sql);
	}

}
