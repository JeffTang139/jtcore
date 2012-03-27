package org.eclipse.jt.core.def.info;

import org.eclipse.jt.core.def.FieldDefine;

/**
 * 信息参数定义接口
 * 
 * @author Jeff Tang
 * 
 */
public interface InfoParameterDefine extends FieldDefine {
	/**
	 * 获得信息定义
	 * 
	 * @return 返回信息定义
	 */
	public InfoDefine getOwner();
}
