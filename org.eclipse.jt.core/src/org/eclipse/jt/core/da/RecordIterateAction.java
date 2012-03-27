package org.eclipse.jt.core.da;

import org.eclipse.jt.core.Context;

/**
 * 记录对象的迭代操作
 * 
 * @author Jeff Tang
 * 
 */
public interface RecordIterateAction {

	/**
	 * 记录对象的迭代操作
	 * 
	 * @param context
	 *            当前context
	 * @param record
	 *            当前被迭代的记录对象
	 * @param recordIndex
	 *            当前被迭代记录对象的序号,从0开始
	 * @return 返回是否中止迭代操作.为true则不会继续读取下一行查询结果.
	 * @throws Throwable
	 */
	public boolean iterate(Context context, IteratedRecord record,
			long recordIndex) throws Throwable;
}
