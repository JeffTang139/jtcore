package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.query.QuJoinedRelationRefDeclare;

/**
 * ��ѯ������ʹ�õ��������õĻ���
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
	 * Ŀ���ϵ�������ӵ�ǰΪ��������������,�����ݹ��join��next
	 * 
	 * @param from
	 *            Ŀ���ϵ����
	 * @param args
	 *            ��������
	 */
	void cloneTo(QuRelationRef from, ArgumentOwner args);

	void render(ISqlRelationRefBuffer buffer, TableUsages usages);
}
