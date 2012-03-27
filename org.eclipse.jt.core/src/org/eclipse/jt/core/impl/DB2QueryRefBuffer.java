package org.eclipse.jt.core.impl;

import java.util.List;

import org.eclipse.jt.core.def.table.TableJoinType;


final class DB2QueryRefBuffer extends DB2RelationRefBuffer implements
		ISqlJoinedQueryRefBuffer {

	final DB2SelectBuffer select = new DB2SelectBuffer();

	DB2QueryRefBuffer(String alias) {
		super(alias);
	}

	DB2QueryRefBuffer(String alias, TableJoinType joinType) {
		super(alias, joinType);
	}

	public final DB2SelectBuffer select() {
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
