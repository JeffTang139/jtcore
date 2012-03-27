package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.ObjectQuerier;
import org.eclipse.jt.core.def.MetaElement;
import org.eclipse.jt.core.def.MetaElementType;

public class TableDeclareStub implements MetaElement {
	NTablePrimary mergedPrimary;
	NTableExtend[] mergedExtend;
	NTableRelation[] mergedRelations;
	private final SQLTableContext visitorContext;

	public TableDeclareStub(SQLTableContext visitorContext) {
		this.visitorContext = visitorContext;
	}

	public TableDefineImpl getTable() {
		return this.visitorContext.table;
	}

	public MetaElementType getMetaElementType() {
		return MetaElementType.TABLE;
	}

	public String getDescription() {
		return this.visitorContext.table.name;
	}

	public String getName() {
		return this.visitorContext.table.name;
	}

	public String getTitle() {
		return this.visitorContext.table.name;
	}

	public void fillRelations(ObjectQuerier oQuerier) {
		this.visitorContext.querier = oQuerier;
		SQLTableVisitor.VISITOR.fillRelations(this.visitorContext, this);
	}
}
