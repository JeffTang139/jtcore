package org.eclipse.jt.core.def.model;

/**
 * 触发点定义，定义了对动作和约束的触发
 * 
 * @author Jeff Tang
 * 
 */
public interface InspectPoint {
	/**
	 * 尝试返回对应的动作定义
	 * 
	 * @return 尝试返回对应的动作定义或者null
	 */
	public ModelActionDefine asAction();

	/**
	 * 尝试返回对应的约束定义
	 * 
	 * @return 尝试返回对应的约束定义或者null;
	 */
	public ModelConstraintDefine asConstraint();
}
