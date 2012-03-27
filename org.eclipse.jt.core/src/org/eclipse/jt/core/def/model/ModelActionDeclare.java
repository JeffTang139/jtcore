package org.eclipse.jt.core.def.model;

import org.eclipse.jt.core.model.ModelService;

/**
 * 模型动作定义接口
 * 
 * @author Jeff Tang
 * 
 */
public interface ModelActionDeclare extends ModelActionDefine,
		ModelInvokeDeclare {
	/**
	 * 动作的脚本
	 * 
	 * @return 返回脚本定义对象
	 */
	public ScriptDeclare getScript();

	/**
	 * 设置模型动作处理器，<br>
	 * 该方法提供给运行时模型设计器使用，模型声名器中不能使用
	 * 
	 * @return 返回旧的处理器
	 */
	public ModelService<?>.ModelActionHandler<?> setHandler(
			ModelService<?>.ModelActionHandler<?> handler);
}
