package org.eclipse.jt.core.def.query;

/**
 * ≤Â»Î”Ôæ‰∂®“Â
 * 
 * @see org.eclipse.jt.core.def.query.InsertStatementDefine
 * 
 * @author Jeff Tang
 * 
 */
public interface InsertStatementDeclare extends InsertStatementDefine,
		FieldValueAssignable, ModifyStatementDeclare {

	public DerivedQueryDeclare getInsertValues();

}
