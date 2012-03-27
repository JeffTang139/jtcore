package org.eclipse.jt.core.def.model;

import org.eclipse.jt.core.model.ModelService;

/**
 * 模型构造器定义接口
 * 
 * @author Jeff Tang
 * 
 */
public interface ModelConstructorDeclare extends ModelConstructorDefine,
		ModelInvokeDeclare {
	/**
	 * 构造器的脚本
	 * 
	 * @return 返回脚本定义对象
	 */
	public ScriptDeclare getScript();

	/**
	 * 设置模型构造器，<br>
	 * 该方法提供给运行时模型设计器使用，模型声名器中不能使用
	 * 
	 * @return 返回旧的构造器
	 */
	public ModelService<?>.ModelConstructor<?> setConstructor(
			ModelService<?>.ModelConstructor<?> constructor);

}
