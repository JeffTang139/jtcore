package org.eclipse.jt.core.impl;

/**
 * 关系引用定义的所有者
 * 
 * @author Jeff Tang
 */
interface RelationRefOwner {

	RelationRef getRelationRef(String name);

	RelationRef findRelationRef(String name);

}
