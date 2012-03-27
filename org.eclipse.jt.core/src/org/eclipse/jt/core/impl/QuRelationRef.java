package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.query.DerivedQueryDefine;
import org.eclipse.jt.core.def.query.QuRelationRefDeclare;
import org.eclipse.jt.core.def.query.RelationColumnDefine;
import org.eclipse.jt.core.def.table.TableDeclarator;
import org.eclipse.jt.core.def.table.TableDefine;
import org.eclipse.jt.core.def.table.TableRelationDefine;
import org.eclipse.jt.core.misc.SXElement;
import org.eclipse.jt.core.misc.SXRenderable;

/**
 * 查询定义中使用的关系引用基接口
 * 
 * <p>
 * 避免使用泛型实现类.
 * 
 * @author Jeff Tang
 * 
 */
interface QuRelationRef extends NodableRelationRef, QuRelationRefDeclare,
		SXRenderable {

	Relation getTarget();

	QuQueryRef castAsQueryRef();

	QuTableRef castAsTableRef();

	QuJoinedTableRef newJoin(TableDefine target);

	QuJoinedTableRef newJoin(TableDefine target, String name);

	QuJoinedTableRef newJoin(TableDeclarator target);

	QuJoinedTableRef newJoin(TableDeclarator target, String name);

	QuJoinedTableRef newJoin(TableRelationDefine relation);

	QuJoinedTableRef newJoin(TableRelationDefine sample, String name);

	QuJoinedQueryRef newJoin(DerivedQueryDefine query);

	QuJoinedQueryRef newJoin(DerivedQueryDefine query, String name);

	SelectColumnImpl<?, ?> newColumn(RelationColumnDefine column);

	SelectColumnImpl<?, ?> newColumn(RelationColumnDefine column, String name);

	RelationColumnRefImpl expOf(RelationColumnDefine column);

	SelectImpl<?, ?> getOwner();

	QuJoinedRelationRef getJoins();

	QuRelationRef next();

	QuRelationRef last();

	QuJoinedRelationRef newJoin0(String name, Relation target);

	/**
	 * 将当前对象及当前对象的子节点与之后的兄弟节点全部render到目标节点内
	 * 
	 * <p>
	 * 连接链表render到子节点joins内
	 * 
	 * @param element
	 */
	void rendTreeInto(SXElement element);

	void validate();

}
