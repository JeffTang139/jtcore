package org.eclipse.jt.core.def.table;

import org.eclipse.jt.core.def.DeclareBase;

/**
 * 可设置的索引字段定义
 * 
 * @author Jeff Tang
 * 
 */
public interface IndexItemDeclare extends IndexItemDefine, DeclareBase {
	/**
	 * 设置是否降序
	 * 
	 * @param desc
	 */
	public void setDesc(boolean desc);
}
