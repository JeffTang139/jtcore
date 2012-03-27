package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.query.RelationColumnDeclare;

/**
 * 关系列定义的内部接口
 * 
 * <p>
 * 只是个标记接口
 * 
 * @author Jeff Tang
 */
interface RelationColumn extends RelationColumnDeclare {

	Relation getOwner();
}
