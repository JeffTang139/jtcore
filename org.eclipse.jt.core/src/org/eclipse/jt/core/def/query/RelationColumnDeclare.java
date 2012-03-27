package org.eclipse.jt.core.def.query;

import org.eclipse.jt.core.def.NamedDeclare;

/**
 * 关系列定义
 * 
 * @author Jeff Tang
 */
public interface RelationColumnDeclare extends RelationColumnDefine,
		NamedDeclare {

	public RelationDeclare getOwner();
}
