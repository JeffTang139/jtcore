package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.obja.StructClass;
import org.eclipse.jt.core.def.obja.StructField;

/**
 * Sql������ݶ���
 */
@StructClass
public class PMV_Sql extends PMV_SimpleSql {
	@StructField(title = "����ջ")
	public String stackTrace;
	@StructField(title = "�쳣��Ϣ")
	public String exception;
}
