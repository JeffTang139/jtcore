package org.eclipse.jt.core.impl;

import java.util.List;

import org.eclipse.jt.core.def.table.TableJoinType;


final class MySqlTableRefBuffer extends MySqlRelationRefBuffer implements
		ISqlJoinedTableRefBuffer {

	final String table;

	MySqlTableRefBuffer(MySqlCommandBuffer command, String table, String alias) {
		super(command, alias);
		this.table = MySqlExprBuffer.quote(table);
	}

	MySqlTableRefBuffer(MySqlCommandBuffer command, String table, String alias,
			TableJoinType type) {
		super(command, alias, type);
		this.table = MySqlExprBuffer.quote(table);
	}

	@Override
	protected void writeRefTextTo(SqlStringBuffer sql,
			List<ParameterReserver> args) {
		sql.append(this.table);
	}

}
