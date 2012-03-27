package org.eclipse.jt.core.impl;

final class DeleteSql extends Sql {

	private final void deleteSingle(DBLang lang, DeleteStatementImpl delete,
			DeleteStatusVisitor status) {
		final TableDefineImpl target = delete.moTableRef.target;
		ISqlDeleteBuffer buffer = lang.sqlbuffers().delete(
				target.primary.namedb(),
				Render.aliasOf(delete.moTableRef, target.primary));
		MoJoinedRelationRef join = delete.moTableRef.getJoins();
		if (join != null) {
			join.render(buffer.target(), status);
		}
		if (delete.condition != null) {
			delete.condition.render(buffer.where(), status);
		}
		this.build(buffer);
	}

	private final void deleteUsingCursor(DBLang lang,
			DeleteStatementImpl delete, DeleteStatusVisitor status) {
		final TableDefineImpl target = delete.moTableRef.target;
		ISqlSegmentBuffer buffer = lang.sqlbuffers().segment();
		ISqlCursorLoopBuffer cursor = buffer.cursorLoop("SC", true);
		delete.render(cursor.query().select(), status);
		cursor.query().select().newColumn("DUMMY").load(1);
		cursor.declare("DUMMY", IntType.TYPE);
		for (int i = 0, c = target.dbTables.size(); i < c; i++) {
			DBTableDefineImpl dbTable = target.dbTables.get(i);
			ISqlDeleteBuffer ds = cursor.delete(dbTable.namedb(),
					Render.aliasOf(delete.moTableRef, dbTable));
			ds.whereCurrentOf("SC");
		}
		this.build(buffer);
	}

	// 没有依赖,多次delete
	private final void deleteUsingCompound(DBLang lang,
			DeleteStatementImpl delete, DeleteStatusVisitor status) {
		final TableDefineImpl target = delete.moTableRef.target;
		DBTableDefineImpl last = status.usage.firstTable();
		ISqlSegmentBuffer buffer = lang.sqlbuffers().segment();
		for (int i = 0, c = target.dbTables.size(); i < c; i++) {
			DBTableDefineImpl dbTable = target.dbTables.get(i);
			if (dbTable == last) {
				continue;
			}
			this.deleteSingleFor(buffer, delete, dbTable, status);
		}
		if (last != null) {
			this.deleteSingleFor(buffer, delete, last, status);
		}
		this.build(buffer);
	}

	// mysql支持的,delete删除多表
	private final void deleteUsingMultipleDelete(
			ISqlDeleteMultiCommandFactory dmcf, DeleteStatementImpl delete,
			DeleteStatusVisitor status) {
		final MoRootTableRef tableRef = delete.moTableRef;
		final TableDefineImpl table = tableRef.target;
		final String alias = Render.aliasOf(tableRef, table.primary);
		ISqlDeleteMultiBuffer buffer = dmcf.deleteMulti(table.primary.namedb(),
				alias);
		ISqlTableRefBuffer from = buffer.target();
		for (int i = 1, c = table.dbTables.size(); i < c; i++) {
			DBTableDefineImpl j = table.dbTables.get(i);
			String ja = Render.aliasOf(tableRef, j);
			Render.renderRecidEqJoin(from, alias, j.namedb(), ja);
			buffer.from(ja);
		}
		tableRef.render(from, status);
		if (delete.condition != null) {
			delete.condition.render(buffer.where(), status);
		}
		this.build(buffer);
	}

	private final void deleteSingleFor(ISqlSegmentBuffer buffer,
			DeleteStatementImpl delete, DBTableDefineImpl dbTable,
			DeleteStatusVisitor status) {
		final String alias = Render.aliasOf(delete.moTableRef, dbTable);
		ISqlDeleteBuffer ds = buffer.delete(dbTable.namedb(), alias);
		ISqlTableRefBuffer trb = ds.target();
		TableUsage usage = status.usageOf(delete.moTableRef);
		if (usage != null) {
			for (DBTableDefineImpl tojoin : usage.tables()) {
				if (tojoin != dbTable) {
					Render.renderRecidEqJoin(trb, alias, tojoin.namedb(),
							Render.aliasOf(delete.moTableRef, tojoin));
				}
			}
		}
		MoJoinedRelationRef join = delete.moTableRef.getJoins();
		if (join != null) {
			join.render(trb, status);
		}
		if (delete.condition != null) {
			delete.condition.render(ds.where(), status);
		}
	}

	DeleteSql(DBLang lang, DeleteStatementImpl delete) {
		final DeleteStatusVisitor visitor = new DeleteStatusVisitor(delete);
		delete.visit(visitor, null);
		final int tbCount = delete.moTableRef.target.dbTables.size();
		if (tbCount == 1) {
			this.deleteSingle(lang, delete, visitor);
		} else {
			ISqlDeleteMultiCommandFactory dmcf = lang.sqlbuffers().getFeature(
					ISqlDeleteMultiCommandFactory.class);
			if (dmcf != null) {
				this.deleteUsingMultipleDelete(dmcf, delete, visitor);
			} else if (visitor.usage.tableCount() > 1) {
				this.deleteUsingCursor(lang, delete, visitor);
			} else {
				this.deleteUsingCompound(lang, delete, visitor);
			}
		}
	}

	static final class DeleteStatusVisitor extends TableUsages {

		final TableDefineImpl target;

		final TableUsage usage;

		DeleteStatusVisitor(DeleteStatementImpl delete) {
			this.target = delete.moTableRef.target;
			this.usage = new TableUsage(delete.moTableRef);
		}

		@Override
		public void visitTableFieldRef(TableFieldRefImpl expr, Object context) {
			super.visitTableFieldRef(expr, context);
			if (expr.field.owner == this.target) {
				this.usage.use(expr.field.getDBTable());
			}
		}
	}
}