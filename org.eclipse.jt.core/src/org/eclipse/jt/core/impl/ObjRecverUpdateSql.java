package org.eclipse.jt.core.impl;

import static org.eclipse.jt.core.impl.TableDefineImpl.FIELD_DBNAME_RECID;
import static org.eclipse.jt.core.impl.TableDefineImpl.FIELD_DBNAME_RECVER;

import java.util.ArrayList;
import java.util.Map.Entry;

import org.eclipse.jt.core.exception.InvalidStatementDefineException;
import org.eclipse.jt.core.impl.RowUpdateSql.UpdatePart;


final class ObjRecverUpdateSql extends Sql {

	final ArgumentReserver a_recid;

	final StrongRefParameter recver = new StrongRefParameter();

	ObjRecverUpdateSql(DBLang lang, MappingQueryStatementImpl statement) {
		statement.validate();
		statement.checkModifyRootOnly();
		QuRootTableRef tableRef = (QuRootTableRef) statement.rootRelationRef();
		TableDefineImpl table = tableRef.getTarget();
		QueryColumnImpl recid = statement.findEqualColumn(tableRef,
				table.f_recid);
		if (recid == null) {
			throw Render.noRecidColumnForTable(statement, table);
		}
		this.a_recid = new ArgumentReserver(recid.field,
				table.f_recid.getType());
		ArrayList<RowUpdateSql.UpdatePart> parts = new ArrayList<RowUpdateSql.UpdatePart>();
		RowUpdateSql.UpdatePart part = null;
		for (DBTableDefineImpl dbTable : table.dbTables) {
			for (QueryColumnImpl qc : statement.columns) {
				TableFieldDefineImpl field = Render.detectUpdateFieldFor(qc,
						tableRef, dbTable);
				if (field != null) {
					if (part == null || part.dbTable != dbTable) {
						part = new RowUpdateSql.UpdatePart(dbTable, recid);
						parts.add(part);
					}
					if (part.put(field, qc) != null) {
						throw new InvalidStatementDefineException("重复的修改列值");
					}
				}
			}
		}
		final boolean firstPrimary = parts.get(0).dbTable.isPrimary();
		if (parts.size() == 0) {
			throw Render.modifyTableNotSupport(statement);
		} else if (parts.size() == 1 && firstPrimary) {
			RowUpdateSql.UpdatePart first = parts.get(0);
			ISqlUpdateBuffer update = lang.sqlbuffers().update(
					first.dbTable.namedb(), alias, false);
			this.update(first, update, null);
			this.build(update);
		} else {
			ISqlSegmentBuffer buffer = lang.sqlbuffers().segment();
			if (!firstPrimary) {
				ISqlUpdateBuffer lock = buffer.update(table.primary.namedb(),
						alias, false);
				lock.newValue(FIELD_DBNAME_RECVER).loadField(alias,
						FIELD_DBNAME_RECVER);
				ISqlExprBuffer where = lock.where();
				where.loadField(alias, FIELD_DBNAME_RECID)
						.loadVar(this.a_recid).eq();
				where.loadField(alias, FIELD_DBNAME_RECVER)
						.loadVar(this.recver).eq();
				where.and(2);
				ISqlConditionBuffer ifseg = buffer.ifThenElse();
				ifseg.newWhen().func(SqlFunction.ROW_COUNT, 0).load(1).eq();
				ISqlSegmentBuffer then = ifseg.newThen();
				for (int i = 0; i < parts.size(); i++) {
					this.update(parts.get(i), null, then);
				}
			} else {
				this.update(parts.get(0), null, buffer);
				ISqlConditionBuffer ifs = buffer.ifThenElse();
				ifs.newWhen().func(SqlFunction.ROW_COUNT, 0).load(1).eq();
				ISqlSegmentBuffer then = ifs.newThen();
				for (int i = 1; i < parts.size(); i++) {
					this.update(parts.get(i), null, then);
				}
			}
			this.build(buffer);
		}
	}

	private static final String alias = "T";

	private final void update(UpdatePart part, ISqlUpdateBuffer update,
			ISqlSegmentBuffer segment) {
		if (update == null) {
			update = segment.update(part.dbTable.namedb(), alias, false);
		}
		for (Entry<TableFieldDefineImpl, ArgumentReserver> e : part.entrySet()) {
			update.newValue(e.getKey().namedb()).loadVar(e.getValue());
		}
		ISqlExprBuffer where = update.where();
		where.loadField(alias, TableDefineImpl.FIELD_DBNAME_RECID);
		where.loadVar(this.a_recid);
		where.eq();
		if (part.dbTable.isPrimary()) {
			where.loadField(alias, TableDefineImpl.FIELD_DBNAME_RECVER);
			where.loadVar(this.recver).eq().and(2);
		}
	}
}