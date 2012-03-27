package org.eclipse.jt.core.def.model;

import org.eclipse.jt.core.def.NamedDeclare;
import org.eclipse.jt.core.def.arg.ArgumentableDeclare;
import org.eclipse.jt.core.def.query.MappingQueryStatementDefine;
import org.eclipse.jt.core.model.ModelService;

/**
 * 模型实体源定义，用以返回模型实体列表的定义
 * 
 * @author Jeff Tang
 * 
 */
public interface ModelObjSourceDeclare extends ModelObjSourceDefine,
        NamedDeclare, ArgumentableDeclare {
	/**
	 * 获得字段定义属于的模型定义
	 * 
	 * @return 返回模型定义
	 */
	public ModelDeclare getOwner();

	/**
	 * 构造器的脚本
	 * 
	 * @return 返回脚本定义对象
	 */
	public ScriptDeclare getScript();

	/**
	 * 获得取实体个数的脚本
	 */
	public ScriptDeclare getMOCountOfScript();

	/**
	 * 设置模型实体源提供器，<br>
	 * 
	 * @return 返回旧的实体源提供器
	 */
	public ModelService<?>.ModelObjProvider<?> setProvider(
	        ModelService<?>.ModelObjProvider<?> provider);

	public MappingQueryStatementDefine setMappingQueryRef(MappingQueryStatementDefine ref);
}
