package org.eclipse.jt.core.impl;

import java.util.List;

import org.eclipse.jt.core.def.table.TableJoinType;


class OracleSubQueryRefBuffer extends OracleRelationRefBuffer implements
		ISqlJoinedQueryRefBuffer {
	OracleSelectBuffer select;

	public OracleSubQueryRefBuffer(String alias) {
		super(alias);
	}

	public OracleSubQueryRefBuffer(String alias, TableJoinType type) {
		super(alias, type);
	}

	public OracleSelectBuffer select() {
		if (this.select == null) {
			this.select = new OracleSelectBuffer();
			this.select.replace(this.targetAlias, this.alternateAlias);
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
