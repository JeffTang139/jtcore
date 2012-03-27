package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.query.MoJoinedRelationRefDeclare;

/**
 * �������Ĺ�ϵ����
 * 
 * @author Jeff Tang
 * 
 */
@SuppressWarnings({ "deprecation", "unused" })
interface MoJoinedRelationRef extends MoRelationRef, JoinedRelationRef,
		Iterable<MoJoinedRelationRef>, MoJoinedRelationRefDeclare {

	MoJoinedRelationRef next();

	MoJoinedRelationRef last();

	void render(ISqlRelationRefBuffer buffer, TableUsages usages);

}
