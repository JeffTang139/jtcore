package org.eclipse.jt.core.def.info;

import org.eclipse.jt.core.def.FieldDeclare;

/**
 * 信息参数定义接口
 * 
 * @author Jeff Tang
 * 
 */
public interface InfoParameterDeclare extends InfoParameterDefine, FieldDeclare {
	/**
	 * 获得信息定义
	 * 
	 * @return 返回信息定义
	 */
	public InfoDeclare getOwner();
}
