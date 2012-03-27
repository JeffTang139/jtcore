package org.eclipse.jt.core.def.model;

import org.eclipse.jt.core.def.ModifiableContainer;

/**
 * 模型属性定义
 * 
 * @author Jeff Tang
 * 
 */
public interface ModelPropertyDeclare extends ModelPropertyDefine,
        ModelInvokeDeclare {
	/**
	 * 设置该属性的赋值是否会引发模型状态的变化
	 */
	public void setStateEffective(boolean value);

	/**
	 * 获取属性设置器信息
	 * 
	 * @return 返回设置器信息
	 */
	public ModelPropAccessDeclare getSetterInfo();

	/**
	 * 获取属性读取器信息
	 * 
	 * @return 返回读取器信息
	 */
	public ModelPropAccessDeclare getGetterInfo();

	/**
	 * 设置引用的模型
	 */
	public void setModelReference(ModelReferenceDefine value);

	/**
	 * 设置引用属性
	 */
	public void setPropertyReference(ModelPropertyDefine value);

	/**
	 * 属性值改变后触发点，包括触发其他的调用或者约束
	 * 
	 * @return 返回触发点集合
	 */
	public ModifiableContainer<? extends InspectPoint> getChangedInspects();

	/**
	 * 新增属性值改变后触发点
	 * 
	 * @param action
	 *            属性改变后触发的动作
	 * @return 触发点
	 */
	public InspectPoint newChangedInspect(ModelActionDefine action);

	/**
	 * 新增属性值改变后触发点
	 * 
	 * @param constraint
	 *            属性改变后触发的约束
	 * @return 触发点
	 */
	public InspectPoint newChangedInspect(ModelConstraintDefine constraint);
}
