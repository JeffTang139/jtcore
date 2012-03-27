package org.eclipse.jt.core.def.query;

import org.eclipse.jt.core.def.DefineBase;
import org.eclipse.jt.core.def.exp.ValueExpression;

/**
 * 查询分组规则定义
 * 
 * @author Jeff Tang
 * 
 */
public interface GroupByItemDefine extends DefineBase {

	/**
	 * 获取分组
	 */
	public ValueExpression getExpression();
}
