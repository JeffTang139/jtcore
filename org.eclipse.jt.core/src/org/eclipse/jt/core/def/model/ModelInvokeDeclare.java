package org.eclipse.jt.core.def.model;

import org.eclipse.jt.core.def.ModifiableContainer;
import org.eclipse.jt.core.def.NamedDeclare;
import org.eclipse.jt.core.def.arg.ArgumentableDeclare;

/**
 * 模型调用定义，乃模型属性，动作构造器之基接口
 * 
 * @author Jeff Tang
 * @param <TAO>
 *            调用的参数实体类型，Object代表空的参数
 */
public abstract interface ModelInvokeDeclare extends ModelInvokeDefine,
		NamedDeclare, ArgumentableDeclare {
	/**
	 * 获得字段定义属于的模型定义
	 * 
	 * @return 返回模型定义
	 */
	public ModelDeclare getOwner();

	/**
	 * 设置是否需要权限控制
	 */
	public void setAuthorizable(boolean value);

	/**
	 * 调用开始之前的触发点，包括触发其他的调用或者约束
	 * 
	 * @return 返回触发点集合
	 */
	public ModifiableContainer<? extends InspectPoint> getBeforeInspects();

	/**
	 * 新增前触发点
	 * 
	 * @param action
	 *            前触发的动作
	 * @return 触发点
	 */
	public InspectPoint newBeforeInspect(ModelActionDefine action);

	/**
	 * 新增前触发点
	 * 
	 * @param constraint
	 *            前触发的约束
	 * @return 触发点
	 */
	public InspectPoint newBeforeInspect(ModelConstraintDefine constraint);

	/**
	 * 调用完成之后的触发点，包括触发其他的调用或者约束
	 * 
	 * @return 返回触发点集合
	 */
	public ModifiableContainer<? extends InspectPoint> getAfterInspects();

	/**
	 * 新增后触发点
	 * 
	 * @param action
	 *            后触发的动作
	 * @return 触发点
	 */
	public InspectPoint newAfterInspect(ModelActionDefine action);

	/**
	 * 新增后触发点
	 * 
	 * @param constraint
	 *            后触发的约束
	 * @return 触发点
	 */
	public InspectPoint newAfterInspect(ModelConstraintDefine constraint);

	/**
	 * 调用完成之后的触发点，包括触发其他的调用
	 * 
	 * @return 返回触发点集合
	 */
	public ModifiableContainer<? extends InspectPoint> getFinallyInspects();

	/**
	 * 新增后触发点
	 * 
	 * @param action
	 *            后触发的动作
	 * @return 触发点
	 */
	public InspectPoint newFinallyInspect(ModelActionDefine action);
}
