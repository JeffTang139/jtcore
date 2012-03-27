package org.eclipse.jt.core.impl;

import java.util.List;

public abstract class SqlCommandBuffer extends SqlBuffer implements
		ISqlCommandBuffer {
	final ISqlSegmentBuffer scope;

	public SqlCommandBuffer(ISqlSegmentBuffer scope) {
		this.scope = scope;
	}

	public String build(List<ParameterReserver> reservers) {
		SqlStringBuffer sql = new SqlStringBuffer();
		this.writeTo(sql, reservers);
		int i = 0;
		for (ParameterReserver r : reservers) {
			r.reserve(i++);
		}
		return sql.toString();
	}
}
