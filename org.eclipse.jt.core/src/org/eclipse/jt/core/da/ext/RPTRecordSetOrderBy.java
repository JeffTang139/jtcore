package org.eclipse.jt.core.da.ext;

/**
 * 排序项
 * 
 * @author Jeff Tang
 * 
 */
public interface RPTRecordSetOrderBy {
	/**
	 * 获得对应列
	 */
	public RPTRecordSetColumn getColumn();

	/**
	 * 获得是否降序
	 */
	public boolean isDesc();

	/**
	 * 获取空值是否作为最小值排序
	 */
	public boolean isNullAsMIN();
}
