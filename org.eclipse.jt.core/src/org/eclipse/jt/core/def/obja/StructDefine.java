package org.eclipse.jt.core.def.obja;

import org.eclipse.jt.core.def.NamedDefine;
import org.eclipse.jt.core.def.NamedElementContainer;
import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.type.ObjectDataType;
import org.eclipse.jt.core.type.TupleType;

/**
 * 结构定义
 * 
 * @author Jeff Tang
 * 
 */
public interface StructDefine extends NamedDefine, ObjectDataType, TupleType {
	/**
	 * 获得字段定义列表
	 * 
	 * @return 返回字段定义列表
	 */
	public NamedElementContainer<? extends StructFieldDefine> getFields();

	/**
	 * 尝试转换
	 * 
	 * @param obj
	 *            需要被转换的对象，不可为空
	 * @return 返回null表示转换失败
	 * @exception NullArgumentException
	 *                obj为null
	 */
	public Object tryConvert(Object convertFrom) throws NullArgumentException;

	/**
	 * 检查是否是结构定义对应的对象实例
	 */
	public boolean isInstance(Object obj);
}
