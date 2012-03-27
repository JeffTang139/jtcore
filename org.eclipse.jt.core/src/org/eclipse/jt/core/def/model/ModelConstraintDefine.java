package org.eclipse.jt.core.def.model;

import org.eclipse.jt.core.def.info.InfoDefine;

/**
 * 模型约束定义
 * 
 * @author Jeff Tang
 * 
 */
public interface ModelConstraintDefine extends InfoDefine {
	/**
	 * 获得字段定义属于的模型定义
	 * 
	 * @return 返回模型定义
	 */
	public ModelDefine getOwner();

	/**
	 * 检查器的脚本
	 * 
	 * @return 返回脚本定义对象
	 */
	public ScriptDefine getScript();
}
