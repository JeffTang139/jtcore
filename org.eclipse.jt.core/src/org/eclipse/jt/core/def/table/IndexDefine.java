package org.eclipse.jt.core.def.table;

import org.eclipse.jt.core.def.ModifiableContainer;
import org.eclipse.jt.core.def.NamedDefine;

/**
 * 物理表索引定义
 * 
 * @author Jeff Tang
 * 
 */
public interface IndexDefine extends NamedDefine {

	/**
	 * 表定义
	 */
	public TableDefine getOwner();

	/**
	 * 是否是唯一索引
	 */
	public boolean isUnique();

	/**
	 * 返回索引组合字段的枚举器
	 * 
	 * @return 返回列的跌代器
	 */
	public ModifiableContainer<? extends IndexItemDefine> getItems();

}
