package org.eclipse.jt.core.def.arg;

import org.eclipse.jt.core.def.FieldDefine;
import org.eclipse.jt.core.def.ModifiableNamedElementContainer;
import org.eclipse.jt.core.type.DataTypable;
import org.eclipse.jt.core.type.DataType;

/**
 * 带参数的，参数化的
 * 
 * @see org.eclipse.jt.core.def.arg.ArgumentableDefine
 * 
 * @author Jeff Tang
 * 
 */
public interface ArgumentableDeclare extends ArgumentableDefine {

	/**
	 * 获得参数集合
	 * 
	 * @return 返回参数集合
	 */
	public ModifiableNamedElementContainer<? extends ArgumentDeclare> getArguments();

	/**
	 * 新增一个参数
	 */
	public ArgumentDeclare newArgument(String name, DataType type);

	/**
	 * 新增一个参数
	 */
	public ArgumentDeclare newArgument(String name, DataTypable typable);

	/**
	 * 新增一个参数
	 * 
	 * @param sample
	 *            根据该值的名称和类型创建参数定义
	 */
	public ArgumentDeclare newArgument(FieldDefine sample);
}
