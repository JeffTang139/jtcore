package org.eclipse.jt.core.def.table;

import org.eclipse.jt.core.def.NamedDefine;

/**
 * 物理表定义
 * 
 * @author Jeff Tang
 * 
 */
public interface DBTableDefine extends NamedDefine {

	/**
	 * 所属表定义
	 */
	public TableDefine getOwner();

	/**
	 * 获得属于该物理表的字段个数
	 */
	public int getFieldCount();

}
