package org.eclipse.jt.core.def.query;

import org.eclipse.jt.core.def.DeclareBase;

/**
 * 排序项定义
 * 
 * @see org.eclipse.jt.core.def.query.OrderByItemDefine
 * 
 * @author Jeff Tang
 * 
 */
public interface OrderByItemDeclare extends OrderByItemDefine, DeclareBase {

	/**
	 * 设置是否是倒序排列
	 * 
	 * @param value
	 */
	public void setDesc(boolean value);
}
