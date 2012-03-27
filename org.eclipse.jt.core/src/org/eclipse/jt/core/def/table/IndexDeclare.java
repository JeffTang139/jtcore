package org.eclipse.jt.core.def.table;

import org.eclipse.jt.core.def.ModifiableContainer;
import org.eclipse.jt.core.def.NamedDeclare;

/**
 * 可设置的物理表索引定义
 * 
 * @author Jeff Tang
 * 
 */
public interface IndexDeclare extends IndexDefine, NamedDeclare {

	/**
	 * 表定义
	 */
	public TableDeclare getOwner();

	/**
	 * 设置是否是唯一索引
	 */
	public void setUnique(boolean value);

	/**
	 * 返回索引组合字段的枚举器
	 * 
	 * @return 返回列的跌代器
	 */
	public ModifiableContainer<? extends IndexItemDeclare> getItems();

	/**
	 * 增加索引字段
	 * 
	 * @param field
	 */
	public IndexItemDeclare addItem(TableFieldDefine field);

	/**
	 * 增加索引字段
	 * 
	 * @param field
	 * @param desc
	 * @return
	 */
	public IndexItemDeclare addItem(TableFieldDefine field, boolean desc);

}
