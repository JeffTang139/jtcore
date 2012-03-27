package org.eclipse.jt.core.exception;

import org.eclipse.jt.core.def.table.TableDefine;

/**
 * 不一致的分区定义异常
 * 
 * <p>
 * 当分区定义的字段类型错误或者分区定义与数据库不匹配时抛出该异常
 * 
 * @author Jeff Tang
 * 
 */
public final class InconsistentPartitionException extends CoreException {

	private static final long serialVersionUID = 1L;

	public InconsistentPartitionException(TableDefine table) {
		super("表定义[" + table.getName() + "]的分区定义与数据库不匹配");
	}

}
