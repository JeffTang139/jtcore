package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.query.RelationColumnDefine;
import org.eclipse.jt.core.def.table.TableReferenceDeclare;

/**
 * 表引用的内部接口
 * 
 * @author Jeff Tang
 * 
 */
interface TableRef extends RelationRef, TableReferenceDeclare {

	static final String xml_attr_table = "table";

	TableDefineImpl getTarget();

	TableFieldRefImpl expOf(RelationColumnDefine column);

	TableFieldRefImpl expOf(String relationColumnName);

}
