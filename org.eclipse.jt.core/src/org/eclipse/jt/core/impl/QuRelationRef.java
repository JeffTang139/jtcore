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
 * ��ѯ������ʹ�õĹ�ϵ���û��ӿ�
 * 
 * <p>
 * ����ʹ�÷���ʵ����.
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
	 * ����ǰ���󼰵�ǰ������ӽڵ���֮����ֵܽڵ�ȫ��render��Ŀ��ڵ���
	 * 
	 * <p>
	 * ��������render���ӽڵ�joins��
	 * 
	 * @param element
	 */
	void rendTreeInto(SXElement element);

	void validate();

}
