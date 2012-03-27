package org.eclipse.jt.core.def.model;

import org.eclipse.jt.core.model.ModelService;

/**
 * 模型属性访问器定义
 * 
 * @author Jeff Tang
 * 
 */
public interface ModelPropAccessDeclare extends ModelPropAccessDefine {
	/**
	 * 获得访问器的脚本
	 * 
	 * @return 返回脚本定义
	 */
	public ScriptDeclare getScript();

	/**
	 * 属性访问器对应的字段定义
	 */
	public void setRefField(ModelFieldDefine field);

	/**
	 * 设置模型属性访问器，<br>
	 * 该方法提供给运行时模型设计器使用，模型声名器中不能使用
	 * 
	 * @return 返回旧的属性访问器
	 */
	public ModelService<?>.ModelPropertyAccessor setAccessor(
			ModelService<?>.ModelPropertyAccessor accessor);
}
