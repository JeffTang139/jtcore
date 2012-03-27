package org.eclipse.jt.core.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.eclipse.jt.core.exception.InvalidStatementDefineException;


final class RowInsertSql extends Sql {

	@SuppressWarnings("serial")
	static final class InsertPart extends
			LinkedHashMap<TableFieldDefineImpl, ArgumentReserver> {

		final DBTableDefineImpl dbTable;

		InsertPart(DBTableDefineImpl dbTable) {
			this.dbTable = dbTable;
		}

		final ArgumentReserver put(TableFieldDefineImpl field,
				QueryColumnImpl qc) {
			return this.put(field,
					new ArgumentReserver(qc.field, field.getType()));
		}
	}

	RowInsertSql(DBLang lang, QueryStatementBase statement) {
		statement.validate();
		final ArrayList<RowInsertSql.InsertPart> parts = new ArrayList<RowInsertSql.InsertPart>();
		final HashSet<TableDefineImpl> tables = new HashSet<TableDefineImpl>();
		RowInsertSql.InsertPart part = null;
		for (QuRelationRef relationRef : statement.rootRelationRef()) {
			if (statement.supportModify(relationRef)) {
				final QuTableRef tableRef = (QuTableRef) relationRef;
				final TableDefineImpl table = tableRef.getTarget();
				if (tables.contains(table)) {
					throw Render.duplicateModifyTable(statement, table);
				}
				tables.add(table);
				if (statement.findEqualColumn(tableRef, table.f_recid) == null) {
					throw Render.noRecidColumnForTable(statement, table);
				}
				for (DBTableDefineImpl dbTable : table.dbTables) {
					for (QueryColumnImpl qc : statement.columns) {
						TableFieldDefineImpl field = Render
								.detectInsertColumnFor(qc, tableRef, dbTable);
						if (field != null) {
							if (part == null || part.dbTable != dbTable) {
								part = new RowInsertSql.InsertPart(dbTable);
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
			ISqlInsertBuffer insert = lang.sqlbuffers().insert(
					part.dbTable.namedb());
			insert(part, insert, null);
			this.build(insert);
		} else {
			ISqlSegmentBuffer buffer = lang.sqlbuffers().segment();
			for (RowInsertSql.InsertPart p : parts) {
				insert(p, null, buffer);
			}
			this.build(buffer);
		}
	}

	private static final void insert(RowInsertSql.InsertPart part,
			ISqlInsertBuffer insert, ISqlSegmentBuffer segment) {
		if (insert == null) {
			insert = segment.insert(part.dbTable.namedb());
		}
		for (Entry<TableFieldDefineImpl, ArgumentReserver> e : part.entrySet()) {
			insert.newField(e.getKey().namedb());
			insert.newValue().loadVar(e.getValue());
		}
	}
}