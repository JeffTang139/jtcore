package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.query.RelationColumnDefine;
import org.eclipse.jt.core.def.query.RelationRefDeclare;

/**
 * 关系引用的内部基接口
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
