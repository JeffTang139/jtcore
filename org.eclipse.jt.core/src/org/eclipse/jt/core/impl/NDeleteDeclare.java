package org.eclipse.jt.core.impl;

/**
 * DELETEÉùÃ÷½Úµã
 * 
 * @author Jeff Tang
 * 
 */
class NDeleteDeclare extends NDmlDeclare {
	public final NDeleteStmt body;

	public NDeleteDeclare(Token start, Token end, TString name,
			NParamDeclare[] params, NDeleteStmt delete) {
		super(start, end, name, params);
		this.body = delete;
	}

	public <T> void accept(T visitorContext, SQLVisitor<T> visitor) {
		visitor.visitDeleteDeclare(visitorContext, this);
	}

	@Override
	public String toString() {
		return RenderVisitor.render(this);
	}
}
