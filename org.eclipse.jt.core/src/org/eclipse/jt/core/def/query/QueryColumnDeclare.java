package org.eclipse.jt.core.def.query;

import org.eclipse.jt.core.def.exp.ValueExpression;
import org.eclipse.jt.core.def.obja.StructFieldDefine;

/**
 * 查询语句定义的输出列定义
 * 
 * @see org.eclipse.jt.core.def.query.QueryColumnDefine
 * 
 * @author Jeff Tang
 * 
 */
public interface QueryColumnDeclare extends QueryColumnDefine,
		SelectColumnDeclare {

	public QueryStatementDeclare getOwner();

	/**
	 * 设置列定义的表达式
	 */
	public void setExpression(ValueExpression value);

	/**
	 * 设置映射到的模型的字段
	 * 
	 * @param field
	 *            java实体属性的结构字段定义
	 */
	public void setMapingField(StructFieldDefine field);

	/**
	 * 设置映射到的模型的字段
	 * 
	 * @param structFieldName
	 *            java实体属性的名称(区分大小写)
	 */
	public void setMapingField(String structFieldName);
}
