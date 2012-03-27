package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.obja.StructClass;
import org.eclipse.jt.core.def.obja.StructField;

/**
 * Sql监控数据对象
 * 
 */
@StructClass
public class PMV_SimpleSql {
	@StructField(title = "开始时间", asDate = true)
	public long start;
	@StructField(title = "结束时间", asDate = true)
	public long finish;
	@StructField(title = "SQL语句")
	public String sql;
}
