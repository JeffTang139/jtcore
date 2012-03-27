package org.eclipse.jt.core.impl;

import java.util.List;

import org.eclipse.jt.core.def.table.TableJoinType;


class DB2TableRefBuffer extends DB2RelationRefBuffer implements
		ISqlJoinedTableRefBuffer {

	final String table;

	DB2TableRefBuffer(String table, String alias) {
		super(alias);
		this.table = DB2ExprBuffer.quote(table);
	}

	DB2TableRefBuffer(String table, String alias, TableJoinType type) {
		super(alias, type);
		this.table = DB2ExprBuffer.quote(table);
	}

	@Override
	protected void writeRefTextTo(SqlStringBuffer sql,
			List<ParameterReserver> args) {
		sql.append(this.table);
	}

}
