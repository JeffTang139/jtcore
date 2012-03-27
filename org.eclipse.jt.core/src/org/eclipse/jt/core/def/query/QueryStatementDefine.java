package org.eclipse.jt.core.def.query;

import org.eclipse.jt.core.def.Container;
import org.eclipse.jt.core.def.NamedElementContainer;
import org.eclipse.jt.core.type.Type;

/**
 * 查询语句定义
 * 
 * @author Jeff Tang
 * 
 */
public interface QueryStatementDefine extends SelectDefine, StatementDefine,
		WithableDefine, Type {

	public NamedElementContainer<? extends QueryColumnDefine> getColumns();

	/**
	 * 获取排序项规则定义
	 * 
	 * @return 未定义则返回null
	 */
	public Container<? extends OrderByItemDefine> getOrderBys();

}
