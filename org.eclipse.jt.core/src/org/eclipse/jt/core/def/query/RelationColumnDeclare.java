package org.eclipse.jt.core.def.query;

import org.eclipse.jt.core.def.NamedDeclare;

/**
 * ��ϵ�ж���
 * 
 * @author Jeff Tang
 */
public interface RelationColumnDeclare extends RelationColumnDefine,
		NamedDeclare {

	public RelationDeclare getOwner();
}
