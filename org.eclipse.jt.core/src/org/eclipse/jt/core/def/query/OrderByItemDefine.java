package org.eclipse.jt.core.def.query;

import org.eclipse.jt.core.def.DefineBase;
import org.eclipse.jt.core.def.exp.ValueExpression;

/**
 * 排序项定义
 * 
 * @author Jeff Tang
 * 
 */
public interface OrderByItemDefine extends DefineBase {

	/**
	 * 返回排序项是否倒序排列
	 * 
	 * @return
	 */
	public boolean isDesc();

	/**
	 * 返回排序项的排序表达式
	 * 
	 * @return
	 */
	public ValueExpression getExpression();
}
