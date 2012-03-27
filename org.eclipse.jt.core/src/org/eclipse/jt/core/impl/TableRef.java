package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.query.RelationColumnDefine;
import org.eclipse.jt.core.def.table.TableReferenceDeclare;

/**
 * �����õ��ڲ��ӿ�
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
