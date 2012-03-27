package org.eclipse.jt.core.def.query;

import org.eclipse.jt.core.def.NamedDeclare;

/**
 * ��ϵ��Ԫ����
 * 
 * @see org.eclipse.jt.core.def.query.RelationDefine
 * 
 * @author Jeff Tang
 * 
 */
public interface RelationDeclare extends RelationDefine, NamedDeclare {

	public RelationColumnDeclare findColumn(String columnName);

	public RelationColumnDeclare getColumn(String columnName);
}
