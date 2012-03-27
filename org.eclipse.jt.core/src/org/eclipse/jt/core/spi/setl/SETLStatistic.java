package org.eclipse.jt.core.spi.setl;

/**
 * 统计信息
 * 
 * @author Jeff Tang
 * 
 */
public class SETLStatistic {
	/**
	 * 源表数量
	 */
	public int sources;
	/**
	 * 源数据条目
	 */
	public long sourceRecords;
	/**
	 * 目标表数量
	 */
	public int targets;
	/**
	 * 目标记录数量
	 */
	public long targetRecords;
	/**
	 * 匹配次数
	 */
	public long matchingTimes;
	/**
	 * 匹配命中次数
	 */
	public long matchedTimes;
	/**
	 * 目标运算次数
	 */
	public long calTimes;
}
