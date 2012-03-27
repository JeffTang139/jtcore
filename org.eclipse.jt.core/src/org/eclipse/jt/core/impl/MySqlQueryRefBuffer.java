package org.eclipse.jt.core.impl;

import java.util.List;

import org.eclipse.jt.core.def.table.TableJoinType;


class MySqlQueryRefBuffer extends MySqlRelationRefBuffer implements
		ISqlJoinedQueryRefBuffer {

	final MySqlSelectBuffer select;

	MySqlQueryRefBuffer(MySqlCommandBuffer command, String alias) {
		super(command, alias);
		this.select = new MySqlSelectBuffer(command);
	}

	MySqlQueryRefBuffer(MySqlCommandBuffer command, String alias,
			TableJoinType type) {
		super(command, alias, type);
		this.select = new MySqlSelectBuffer(command);
	}

	public final MySqlSelectBuffer select() {
		return this.select;
	}

	@Override
	protected final void writeRefTextTo(SqlStringBuffer sql,
			List<ParameterReserver> args) {
		sql.append('(');
		this.select.writeTo(sql, args);
		sql.append(')');
	}
}
