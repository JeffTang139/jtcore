package org.eclipse.jt.core.impl;

import java.util.List;

import org.eclipse.jt.core.def.table.TableJoinType;


class SQLServerSubQueryRefBuffer extends SQLServerRelationRefBuffer implements
		ISqlJoinedQueryRefBuffer {
	SQLServerSelectBuffer select;

	public SQLServerSubQueryRefBuffer(String alias) {
		super(alias);
	}

	public SQLServerSubQueryRefBuffer(String alias, TableJoinType type) {
		super(alias, type);
	}

	public ISqlSelectBuffer select() {
		if (this.select == null) {
			this.select = new SQLServerSelectBuffer();
		}
		return this.select;
	}

	@Override
	protected void writeRefTextTo(SqlStringBuffer sql,
			List<ParameterReserver> args) {
		sql.append('(');
		this.select.writeTo(sql, args);
		sql.append(')');
	}
}
