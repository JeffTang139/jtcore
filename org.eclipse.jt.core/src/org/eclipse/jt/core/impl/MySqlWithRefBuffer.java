package org.eclipse.jt.core.impl;

import java.util.List;

class MySqlWithRefBuffer extends MySqlRelationRefBuffer implements
		ISqlJoinedWithRefBuffer {

	final MySqlWithBuffer with;

	MySqlWithRefBuffer(MySqlCommandBuffer command, String target, String alias) {
		super(command, alias);
		if (!(command instanceof MySqlQueryBuffer)) {
			throw new UnsupportedOperationException();
		}
		MySqlQueryBuffer query = (MySqlQueryBuffer) command;
		this.with = query.getWith(target);
	}

	@Override
	protected void writeRefTextTo(SqlStringBuffer sql,
			List<ParameterReserver> args) {
		sql.append('(');
		this.with.writeTo(sql, args);
		sql.append(')');
	}

}
