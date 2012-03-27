package org.eclipse.jt.core.exception;

import org.eclipse.jt.core.def.table.TableDefine;

/**
 * 表定义结构同步异常
 * 
 * @author Jeff Tang
 * 
 */
public final class TableSynchronizationException extends CoreException {

	private static final long serialVersionUID = -1776879243885138609L;

	public final TableDefine table;

	public TableSynchronizationException(TableDefine table, String message) {
		super("逻辑表[" + table.getName() + "]结构同步异常:" + message);
		this.table = table;
	}

	public TableSynchronizationException(TableDefine table, String message,
			Throwable cause) {
		super("逻辑表[" + table.getName() + "]结构同步异常:" + message, cause);
		this.table = table;
	}
}
