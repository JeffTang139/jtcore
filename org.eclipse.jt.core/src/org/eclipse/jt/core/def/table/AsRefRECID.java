package org.eclipse.jt.core.def.table;

public @interface AsRefRECID {

	String targetAlias() default "";

	String targetTable();
}
