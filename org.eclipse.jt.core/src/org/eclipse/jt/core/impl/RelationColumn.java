package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.query.RelationColumnDeclare;

/**
 * ��ϵ�ж�����ڲ��ӿ�
 * 
 * <p>
 * ֻ�Ǹ���ǽӿ�
 * 
 * @author Jeff Tang
 */
interface RelationColumn extends RelationColumnDeclare {

	Relation getOwner();
}
