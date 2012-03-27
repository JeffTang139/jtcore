package org.eclipse.jt.core.def.query;

/**
 * 集合运算
 * 
 * @author Jeff Tang
 * 
 */
public interface SetOperateDefine {

	/**
	 * 获取集合运算符
	 * 
	 * @return
	 */
	public SetOperator getOperator();

	/**
	 * 获取集合运算对象
	 * 
	 * @return
	 */
	public DerivedQueryDefine getTarget();
}
