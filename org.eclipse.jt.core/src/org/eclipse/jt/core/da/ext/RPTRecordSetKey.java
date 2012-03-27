package org.eclipse.jt.core.da.ext;

/**
 * 键
 * 
 * @author Jeff Tang
 * 
 */
public interface RPTRecordSetKey extends RPTRecordSetColumn {
	/**
	 * 键名称
	 */
	public String getName();

	/**
	 * 获得默认的约束（记录集的默认约束对应该键的键约束），对该约束的设置影响默认的条件
	 */
	public RPTRecordSetKeyRestriction getDefaultKeyRestriction();

	/**
	 * 添加键约束<br>
	 * 等效于：this.getDefaultKeyRestriction().addMatchValue(Object keyValue);
	 */
	public int addMatchValue(Object keyValue);

	/**
	 * 清除约束<br>
	 * 等效于：this.getDefaultKeyRestriction().clearMatchValues();
	 */
	public void clearMatchValues();
}
