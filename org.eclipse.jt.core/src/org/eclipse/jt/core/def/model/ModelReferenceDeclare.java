package org.eclipse.jt.core.def.model;

import org.eclipse.jt.core.def.NamedDeclare;

/**
 * 模型关系定义
 * 
 * @author Jeff Tang
 * 
 */
public interface ModelReferenceDeclare extends ModelReferenceDefine,
		NamedDeclare {
	/**
	 * 获得字段定义属于的模型定义
	 * 
	 * @return 返回模型定义
	 */
	public ModelDeclare getOwner();
}
