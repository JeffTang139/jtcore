package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.table.TableJoinType;

public interface ISqlRelationRefBuffer extends ISqlBuffer {
	public ISqlJoinedTableRefBuffer joinTable(String table, String alias,
			TableJoinType type);

	public ISqlJoinedQueryRefBuffer joinQuery(String alias, TableJoinType type);

	public ISqlJoinedWithRefBuffer joinWith(String target, String alias,
			TableJoinType type);
}
