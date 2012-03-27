package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.query.RelationColumnDefine;
import org.eclipse.jt.core.def.query.RelationRefDeclare;

/**
 * ��ϵ���õ��ڲ����ӿ�
 * 
 * @see org.eclipse.jt.core.def.query.RelationRefDefine
 * 
 * @author Jeff Tang
 * 
 */
interface RelationRef extends RelationRefDeclare, OMVisitable {

	Relation getTarget();

	RelationColumnRefImpl expOf(RelationColumnDefine column);

	RelationColumnRefImpl expOf(String columnName);

	int modCount();

	void increaseModCount();

}
