package org.eclipse.jt.core.def.table;

import org.eclipse.jt.core.def.NamedDefine;

/**
 * 级次定义
 * 
 * @author Jeff Tang
 * 
 */
public interface HierarchyDefine extends NamedDefine {

	/**
	 * 所属表定义
	 */
	public TableDefine getOwner();

	/**
	 * 获得最大支持的级次深度
	 * 
	 * @return 返回最大级次
	 */
	public int getMaxLevel();

}
