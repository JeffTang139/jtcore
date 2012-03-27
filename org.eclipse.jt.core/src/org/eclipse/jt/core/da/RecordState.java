package org.eclipse.jt.core.da;

/**
 * 记录状态
 * 
 * @author Jeff Tang
 * 
 */
public enum RecordState {
	/**
	 * 新纪录
	 */
	NEW,
	/**
	 * 新纪录并修改过
	 */
	NEW_MODIFIED,
	/**
	 * 从数据库装载，并没有修改，
	 */
	IN_DB,
	/**
	 * 从数据库装载，并且已经改动（从子段上写入了数据，不一定非要是不同的数据）
	 */
	IN_DB_MODIFING,
	/**
	 * 从数据库装载，并且已经在记录集上删除
	 */
	IN_DB_DELETING,
}
