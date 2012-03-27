package org.eclipse.jt.core.def.obja;

import org.eclipse.jt.core.def.FieldDeclare;

/**
 * 结构字段定义
 * 
 * @author Jeff Tang
 * 
 */
public interface StructFieldDeclare extends StructFieldDefine, FieldDeclare {
	/**
	 * 设置是否是状态字段，参与序列化克隆比较等
	 */
	public void setStateField(boolean value);

}
