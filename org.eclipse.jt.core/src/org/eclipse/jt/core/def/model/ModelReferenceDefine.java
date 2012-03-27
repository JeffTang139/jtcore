package org.eclipse.jt.core.def.model;

import org.eclipse.jt.core.def.NamedDefine;

/**
 * 模型关系定义
 * 
 * @author Jeff Tang
 * 
 */
public interface ModelReferenceDefine extends NamedDefine {
	/**
	 * 获得字段定义属于的模型定义
	 * 
	 * @return 返回模型定义
	 */
	public ModelDefine getOwner();

	/**
	 * 获取目标模型
	 * 
	 * @return 返回目标模型
	 */
	public ModelDefine getTarget();
}
