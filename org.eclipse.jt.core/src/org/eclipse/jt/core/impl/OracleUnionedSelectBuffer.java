package org.eclipse.jt.core.impl;

import java.util.List;

class OracleUnionedSelectBuffer extends OracleSelectBuffer {
	final boolean unionAll;

	public OracleUnionedSelectBuffer(boolean unionAll) {
		this.unionAll = unionAll;
	}

	@Override
	public void writeTo(SqlStringBuffer sql, List<ParameterReserver> args) {
		if (this.unionAll) {
			sql.append(" union ");
		} else {
			sql.append(" union all ");
		}
		if (this.union != null) {
			sql.append('(');
			super.writeTo(sql, args);
			sql.append(')');
		} else {
			super.writeTo(sql, args);
		}
	}
}
