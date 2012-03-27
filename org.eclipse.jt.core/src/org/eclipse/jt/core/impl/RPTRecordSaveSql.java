package org.eclipse.jt.core.impl;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

final class RPTRecordSaveSql extends Sql {

	@SuppressWarnings("serial")
	private static final class Fields extends
			LinkedHashMap<TableFieldDefineImpl, StructFieldDefineImpl> {
	}

	private static final Comparator<RPTRecordSetDBTableInfo> sorter = new Comparator<RPTRecordSetDBTableInfo>() {

		public int compare(RPTRecordSetDBTableInfo o1,
				RPTRecordSetDBTableInfo o2) {
			return o1.dbTable.index() - o2.dbTable.index();
		}
	};

	RPTRecordSaveSql(DBLang lang, RPTRecordSetTableInfo tableInfo) {
		ISqlSegmentBuffer buffer = lang.sqlbuffers().segment();
		Fields fields = new Fields();
		ISqlMergeCommandFactory mf = buffer
				.getFeature(ISqlMergeCommandFactory.class);
		ISqlReplaceCommandFactory rf = buffer
				.getFeature(ISqlReplaceCommandFactory.class);
		RPTRecordSetDBTableInfo last = null;
		if (mf != null) {
			Collections.sort(tableInfo, sorter);
			for (RPTRecordSetDBTableInfo dbTableInfo : tableInfo) {
				if (mergeLastCosUpdateKey(dbTableInfo)) {
					last = dbTableInfo;
					continue;
				}
				fillUpdateFieldsExcludeDuplicated(dbTableInfo, fields);
				ISqlMergeBuffer merge = mf.merge(dbTableInfo.dbTable.name,
						TARGET);
				mergeUsingAndOn(merge, tableInfo, dbTableInfo);
				mergeWhenMatched(merge, fields);
				removeRecverAndKeys(tableInfo, fields);
				mergeWhenNotMatched(merge, tableInfo, dbTableInfo, fields);
			}
			if (last != null) {
				updateWhenNoneThenInsert(tableInfo, last, fields, buffer);
			}
		} else if (rf != null) {
			for (RPTRecordSetDBTableInfo dbTableInfo : tableInfo) {
				if (mergeLastCosUpdateKey(dbTableInfo)) {
					last = dbTableInfo;
					continue;
				}
				replace(tableInfo, dbTableInfo, fields, rf);
			}
			if (last != null) {
				replace(tableInfo, last, fields, rf);
			}
		} else {
			for (RPTRecordSetDBTableInfo dbTableInfo : tableInfo) {
				if (mergeLastCosUpdateKey(dbTableInfo)) {
					last = dbTableInfo;
					continue;
				}
				updateWhenNoneThenInsert(tableInfo, dbTableInfo, fields, buffer);
			}
			if (last != null) {
				updateWhenNoneThenInsert(tableInfo, last, fields, buffer);
			}
		}
		this.build(buffer);
	}

	private static final boolean mergeLastCosUpdateKey(
			RPTRecordSetDBTableInfo dbTableInfo) {
		if (dbTableInfo.dbTable.isPrimary()) {
			for (int tfIndex = 0, tfCount = dbTableInfo.size(); tfIndex < tfCount; tfIndex++) {
				if (dbTableInfo.get(tfIndex).tableField.isPrimaryKey()) {
					return true;
				}
			}
		}
		return false;
	}

	private static final void fillUpdateFieldsExcludeDuplicated(
			RPTRecordSetDBTableInfo dbTableInfo, Fields fields) {
		fields.clear();
		for (int tfIndex = 0, tfCount = dbTableInfo.size(); tfIndex < tfCount; tfIndex++) {
			RPTRecordSetFieldImpl rsf = dbTableInfo.get(tfIndex);
			if (rsf.tableField.isRECID()) {
				continue;
			} else if (!fields.containsKey(rsf.tableField)) {
				fields.put(rsf.tableField, rsf.field);
			}
		}
	}

	private static final String TARGET = "target";

