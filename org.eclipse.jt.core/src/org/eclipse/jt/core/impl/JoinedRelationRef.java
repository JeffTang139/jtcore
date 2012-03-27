package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.query.JoinedRelationRefDeclare;
import org.eclipse.jt.core.def.table.TableJoinType;

/**
 * ���ӵĹ�ϵ����
 * 
 * @author Jeff Tang
 * 
 */
interface JoinedRelationRef extends NodableRelationRef,
		JoinedRelationRefDeclare {

	ConditionalExpr getJoinCondition();

	TableJoinType getJoinType();

	JoinedRelationRef next();

	JoinedRelationRef last();

}
