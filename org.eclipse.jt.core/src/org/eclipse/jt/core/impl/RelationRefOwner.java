package org.eclipse.jt.core.impl;

/**
 * ��ϵ���ö����������
 * 
 * @author Jeff Tang
 */
interface RelationRefOwner {

	RelationRef getRelationRef(String name);

	RelationRef findRelationRef(String name);

}
