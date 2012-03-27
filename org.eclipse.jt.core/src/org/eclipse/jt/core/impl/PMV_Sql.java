package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.obja.StructClass;
import org.eclipse.jt.core.def.obja.StructField;

/**
 * Sql监控数据对象
 */
@StructClass
public class PMV_Sql extends PMV_SimpleSql {
	@StructField(title = "调用栈")
	public String stackTrace;
	@StructField(title = "异常信息")
	public String exception;
}
