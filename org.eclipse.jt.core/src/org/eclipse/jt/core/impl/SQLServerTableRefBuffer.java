package org.eclipse.jt.core.impl;

import java.util.List;

import org.eclipse.jt.core.def.table.TableJoinType;


class SQLServerTableRefBuffer extends SQLServerRelationRefBuffer implements
		ISqlJoinedTableRefBuffer, ISqlJoinedWithRefBuffer {
	final String table;

	public SQLServerTableRefBuffer(String table, String alias) {
		super(alias);
		this.table = table;
	}

	public SQLServerTableRefBuffer(String table, String alias,
			TableJoinType type) {
		super(alias, type);
		this.table = table;
	}

	@Override
	protected void writeRefTextTo(SqlStringBuffer sql,
			List<ParameterReserver> args) {
		sql.append(this.table);
	}
}
