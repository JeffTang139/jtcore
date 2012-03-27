package org.eclipse.jt.core.def.exp;

import org.eclipse.jt.core.def.query.QuRelationRefDefine;

/**
 * 级次谓词表达式
 * 
 * @author Jeff Tang
 * 
 */
public interface HierarchyPredicateExpression extends ConditionalExpression {

	/**
	 * 返回谓词
	 */
	public HierarchyPredicate getPredicate();

	/**
	 * 返回源表引用
	 */
	public QuRelationRefDefine getSource();

	/**
	 * 返回目标表引用
	 */
	public QuRelationRefDefine getTarget();

	/**
	 * 返回谓词的级次值
	 */
	public ValueExpression getLevel();

}
