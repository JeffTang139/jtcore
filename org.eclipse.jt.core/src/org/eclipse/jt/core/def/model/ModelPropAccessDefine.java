package org.eclipse.jt.core.def.model;

/**
 * 模型属性访问器定义，即模型属性的读取器以及设置器定义
 * 
 * @author Jeff Tang
 * 
 */
public interface ModelPropAccessDefine {
	/**
	 * 获取所属的属性定义
	 */
	public ModelPropertyDefine getPropertyDefine();

	/**
	 * 获得访问器的脚本
	 * 
	 * @return 返回脚本定义
	 */
	public ScriptDefine getScript();

	/**
	 * 属性访问器对应的字段定义
	 * 
	 * @return 返回字段定义
	 */
	public ModelFieldDefine getRefField();
}