	private static final void mergeUsingAndOn(ISqlMergeBuffer merge,
			RPTRecordSetTableInfo tableInfo, RPTRecordSetDBTableInfo dbTableInfo) {
		if (dbTableInfo.dbTable.isPrimary()) {
			merge.usingDummy();
			buildKeyEqualParam(merge.onCondition(), TARGET, tableInfo);
		} else {
			TableFieldDefineImpl recid = tableInfo.table.f_recid;
			ISqlSelectBuffer using = merge.usingSubQuery(SOURCE);
			using.newTableRef(tableInfo.table.primary.namedb(), S_FROM);
			using.newColumn(S_VAL).loadField(S_FROM, recid.namedb());
			buildKeyEqualParam(using.where(), S_FROM, tableInfo);
			merge.onCondition().loadField(TARGET, recid.namedb())
					.loadField(SOURCE, S_VAL).eq();
		}
	}

	private static final String SOURCE = "source";
	private static final String S_FROM = "n";
	private static final String S_VAL = "v";

	private static final void buildKeyEqualParam(ISqlExprBuffer where,
			String alias, RPTRecordSetTableInfo tableInfo) {
		final int c = tableInfo.fields.length;
		for (int ki = 0; ki < c; ki++) {
			StructFieldDefineImpl keySf = tableInfo.getKeyValueField(ki);
			TableFieldDefineImpl field = tableInfo.fields[ki];
			where.loadField(TARGET, field.namedb());
			where.loadVar(arOf(keySf, field.getType()));
			where.eq();
		}
		where.and(c);
	}

	private static final void mergeWhenMatched(ISqlMergeBuffer merge,
			Fields fields) {
		for (Entry<TableFieldDefineImpl, StructFieldDefineImpl> e : fields
				.entrySet()) {
			TableFieldDefineImpl field = e.getKey();
			merge.setValue(field.namedb()).loadVar(
					arOf(e.getValue(), field.getType()));
		}
	}

	private static final void mergeWhenNotMatched(ISqlMergeBuffer merge,
			RPTRecordSetTableInfo tableInfo,
			RPTRecordSetDBTableInfo dbTableInfo, Fields fields) {
		if (dbTableInfo.dbTable.isPrimary() && !recidIsKey(tableInfo)) {
			TableFieldDefineImpl recid = tableInfo.table.f_recid;
			merge.newValue(recid.namedb()).loadVar(
					arOf(tableInfo.recidSf, recid.getType()));
		}
		if (dbTableInfo.dbTable.isPrimary()) {
			TableFieldDefineImpl recver = tableInfo.table.f_recver;
			merge.newValue(recver.namedb()).loadVar(
					arOf(tableInfo.recverSf, recver.getType()));
			for (int i = 0; i < tableInfo.fields.length; i++) {
				TableFieldDefineImpl key = tableInfo.fields[i];
				merge.newValue(key.namedb()).loadVar(
						arOf(tableInfo.getKeyValueField(i), key.getType()));
			}
		}
		for (Entry<TableFieldDefineImpl, StructFieldDefineImpl> e : fields
				.entrySet()) {
			TableFieldDefineImpl field = e.getKey();
			merge.newValue(field.namedb()).loadVar(
					arOf(e.getValue(), field.getType()));
		}
	}

	private static final void removeRecverAndKeys(
			RPTRecordSetTableInfo tableInfo, Fields fields) {
		fields.remove(tableInfo.table.f_recver);
		for (int i = 0; i < tableInfo.fields.length; i++) {
			fields.remove(tableInfo.fields[i]);
		}
	}

	private static final boolean recidIsKey(RPTRecordSetTableInfo tableInfo) {
		for (int i = 0; i < tableInfo.fields.length; i++) {
			if (tableInfo.fields[i].isRECID()) {
				return true;
			}
		}
		return false;
	}

