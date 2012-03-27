package org.eclipse.jt.core.exception;

import org.eclipse.jt.core.def.table.TableDefine;

/**
 * ����ṹͬ���쳣
 * 
 * @author Jeff Tang
 * 
 */
public final class TableSynchronizationException extends CoreException {

	private static final long serialVersionUID = -1776879243885138609L;

	public final TableDefine table;

	public TableSynchronizationException(TableDefine table, String message) {
		super("�߼���[" + table.getName() + "]�ṹͬ���쳣:" + message);
		this.table = table;
	}

	public TableSynchronizationException(TableDefine table, String message,
			Throwable cause) {
		super("�߼���[" + table.getName() + "]�ṹͬ���쳣:" + message, cause);
		this.table = table;
	}
}
