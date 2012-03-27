package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.exception.InvalidStatementDefineException;

final class InsertSql extends Sql {

	static final EFilter<SelectColumnImpl<?, ?>, DBTableDefineImpl> filter = new EFilter<SelectColumnImpl<?, ?>, DBTableDefineImpl>() {

		public boolean accept(SelectColumnImpl<?, ?> column,
				DBTableDefineImpl dbTable) {
			return !InsertSql.isInsertColumnFor(column.name, dbTable);
		}
	};

	private final void single(DBLang lang, InsertStatementImpl insert,
			TableUsages usages) {
		final TableDefineImpl target = insert.moTableRef.target;
		final NamedDefineContainerImpl<DerivedQueryColumnImpl> columns = insert.values.columns;
		ISqlInsertBuffer buffer = lang.sqlbuffers().insert(
				target.primary.namedb());
		for (int i = 0, c = columns.size(); i < c; i++) {
			buffer.newField(target.fields.get(columns.get(i).name).namedb());
		}
		if (insert.isSubqueried()) {
			insert.values.render(buffer.select(), usages);
		} else {
			for (int i = 0, c = columns.size(); i < c; i++) {
				columns.get(i).value().render(buffer.newValue(), usages);
			}
		}
		this.build(buffer);
	}

	private final void cursor(DBLang lang, InsertStatementImpl insert,
			TableUsages usages) {
		final TableDefineImpl target = insert.moTableRef.target;
		final NamedDefineContainerImpl<DerivedQueryColumnImpl> columns = insert.values.columns;
		final ISqlSegmentBuffer buffer = lang.sqlbuffers().segment();
		final ISqlCursorLoopBuffer cursor = buffer.cursorLoop("SC", true);
		insert.values.render(cursor.query().select(), usages);
		for (int i = 0, c = insert.values.columns.size(); i < c; i++) {
			DerivedQueryColumnImpl column = insert.values.columns.get(i);
			// HCL
			cursor.declare(column.name, target.fields.get(column.name)
					.getType());
		}
		if (insert.values.rootRelationRef() == null) {
			cursor.query().select().fromDummy();
		}
		for (int i = 0; i < target.dbTables.size(); i++) {
			DBTableDefineImpl dbTable = target.dbTables.get(i);
			ISqlInsertBuffer si = cursor.insert(dbTable.name);
			for (int j = 0; j < columns.size(); j++) {
				DerivedQueryColumnImpl column = columns.get(j);
				if (InsertSql.isInsertColumnFor(column.name, dbTable)) {
					si.newField(target.fields.get(column.name).namedb());
					si.newValue().loadVar(column.name);
				}
			}
		}
		this.build(buffer);
	}

	private final void multiple(DBLang lang, InsertStatementImpl insert,
			TableUsages usages) {
		final TableDefineImpl target = insert.moTableRef.target;
		final NamedDefineContainerImpl<DerivedQueryColumnImpl> columns = insert.values.columns;
		final ISqlSegmentBuffer buffer = lang.sqlbuffers().segment();
		for (int i = 0; i < target.dbTables.size(); i++) {
			DBTableDefineImpl dbTable = target.dbTables.get(i);
			ISqlInsertBuffer si = buffer.insert(dbTable.name);
			if (insert.isSubqueried()) {
				for (int j = 0; j < columns.size(); j++) {
					DerivedQueryColumnImpl column = columns.get(j);
					if (InsertSql.isInsertColumnFor(column.name, dbTable)) {
						si.newField(target.fields.get(column.name).namedb());
					}
				}
				insert.values.renderFilterColumnFrom(si.select(), usages, InsertSql.filter,
						dbTable);
			} else {
				for (int j = 0; j < columns.size(); j++) {
					DerivedQueryColumnImpl column = columns.get(j);
					if (InsertSql.isInsertColumnFor(column.name, dbTable)) {
						si.newField(target.fields.get(column.name).namedb());
						column.value().render(si.newValue(), usages);
					}
				}
			}
		}
		this.build(buffer);
	}

	static final boolean isInsertColumnFor(String field,
			DBTableDefineImpl dbTable) {
		TableFieldDefineImpl f = dbTable.owner.fields.get(field);
		return f.isRECID() || f.getDBTable() == dbTable;
	}

	InsertSql(DBLang lang, InsertStatementImpl insert) {
		if (insert.values.columns.size() == 0) {
			throw new InvalidStatementDefineException("插入语句定义[" + insert.name
					+ "]未定义插入值。");
		}
		final InsertStatusVisitor visitor = new InsertStatusVisitor();
		insert.visit(visitor, null);
		if (insert.values.columns.find(TableDefineImpl.FIELD_NAME_RECID) == null) {
			throw new InvalidStatementDefineException("插入语句定义[" + insert.name
					+ "]未定义RECID字段的插入值。");
		}
		final int tbCount = insert.moTableRef.target.dbTables.size();
		if (tbCount == 1) {
			this.single(lang, insert, visitor);
		} else if (visitor.isValuesNonDeterministic()) {
			this.cursor(lang, insert, visitor);
		} else {
			this.multiple(lang, insert, visitor);
		}
	}

	final class InsertStatusVisitor extends TableUsages {

		private boolean isValuesNonDeterministic;

		final boolean isValuesNonDeterministic() {
			return this.isValuesNonDeterministic;
		}

		@Override
		public void visitOperateExpr(OperateExpr expr, Object context) {
			super.visitOperateExpr(expr, context);
			if (expr.isNonDeterministic()) {
				this.isValuesNonDeterministic = true;
			}
		}
	}

}