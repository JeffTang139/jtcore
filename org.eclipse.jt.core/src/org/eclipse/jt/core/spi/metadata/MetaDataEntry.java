package org.eclipse.jt.core.spi.metadata;

import org.eclipse.jt.core.misc.MissingObjectException;

/**
 * 参数据条目定义
 * 
 * @author Jeff Tang
 * 
 */
public interface MetaDataEntry {
	/**
	 * 名称
	 */
	public String getName();

	/**
	 * 描述
	 */
	public String getDescription();

	/**
	 * 获得版本
	 */
	public long getVersion();

	/**
	 * 获得数据大小
	 */
	public int getDataSize();

	/**
	 * 获得子条目的个数
	 */
	public int getSubCount();

	/**
	 * 获取子条目
	 */
	public MetaDataEntry getSub(int index) throws IndexOutOfBoundsException;

	/**
	 * 根据名称查找子条目，找不到抛出异常
	 */
	public MetaDataEntry getSub(String name) throws MissingObjectException;

	/**
	 * 根据名称查找子条目，找不到则返回null
	 */
	public MetaDataEntry findSub(String name);
}
