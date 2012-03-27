package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.MetaElementType;
import org.eclipse.jt.core.def.query.DeleteStatementDeclarator;
import org.eclipse.jt.core.def.query.DeleteStatementDeclare;

/**
 * …æ≥˝”Ôæ‰ µœ÷¿‡
 * 
 * @author Jeff Tang
 * 
 */
public final class DeleteStatementImpl extends ConditionalStatementImpl
		implements DeleteStatementDeclare,
		Declarative<DeleteStatementDeclarator> {

	public final DeleteStatementDeclarator getDeclarator() {
		return this.declarator;
	}

	public final MetaElementType getMetaElementType() {
		return MetaElementType.DELETE;
	}

	@Override
	public final String getXMLTagName() {
		return DeleteStatementImpl.xml_element_delete;
	}

	static final String xml_element_delete = "delete-statement";

	final DeleteStatementDeclarator declarator;

	public DeleteStatementImpl(String name, TableDefineImpl table) {
		super(name, table);
		this.declarator = null;
	}

	public DeleteStatementImpl(String name, TableDefineImpl table,
			DeleteStatementDeclarator declarator) {
		super(name, table);
		this.declarator = declarator;
	}

	private DeleteSql sql;

	@Override
	public final Sql getSql(DBAdapterImpl dbAdapter) {
		this.ensurePrepared(dbAdapter.getContext(), false);
		DeleteSql sql = this.sql;
		if (sql == null) {
			synchronized (this) {
				sql = this.sql;
				if (sql == null) {
					this.sql = sql = new DeleteSql(dbAdapter.lang, this);
				}
			}
		}
		return sql;
	}

	@Override
	protected final void doPrepare(DBLang lang) throws Throwable {
		super.doPrepare(lang);
		this.sql = null;
	}

	public final <TContext> void visit(OMVisitor<TContext> visitor,
			TContext context) {
		visitor.visitDeleteStatement(this, context);
	}
}
