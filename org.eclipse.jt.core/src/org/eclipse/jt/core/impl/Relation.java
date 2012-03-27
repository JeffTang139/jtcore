package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.query.RelationDeclare;
import org.eclipse.jt.core.misc.SXRenderable;

/**
 * ��ϵԪ������ڲ��ӿ�
 * 
 * @author Jeff Tang
 */
interface Relation extends RelationDeclare, SXRenderable {

	RelationColumn getColumn(String columnName);

	RelationColumn findColumn(String columnName);
}
