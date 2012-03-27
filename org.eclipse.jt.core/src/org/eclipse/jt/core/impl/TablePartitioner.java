package org.eclipse.jt.core.impl;

import java.sql.SQLException;

import org.eclipse.jt.core.def.table.TableDefine;


/**
 * ���������
 * 
 * @author Jeff Tang
 * 
 */
abstract class TablePartitioner {

	/**
	 * ���Զ�ָ�����߼���ִ�в�ַ����Ĳ���
	 */
	public abstract void split(TableDefine table, DBConnectionEntry conn)
			throws SQLException;
}
