package org.eclipse.jt.core.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.eclipse.jt.core.exception.InvalidStatementDefineException;


final class RowUpdateSql extends Sql {

	@SuppressWarnings("serial")
	public static final class UpdatePart extends
			LinkedHashMap<TableFieldDefineImpl, ArgumentReserver> {

		public final DBTableDefineImpl dbTable;
		public final TableFieldDefineImpl c_recid;
		public final ArgumentReserver a_recid;

		public UpdatePart(DBTableDefineImpl dbTable, QueryColumnImpl recid) {
			this.dbTable = dbTable;
			this.c_recid = dbTable.owner.f_recid;
			this.a_recid = new ArgumentReserver(recid.field,
					this.c_recid.getType());
		}

		public final ArgumentReserver put(TableFieldDefineImpl field,
				QueryColumnImpl qc) {
			return this.put(field,
					new ArgumentReserver(qc.field, field.getType()));
		}
	}

	private static final String alias = "T";

	RowUpdateSql(DBLang lang, QueryStatementBase statement) {
		statement.validate();
		final ArrayList<RowUpdateSql.UpdatePart> parts = new ArrayList<RowUpdateSql.UpdatePart>();
		final HashSet<TableDefineImpl> tables = new HashSet<TableDefineImpl>();
		RowUpdateSql.UpdatePart part = null;
		for (QuRelationRef relationRef : statement.rootRelationRef()) {
			if (statement.supportModify(relationRef)) {
				final QuTableRef tableRef = (QuTableRef) relationRef;
				final TableDefineImpl table = tableRef.getTarget();
				if (tables.contains(table)) {
					throw Render.duplicateModifyTable(statement, table);
				}
				tables.add(table);
				final QueryColumnImpl recid = statement.findEqualColumn(
						tableRef, table.f_recid);
				if (recid == null) {
					throw Render.noRecidColumnForTable(statement, table);
				}
				for (DBTableDefineImpl dbTable : table.dbTables) {
					for (QueryColumnImpl qc : statement.columns) {
						TableFieldDefineImpl field = Render
								.detectUpdateFieldFor(qc, tableRef, dbTable);
						if (field != null) {
							if (part == null || part.dbTable != dbTable) {
								part = new RowUpdateSql.UpdatePart(dbTable,
										recid);
								parts.add(part);
							}
							if (part.put(field, qc) != null) {
								throw new InvalidStatementDefineException(
										"重复的修改列值");
							}
						}
					}
				}
			}
		}
		if (parts.size() == 0) {
			throw Render.modifyTableNotSupport(statement);
		} else if (parts.size() == 1) {
			part = parts.get(0);
			ISqlUpdateBuffer update = lang.sqlbuffers().update(
					part.dbTable.namedb(), alias, false);
			update(part, update, null);
			this.build(update);
		} else {
			ISqlSegmentBuffer buffer = lang.sqlbuffers().segment();
			for (RowUpdateSql.UpdatePart p : parts) {
				update(p, null, buffer);
			}
			this.build(buffer);
		}
	}

	private static final void update(RowUpdateSql.UpdatePart part,
			ISqlUpdateBuffer update, ISqlSegmentBuffer segment) {
		if (update == null) {
			update = segment.update(part.dbTable.namedb(), alias, false);
		}
		for (Entry<TableFieldDefineImpl, ArgumentReserver> e : part.entrySet()) {
			TableFieldDefineImpl field = e.getKey();
			update.newValue(field.namedb()).loadVar(e.getValue());
		}
		ISqlExprBuffer where = update.where();
		where.loadField(alias, part.c_recid.namedb());
		where.loadVar(part.a_recid);
		where.eq();
	}
}