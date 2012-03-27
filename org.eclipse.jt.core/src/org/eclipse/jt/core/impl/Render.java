package org.eclipse.jt.core.impl;

import static org.eclipse.jt.core.impl.TableDefineImpl.FIELD_DBNAME_RECID;

import org.eclipse.jt.core.def.table.TableJoinType;
import org.eclipse.jt.core.exception.InvalidStatementDefineException;


final class Render {

	private Render() {
	}

	static final ISqlTableRefBuffer renderTableRef(TableRef tableRef,
			ISqlSelectBuffer buffer, TableUsages usages) {
		if (tableRef.getTarget() == TableDefineImpl.DUMMY) {
			buffer.fromDummy();
			return null;
		}
		TableUsage usage = usages.usageOf(tableRef);
		if (usage == null || usage.tableCount() == 0) {
			return buffer.newTableRef(tableRef.getTarget().primary.namedb(),
					tableRef.getName());
		} else if (usage.tableCount() == 1) {
			return buffer.newTableRef(usage.firstTable().namedb(),
					tableRef.getName());
		} else {
			ISqlTableRefBuffer trb = null;
			String left = null;
			for (DBTableDefineImpl dbTable : usage.tables()) {
				if (trb == null) {
					trb = buffer.newTableRef(dbTable.namedb(),
							left = Render.aliasOf(tableRef, dbTable));
				} else {
					renderRecidEqJoin(trb, left, dbTable.namedb(),
							aliasOf(tableRef, dbTable));
				}
			}
			return trb;
		}
	}

	static final ISqlJoinedTableRefBuffer renderJoinedTableRef(
			JoinedTableRef tableRef, ISqlRelationRefBuffer buffer,
			TableUsages usages) {
		if (tableRef.getTarget() == TableDefineImpl.DUMMY) {
			throw new UnsupportedOperationException("DUMMY��������");
		}
		TableUsage usage = usages.usageOf(tableRef);
		if (usage == null || usage.tableCount() == 0) {
			return buffer.joinTable(tableRef.getTarget().primary.namedb(),
					tableRef.getName(), tableRef.getJoinType());
		} else if (usage.tableCount() == 1) {
			return buffer.joinTable(usage.firstTable().namedb(),
					tableRef.getName(), tableRef.getJoinType());
		} else {
			ISqlJoinedTableRefBuffer trb = null;
			String left = null;
			for (DBTableDefineImpl dbTable : usage.tables()) {
				if (trb == null) {
					trb = buffer.joinTable(dbTable.namedb(),
							left = Render.aliasOf(tableRef, dbTable),
							TableJoinType.INNER);
				} else {
					renderRecidEqJoin(trb, left, dbTable.namedb(),
							aliasOf(tableRef, dbTable));
				}
			}
			return trb;
		}
	}

	static final void renderRecidEqJoin(ISqlTableRefBuffer buffer,
			String leftAlias, String rightTableName, String rightAlias) {
		ISqlJoinedTableRefBuffer join = buffer.joinTable(rightTableName,
				rightAlias, TableJoinType.INNER);
		ISqlExprBuffer condition = join.onCondition();
		condition.loadField(leftAlias, FIELD_DBNAME_RECID);
		condition.loadField(rightAlias, FIELD_DBNAME_RECID);
		condition.eq();
	}

	static final String aliasOf(TableRef tableRef, DBTableDefineImpl dbTable) {
		return dbTable.isPrimary() ? tableRef.getName() : tableRef.getName()
				+ "_" + dbTable.index();
	}

	static final InvalidStatementDefineException noRecidColumnForTable(
			QueryStatementBase statement, TableDefineImpl table) {
		return new InvalidStatementDefineException("��ѯ��䶨��[" + statement.name
				+ "]û�������[" + table.name + "]��RECID��.");
	}

	static final InvalidStatementDefineException duplicateModifyColumn() {
		return new InvalidStatementDefineException("�ظ����޸���ֵ");
	}

	static final InvalidStatementDefineException duplicateModifyTable(
			QueryStatementBase statement, TableDefineImpl table) {
		return new InvalidStatementDefineException("��ѯ��䶨��[" + statement.name
				+ "]�ظ����±�[" + table.name + "]");
	}

	static final String rowModifyAlias(DBTableDefineImpl dbTable) {
		return dbTable.name;
	}

	static final InvalidStatementDefineException modifyTableNotSupport(
			QueryStatementBase statement) {
		throw new InvalidStatementDefineException("��ѯ����[" + statement.name
				+ "]����������û�ж����κοɸ��µ������.");
	}

	static final TableFieldDefineImpl detectUpdateFieldFor(QueryColumnImpl qc,
			QuTableRef tableRef, DBTableDefineImpl dbTable) {
		if (tableRef.getTarget() != dbTable.owner) {
			throw new IllegalArgumentException();
		}
		if (qc.value() instanceof TableFieldRefImpl) {
			TableFieldRefImpl fieldRef = (TableFieldRefImpl) qc.value();
			TableFieldDefineImpl field = fieldRef.field;
			if (fieldRef.tableRef == tableRef && field.dbTable == dbTable
					&& !field.isRECID()) {
				return field;
			}
		}
		return null;
	}

	static final TableFieldDefineImpl detectInsertColumnFor(QueryColumnImpl qc,
			QuTableRef tableRef, DBTableDefineImpl dbTable) {
		if (tableRef.getTarget() != dbTable.owner) {
			throw new IllegalArgumentException();
		}
		if (qc.value() instanceof TableFieldRefImpl) {
			TableFieldRefImpl fieldRef = (TableFieldRefImpl) qc.value();
			if (fieldRef.tableRef == tableRef
					&& (fieldRef.field.dbTable == dbTable || fieldRef.field
							.isRECID())) {
				return fieldRef.field;
			}
		}
		return null;
	}
}
