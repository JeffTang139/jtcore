package org.eclipse.jt.core.impl;


/**
 * INSERT VALUES×Ó¾ä½Úµã
 * 
 * @author Jeff Tang
 * 
 */
class NInsertValues extends NInsertSource {
	public final TString[] columns;
	public final NValueExpr[] values;

	public NInsertValues(Token start, Token end, TString[] columns,
			NValueExpr[] values) {
		super(start, end);
		this.columns = columns;
		this.values = values;
	}

	public <T> void accept(T visitorContext, SQLVisitor<T> visitor) {
		visitor.visitInsertValues(visitorContext, this);
	}
}
