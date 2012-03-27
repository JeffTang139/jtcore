package org.eclipse.jt.core.impl;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.eclipse.jt.core.impl.RowUpdateSql.UpdatePart;


final class RowSaveSql extends Sql {

	RowSaveSql(DBLang lang, QueryStatementImpl statement) {
		statement.validate();
		final LinkedHashMap<DBTableDefineImpl, UpdatePart> parts = new LinkedHashMap<DBTableDefineImpl, UpdatePart>();
		fillSaveParts(statement, parts);
		if (parts.size() == 0) {
			throw Render.modifyTableNotSupport(statement);
		} else {
			ISqlCommandFactory f = lang.sqlbuffers();
			ISqlMergeCommandFactory mf = lang.sqlbuffers().getFeature(
					ISqlMergeCommandFactory.class);
			ISqlReplaceCommandFactory rf = lang.sqlbuffers().getFeature(
					ISqlReplaceCommandFactory.class);
			if (mf != null) {
				usingMerge(f, mf, parts, this);
			} else if (rf != null) {
				usingReplace(f, rf, parts, this);
			} else {
				updateAndInsert(f, parts, this);
			}
		}
	}

	private static final void fillSaveParts(QueryStatementImpl statement,
			LinkedHashMap<DBTableDefineImpl, UpdatePart> parts) {
		UpdatePart part = null;
		for (QuRelationRef relationRef : statement.rootRelationRef()) {
			if (statement.supportModify(relationRef)) {
				QuTableRef tableRef = (QuTableRef) relationRef;
				TableDefineImpl table = tableRef.getTarget();
				QueryColumnImpl recid = statement.findEqualColumn(tableRef,
						table.f_recid);
				if (recid == null) {
					throw Render.noRecidColumnForTable(statement, table);
				}
				for (DBTableDefineImpl dbTable : table.dbTables) {
					for (int i = 0, c = statement.columns.size(); i < c; i++) {
						QueryColumnImpl qc = statement.columns.get(i);
						TableFieldDefineImpl field = Render
								.detectUpdateFieldFor(qc, tableRef, dbTable);
						if (field != null) {
							if (part == null || part.dbTable != dbTable) {
								part = new UpdatePart(dbTable, recid);
								if (parts.put(dbTable, part) != null) {
									throw Render.duplicateModifyTable(
											statement, table);
								}
							}
							if (part.put(field, qc) != null) {
								throw Render.duplicateModifyColumn();
							}
						}
					}
				}
			}
		}
	}

	private static final void usingMerge(ISqlCommandFactory cf,
			ISqlMergeCommandFactory mf,
			LinkedHashMap<DBTableDefineImpl, UpdatePart> parts, RowSaveSql sql) {
		if (parts.size() == 1) {
			UpdatePart p = parts.entrySet().iterator().next().getValue();
			ISqlMergeBuffer merge = mf.merge(p.dbTable.name,
					Render.rowModifyAlias(p.dbTable));
			merge(p, merge, null);
			sql.build(merge);
		} else {
			ISqlSegmentBuffer buffer = cf.segment();
			for (UpdatePart p : parts.values()) {
				merge(p, null, mf);
			}
			sql.build(buffer);
		}
	}

	private static final void merge(UpdatePart p, ISqlMergeBuffer merge,
			ISqlMergeCommandFactory f) {
		if (merge == null) {
			merge = f.merge(p.dbTable.name, Render.rowModifyAlias(p.dbTable));
		}
		merge.usingDummy();
		merge.onCondition()
				.loadField(Render.rowModifyAlias(p.dbTable), p.c_recid.name)
				.loadVar(p.a_recid).eq();
		merge.newValue(p.dbTable.owner.f_recid.namedb()).loadVar(p.a_recid);
		for (Entry<TableFieldDefineImpl, ArgumentReserver> e : p.entrySet()) {
			merge.newValue(e.getKey().namedb()).loadVar(e.getValue());
			merge.setValue(e.getKey().namedb()).loadVar(e.getValue());
		}
	}

	private static final void usingReplace(ISqlCommandFactory cf,
			ISqlReplaceCommandFactory rf,
			LinkedHashMap<DBTableDefineImpl, UpdatePart> parts, RowSaveSql sql) {
		if (parts.size() == 1) {
			UpdatePart p = parts.entrySet().iterator().next().getValue();
			ISqlReplaceBuffer replace = rf.replace(p.dbTable.name);
			replace(p, replace, rf);
			sql.build(replace);
		} else {
			ISqlSegmentBuffer buffer = cf.segment();
			for (UpdatePart p : parts.values()) {
				replace(p, null, rf);
			}
			sql.build(buffer);
		}
	}

	private static final void replace(UpdatePart p, ISqlReplaceBuffer replace,
			ISqlReplaceCommandFactory f) {
		if (replace == null) {
			replace = f.replace(p.dbTable.name);
		}
		for (Entry<TableFieldDefineImpl, ArgumentReserver> e : p.entrySet()) {
			replace.newField(e.getKey().namedb());
			replace.newValue().loadVar(e.getValue());
		}
	}

	private static final void updateAndInsert(ISqlCommandFactory cf,
			LinkedHashMap<DBTableDefineImpl, UpdatePart> parts, RowSaveSql sql) {
		ISqlSegmentBuffer buffer = cf.segment();
		for (UpdatePart p : parts.values()) {
			final String alias = Render.rowModifyAlias(p.dbTable);
			ISqlUpdateBuffer update = buffer.update(p.dbTable.name, alias,
					false);
			update.where().loadField(alias, p.c_recid.name).loadVar(p.a_recid)
					.eq();
			for (Entry<TableFieldDefineImpl, ArgumentReserver> e : p.entrySet()) {
				update.newValue(e.getKey().namedb()).loadVar(e.getValue());
			}
			ISqlConditionBuffer ifs = buffer.ifThenElse();
			ifs.newWhen().func(SqlFunction.ROW_COUNT, 0).load(0).eq();
			ISqlInsertBuffer insert = ifs.newThen().insert(p.dbTable.name);
			insert.newField(p.c_recid.name);
			insert.newValue().loadVar(p.a_recid);
			for (Entry<TableFieldDefineImpl, ArgumentReserver> e : p.entrySet()) {
				insert.newField(e.getKey().namedb());
				insert.newValue().loadVar(e.getValue());
			}
		}
		sql.build(buffer);
	}

}