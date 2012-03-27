package org.eclipse.jt.core.def.model;

import org.eclipse.jt.core.Context;
import org.eclipse.jt.core.def.Container;
import org.eclipse.jt.core.def.NamedDefine;
import org.eclipse.jt.core.def.apc.CheckPointDefine;
import org.eclipse.jt.core.def.arg.ArgumentableDefine;

/**
 * 模型调用定义，乃模型属性，动作构造器之基接口
 * 
 * @author Jeff Tang
 * @param <TAO>
 *            调用的参数实体类型，Object代表空的参数
 */
public abstract interface ModelInvokeDefine extends NamedDefine,
        ArgumentableDefine, CheckPointDefine {
	/**
	 * 获得字段定义属于的模型定义
	 * 
	 * @return 返回模型定义
	 */
	public ModelDefine getOwner();

	/**
	 * 获得是否需要权限控制
	 * 
	 * @return 返回是否权限控制
	 */
	public boolean isAuthorizable();

	/**
	 * 调用开始之前的检查点，包括触发其他的调用或者约束
	 * 
	 * @return 返回检查点集合
	 */
	public Container<? extends InspectPoint> getBeforeInspects();

	/**
	 * 调用完成之后的检查点，包括触发其他的调用或者约束
	 * 
	 * @return 返回检查点集合
	 */
	public Container<? extends InspectPoint> getAfterInspects();

	/**
	 * 调用完成之后的检查点，包括触发其他的调用
	 * 
	 * @return 返回检查点集合
	 */
	public Container<? extends InspectPoint> getFinallyInspects();

	// /////////////////////////////////////////////////////////
	// /////////////////////// runtime /////////////////////////
	// /////////////////////////////////////////////////////////

	/**
	 * 获得调用的有效性
	 * 
	 * @param context
	 *            上下文
	 * @param mo
	 *            模型对象
	 * @return 返回有效性
	 */
	public ModelInvokeValidity getValidity(Context context, Object mo);
}
