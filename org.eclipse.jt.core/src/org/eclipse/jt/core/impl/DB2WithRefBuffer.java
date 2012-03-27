package org.eclipse.jt.core.impl;

import java.util.List;

import org.eclipse.jt.core.def.table.TableJoinType;


final class DB2WithRefBuffer extends DB2RelationRefBuffer implements
		ISqlJoinedWithRefBuffer {

	final String with;

	DB2WithRefBuffer(String with, String alias) {
		super(alias);
		this.with = DB2ExprBuffer.quote(with);
	}

	DB2WithRefBuffer(String with, String alias, TableJoinType type) {
		super(alias, type);
		this.with = DB2ExprBuffer.quote(with);
	}

	@Override
	protected void writeRefTextTo(SqlStringBuffer sql,
			List<ParameterReserver> args) {
		sql.append(this.with);
	}
}
