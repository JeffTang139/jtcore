package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.obja.StructClass;
import org.eclipse.jt.core.def.obja.StructField;

/**
 * Sql������ݶ���
 * 
 */
@StructClass
public class PMV_SimpleSql {
	@StructField(title = "��ʼʱ��", asDate = true)
	public long start;
	@StructField(title = "����ʱ��", asDate = true)
	public long finish;
	@StructField(title = "SQL���")
	public String sql;
}
