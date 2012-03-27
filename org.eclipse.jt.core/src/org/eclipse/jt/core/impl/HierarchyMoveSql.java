package org.eclipse.jt.core.impl;

import static org.eclipse.jt.core.impl.TableDefineImpl.FIELD_DBNAME_RECID;

import org.eclipse.jt.core.type.TypeFactory;


final class HierarchyMoveSql extends Sql {

	static final String FROM = "p__from";
	static final String TO = "p__to";
	// 即target的path
	static final String NEWBASE = "p__newbase";
	static final String MINPATH = "p__minpath";
	static final String MAXPATH = "p__maxpath";
	static final String BASELEN = "p__baselen";

	StrongRefParameter from = new StrongRefParameter();
	StrongRefParameter to = new StrongRefParameter();

	HierarchyMoveSql(DBLang lang, HierarchyDefineImpl hierarchy) {
		ISqlSegmentBuffer buffer = lang.sqlbuffers().segment();
		buffer.declare(FROM, GUIDType.TYPE);
		buffer.declare(TO, GUIDType.TYPE);
		final int pl = hierarchy.getPathLength();
		buffer.declare(MINPATH, TypeFactory.VARBINARY(pl));
		buffer.declare(MAXPATH, TypeFactory.VARBINARY(pl));
		buffer.declare(NEWBASE, TypeFactory.VARBINARY(pl));
		buffer.declare(BASELEN, IntType.TYPE);
		validatePrimaryExistsRecid(buffer, hierarchy, FROM);
		validatePrimaryExistsRecid(buffer, hierarchy, TO);
		ensreuHierarchyExistsRecidAndSetPath(buffer, hierarchy, TO, NEWBASE);
		ISqlConditionBuffer ifs = buffer.ifThenElse();
		existsRecid(ifs.newWhen(), hierarchy, FROM, false);
		// then move recur
		// else move only
		// HCL
	}

	private static final void existsRecid(ISqlExprBuffer buffer,
			HierarchyDefineImpl hierarchy, String recid, boolean not) {
		ISqlSelectBuffer select = buffer.subQuery();
		whereRecidEqParam(select, hierarchy.tableName(), recid);
		select.newColumn("C").load(1);
		buffer.predicate(SqlPredicate.EXISTS, 1);
		if (not) {
			buffer.not();
		}
	}

	private static final void whereRecidEqParam(ISqlSelectBuffer buffer,
			String tableName, String recid) {
		buffer.newTableRef(tableName, ALIAS);
		buffer.where().loadField(ALIAS, FIELD_DBNAME_RECID).loadVar(recid).eq();
	}

	private static final String ALIAS = "T";

	private static final void validatePrimaryExistsRecid(
			ISqlSegmentBuffer buffer, HierarchyDefineImpl hierarchy,
			String recid) {
		ISqlConditionBuffer ifs = buffer.ifThenElse();
		ISqlExprBuffer when = ifs.newWhen();
		existsRecid(when, hierarchy, recid, true);
		// HCL 相比直接退出过程,更好的处理是抛出异常
		ifs.newThen().exit();
	}

	private static final byte[] ZERO = new byte[] { 0 };

	static final void ensreuHierarchyExistsRecidAndSetPath(
			ISqlSegmentBuffer buffer, HierarchyDefineImpl hierarchy,
			String recid, String path) {
		ISqlConditionBuffer ifs = buffer.ifThenElse();
		existsRecid(ifs.newWhen(), hierarchy, recid, false);
		assignPathValue(ifs.newThen(), hierarchy, recid, path);
		insertDefaultHierarchy(ifs.elseThen(), hierarchy, recid, path);
	}

	static final void assignPathValue(ISqlSegmentBuffer buffer,
			HierarchyDefineImpl hierarchy, String recid, String path) {
		ISqlSelectIntoBuffer si = buffer.selectInto();
		si.newTable(hierarchy.tableName(), ALIAS);
		si.where().loadField(ALIAS, HierarchyDefineImpl.COLUMN_NAME_RECID)
				.loadVar(recid).eq();
		si.newColumn(path).loadField(ALIAS,
				HierarchyDefineImpl.COLUMN_NAME_PATH);
	}

	static final void insertDefaultHierarchy(ISqlSegmentBuffer buffer,
			HierarchyDefineImpl hierarchy, String recid, String path) {
		ISqlInsertBuffer insert = buffer.insert(hierarchy.tableName());
		insert.newField(HierarchyDefineImpl.COLUMN_NAME_RECID);
		insert.newValue().loadVar(recid);
		insert.newField(HierarchyDefineImpl.COLUMN_NAME_PATH);
		insert.newValue().load(ZERO).loadVar(recid)
				.func(SqlFunction.BIN_CONCAT, 2);
		buffer.assign(path).load(ZERO).loadVar(recid)
				.func(SqlFunction.BIN_CONCAT, 2);
	}

	static final void moveMoveFromOnly(ISqlSegmentBuffer buffer) {

	}

	static final void moveFromWithDescendant(ISqlSegmentBuffer buffer) {
	}
}