package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.query.QuJoinedRelationRefDeclare;

/**
 * 查询定义中使用的连接引用的基类
 * 
 * @author Jeff Tang
 * 
 */
interface QuJoinedRelationRef extends QuJoinedRelationRefDeclare,
		QuRelationRef, JoinedRelationRef, Iterable<QuJoinedRelationRef> {

	QuJoinedQueryRef castAsQueryRef();

	QuJoinedTableRef castAsTableRef();

	QuRelationRef parent();

	QuJoinedRelationRef next();

	QuJoinedRelationRef last();

	/**
	 * 目标关系引用增加当前为样例的连接引用,包括递归的join及next
	 * 
	 * @param from
	 *            目标关系定义
	 * @param args
	 *            参数容器
	 */
	void cloneTo(QuRelationRef from, ArgumentOwner args);

	void render(ISqlRelationRefBuffer buffer, TableUsages usages);
}
