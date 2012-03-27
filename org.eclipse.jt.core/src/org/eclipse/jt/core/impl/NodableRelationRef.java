package org.eclipse.jt.core.impl;

/**
 * 作为树节点的关系引用
 * 
 * @author Jeff Tang
 * 
 */
interface NodableRelationRef extends RelationRef, RelationJoinable {

	NodableRelationRef next();

	NodableRelationRef last();

	void setNext(NodableRelationRef next);

	JoinedRelationRef getJoins();
}