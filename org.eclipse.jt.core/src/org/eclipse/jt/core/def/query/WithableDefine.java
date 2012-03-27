package org.eclipse.jt.core.def.query;

import org.eclipse.jt.core.def.ModifiableNamedElementContainer;

/**
 * 支持with定义的
 * 
 * @author Jeff Tang
 * 
 */
public interface WithableDefine {

	/**
	 * 获取临时结果集的列表
	 * 
	 * @return 未定义则返回null
	 */
	public ModifiableNamedElementContainer<? extends DerivedQueryDefine> getWiths();
}
