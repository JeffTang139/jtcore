package org.eclipse.jt.core.da.ext;

/**
 * 键约束
 * 
 * @author Jeff Tang
 * 
 */
public interface RPTRecordSetKeyRestriction extends RPTRecordSetColumn {

	/**
	 * 设置匹配用键约束，默认情况下匹配键的默认约束
	 */
	public RPTRecordSetKeyRestriction setMatchKeyRestriction(
			RPTRecordSetKeyRestriction matchKeyRestriction);

	/**
	 * 获得约束键
	 */
	public RPTRecordSetKey getKey();

	/**
	 * 添加键约束
	 */
	public int addMatchValue(Object keyValue);

	/**
	 * 添加本约束的键约束，同时设置匹配键约束的匹配值
	 */
	public int addMatchValue(Object keyValue, Object matchKeyValue);

	/**
	 * 清除约束值
	 */
	public Object removeMatchValue(Object keyValue);

	/**
	 * 获取约束个数
	 */
	public int getMatchValueCount();

	/**
	 * 清除约束
	 */
	public void clearMatchValues();
}
