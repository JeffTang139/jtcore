package org.eclipse.jt.core.impl;

/**
 * INSERTÉùÃ÷½Úµã
 * 
 * @author Jeff Tang
 * 
 */
class NInsertDeclare extends NDmlDeclare {
	public final NInsertStmt body;

	public NInsertDeclare(Token start, Token end, TString name,
			NParamDeclare[] params, NInsertStmt body) {
		super(start, end, name, params);
		this.body = body;
	}

	public <T> void accept(T visitorContext, SQLVisitor<T> visitor) {
		visitor.visitInsertDeclare(visitorContext, this);
	}

	@Override
	public String toString() {
		return RenderVisitor.render(this);
	}
}
