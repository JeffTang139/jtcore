package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.query.QueryReferenceDeclare;
import org.eclipse.jt.core.def.query.RelationColumnDefine;

/**
 * 查询引用的内部接口
 * 
 * @author Jeff Tang
 * 
 */
interface QueryRef extends RelationRef, QueryReferenceDeclare {

	public SelectImpl<?, ?> getTarget();

	public SelectColumnRefImpl expOf(RelationColumnDefine column);

	public SelectColumnRefImpl expOf(String columnName);

}
