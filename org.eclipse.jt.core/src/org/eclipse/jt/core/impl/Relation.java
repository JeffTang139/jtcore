package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.query.RelationDeclare;
import org.eclipse.jt.core.misc.SXRenderable;

/**
 * 关系元定义的内部接口
 * 
 * @author Jeff Tang
 */
interface Relation extends RelationDeclare, SXRenderable {

	RelationColumn getColumn(String columnName);

	RelationColumn findColumn(String columnName);
}
