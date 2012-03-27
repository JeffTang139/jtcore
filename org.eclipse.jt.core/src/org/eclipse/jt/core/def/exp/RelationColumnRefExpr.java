package org.eclipse.jt.core.def.exp;

import org.eclipse.jt.core.def.query.RelationColumnDefine;
import org.eclipse.jt.core.def.query.RelationRefDefine;

/**
 * 关系列引用表达式
 * 
 * @author Jeff Tang
 * 
 */
public interface RelationColumnRefExpr extends ValueExpression {

	/**
	 * 获取指向的关系列定义
	 * 
	 * @return
	 */
	public RelationColumnDefine getColumn();

	/**
	 * 获取所在的关系引用定义
	 * 
	 * @return
	 */
	public RelationRefDefine getReference();
}
