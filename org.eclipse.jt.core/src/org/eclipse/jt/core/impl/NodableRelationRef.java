package org.eclipse.jt.core.impl;

/**
 * ��Ϊ���ڵ�Ĺ�ϵ����
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