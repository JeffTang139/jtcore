package org.eclipse.jt.core.def.table;

import org.eclipse.jt.core.def.exp.TableFieldRefExpr;
import org.eclipse.jt.core.def.query.RelationColumnDefine;
import org.eclipse.jt.core.def.query.RelationRefDeclare;

/**
 * 表引用接口
 * 
 * @see org.eclipse.jt.core.def.table.TableReferenceDefine
 * 
 * @author Jeff Tang
 */
public interface TableReferenceDeclare extends TableReferenceDefine,
		RelationRefDeclare {

	public TableDeclare getTarget();

	public TableFieldRefExpr expOf(RelationColumnDefine column);

	public TableFieldRefExpr expOf(String columnName);

}