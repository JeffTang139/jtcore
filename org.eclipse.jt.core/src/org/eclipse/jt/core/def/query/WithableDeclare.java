package org.eclipse.jt.core.def.query;

import org.eclipse.jt.core.def.ModifiableNamedElementContainer;

/**
 * 支持with定义的
 * 
 * @author Jeff Tang
 * 
 */
public interface WithableDeclare extends WithableDefine {

	public ModifiableNamedElementContainer<? extends DerivedQueryDeclare> getWiths();

	/**
	 * 使用with子句,增加临时结果集
	 * 
	 * @param name
	 *            临时结果集的名称,不能与其他临时结果集名称重复
	 * @return
	 */
	public DerivedQueryDeclare newWith(String name);
}
