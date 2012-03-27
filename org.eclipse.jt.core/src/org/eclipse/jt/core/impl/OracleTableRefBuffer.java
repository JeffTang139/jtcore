package org.eclipse.jt.core.impl;

import java.util.List;

import org.eclipse.jt.core.def.table.TableJoinType;


class OracleTableRefBuffer extends OracleRelationRefBuffer implements
		ISqlJoinedTableRefBuffer, ISqlJoinedWithRefBuffer {
	final String name;

	public OracleTableRefBuffer(String table, String alias) {
		super(alias);
		this.name = OracleExprBuffer.quote(table);
	}

	public OracleTableRefBuffer(String table, String alias, TableJoinType type) {
		super(alias, type);
		this.name = OracleExprBuffer.quote(table);
	}

	@Override
	protected void writeRefTextTo(SqlStringBuffer sql,
			List<ParameterReserver> args) {
		sql.append(this.name);
	}
}
