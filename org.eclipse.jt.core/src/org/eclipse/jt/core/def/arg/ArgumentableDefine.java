package org.eclipse.jt.core.def.arg;

import org.eclipse.jt.core.def.DefineBase;
import org.eclipse.jt.core.def.NamedElementContainer;

/**
 * 带参数的，参数化的
 * 
 * @author Jeff Tang
 * 
 */
public interface ArgumentableDefine extends DefineBase {
	/**
	 * 获得参数集合
	 * 
	 * @return 返回参数集合
	 */
	public NamedElementContainer<? extends ArgumentDefine> getArguments();

	/**
	 * 获取参数对象的类
	 * 
	 * @return 返回参数实体对象的类
	 */
	public Class<?> getAOClass();

	/**
	 * 创建参数对象
	 * 
	 * @return 返回新建的参数对象
	 */
	public Object newAO();

	/**
	 * 将值列表，转换成对应的参数对象
	 */
	public Object newAO(Object... args);

}
