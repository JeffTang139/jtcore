package org.eclipse.jt.core.def.model;

import java.util.List;

import org.eclipse.jt.core.misc.ObjectBuilder;


/**
 * 脚本上下文，用于与某个线程绑定执行脚本的借口
 * 
 * @author Jeff Tang
 * 
 */
public interface ModelScriptContext<TPreparedData> {
	/**
	 * 获得对应的引擎
	 */
	public ModelScriptEngine<TPreparedData> getEngine();

	/**
	 * 释放上下文
	 */
	public void release();

	public TPreparedData prepareGetter(ModelPropAccessDefine propAccess);

	public Object executeGetter(ModelPropAccessDefine propAccess,
	        TPreparedData preparedData, Object mo);

	public TPreparedData prepareSetter(ModelPropAccessDefine propAccess);

	public void executeSetter(ModelPropAccessDefine propAccess,
	        TPreparedData preparedData, Object mo, Object value);

	public TPreparedData prepareAction(ModelActionDefine actionDefine);

	public void executeAction(ModelActionDefine actionDefine,
	        TPreparedData preparedData, Object mo, Object ao,
	        ModelInvokeDefine trigger, Object triggerAO, Object value);

	public TPreparedData prepareConstructor(ModelConstructorDefine constructor);

	public void executeConstructor(ModelConstructorDefine constructor,
	        TPreparedData preparedData, Object mo, Object ao);

	public TPreparedData prepareConstraint(ModelConstraintDefine constraint);

	public void executeChecker(ModelConstraintDefine constraint,
	        TPreparedData preparedData, Object mo, ModelInvokeDefine trigger,
	        Object triggerAO, Object value);

	public TPreparedData prepareSource(ModelObjSourceDefine sourceDefine);

	public TPreparedData prepareSourceCountOf(ModelObjSourceDefine sourceDefine);

	public <TMO> void executeSource(ModelObjSourceDefine sourceDefine,
	        TPreparedData preparedData, Object ao, int offset, int count,
	        List<TMO> mos, ObjectBuilder<TMO> factory);

	public int executeSourceCountOf(ModelObjSourceDefine sourceDefine,
	        TPreparedData preparedData, Object ao);
}
