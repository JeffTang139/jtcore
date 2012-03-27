package org.eclipse.jt.core.impl;

import java.sql.SQLException;

import org.eclipse.jt.core.def.table.TableDefine;


/**
 * 表分区的器
 * 
 * @author Jeff Tang
 * 
 */
abstract class TablePartitioner {

	/**
	 * 尝试对指定的逻辑表执行拆分分区的操作
	 */
	public abstract void split(TableDefine table, DBConnectionEntry conn)
			throws SQLException;
}
