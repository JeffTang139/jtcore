package org.eclipse.jt.core.def.query;

import org.eclipse.jt.core.def.exp.ValueExpression;
import org.eclipse.jt.core.type.DataTypable;

/**
 * 查询语句选择列定义
 * 
 * <p>
 * 表示一个抽象查询定义的输出列
 * 
 * @author Jeff Tang
 * 
 */
public interface SelectColumnDefine extends RelationColumnDefine, DataTypable {

	/**
	 * 获取所属的查询定义
	 * 
	 * @return 查询定义
	 */
	public SelectDefine getOwner();

	/**
	 * 返回列定义的表达式
	 * 
	 * @return 返回列定义的表达式
	 */
	public ValueExpression getExpression();
}