	private static final void updateAt(ISqlSegmentBuffer buffer,
			RPTRecordSetTableInfo tableInfo,
			RPTRecordSetDBTableInfo dbTableInfo, Fields fields) {
		ISqlUpdateBuffer update = buffer.update(dbTableInfo.dbTable.name,
				TARGET, false);
		for (Entry<TableFieldDefineImpl, StructFieldDefineImpl> e : fields
				.entrySet()) {
			TableFieldDefineImpl field = e.getKey();
			update.newValue(field.namedb()).loadVar(
					arOf(e.getValue(), field.getType()));
		}
		if (dbTableInfo.dbTable.isPrimary()) {
			buildKeyEqualParam(update.where(), TARGET, tableInfo);
		} else {
			final TableFieldDefineImpl recid = tableInfo.table.f_recid;
			ISqlExprBuffer where = update.where();
			where.loadField(TARGET, recid.namedb());
			ISqlSelectBuffer subquery = where.subQuery();
			subquery.newTableRef(tableInfo.table.primary.name, S_FROM);
			subquery.newColumn(S_VAL).loadField(S_FROM, recid.namedb());
			buildKeyEqualParam(subquery.where(), S_FROM, tableInfo);
			where.predicate(SqlPredicate.IN, 2);
		}
	}

	private static final void updateCountEqZero(ISqlExprBuffer expr) {
		expr.func(SqlFunction.ROW_COUNT, 0);
		expr.load(0);
		expr.eq();
	}

	private static final void insertAt(ISqlSegmentBuffer segment,
			RPTRecordSetTableInfo tableInfo,
			RPTRecordSetDBTableInfo dbTableInfo, Fields fields) {
		ISqlInsertBuffer insert = segment.insert(dbTableInfo.dbTable.name);
		if (dbTableInfo.dbTable.isPrimary() && !recidIsKey(tableInfo)) {
			TableFieldDefineImpl recid = tableInfo.table.f_recid;
			insert.newField(recid.namedb());
			insert.newValue().loadVar(arOf(tableInfo.recidSf, recid.getType()));
		}
		if (dbTableInfo.dbTable.isPrimary()) {
			TableFieldDefineImpl recver = tableInfo.table.f_recver;
			insert.newField(recver.namedb());
			insert.newValue().loadVar(
					arOf(tableInfo.recverSf, recver.getType()));
			for (int i = 0; i < tableInfo.fields.length; i++) {
				TableFieldDefineImpl key = tableInfo.fields[i];
				insert.newField(key.namedb());
				insert.newValue().loadVar(
						arOf(tableInfo.getKeyValueField(i), key.getType()));
			}
		}
		for (Entry<TableFieldDefineImpl, StructFieldDefineImpl> e : fields
				.entrySet()) {
			TableFieldDefineImpl column = e.getKey();
			insert.newField(column.namedb());
			insert.newValue().loadVar(arOf(e.getValue(), column.getType()));
		}
	}

	private static final void updateWhenNoneThenInsert(
			RPTRecordSetTableInfo tableInfo,
			RPTRecordSetDBTableInfo dbTableInfo, Fields fields,
			ISqlSegmentBuffer buffer) {
		fillUpdateFieldsExcludeDuplicated(dbTableInfo, fields);
		updateAt(buffer, tableInfo, dbTableInfo, fields);
		ISqlConditionBuffer ifs = buffer.ifThenElse();
		updateCountEqZero(ifs.newWhen());
		removeRecverAndKeys(tableInfo, fields);
		insertAt(ifs.newThen(), tableInfo, dbTableInfo, fields);
	}

	private static final void replace(RPTRecordSetTableInfo tableInfo,
			RPTRecordSetDBTableInfo dbTableInfo, Fields fields,
			ISqlReplaceCommandFactory rf) {
		fillUpdateFieldsExcludeDuplicated(dbTableInfo, fields);
		fields.put(tableInfo.table.f_recid, tableInfo.recidSf);
		for (int ki = 0; ki < tableInfo.fields.length; ki++) {
			fields.put(tableInfo.fields[ki], tableInfo.getKeyValueField(ki));
		}
		ISqlReplaceBuffer replace = rf.replace(dbTableInfo.dbTable.name);
		for (Entry<TableFieldDefineImpl, StructFieldDefineImpl> e : fields
				.entrySet()) {
			TableFieldDefineImpl field = e.getKey();
			replace.newField(field.namedb());
			replace.newValue().loadVar(arOf(e.getValue(), field.getType()));
		}
	}
}