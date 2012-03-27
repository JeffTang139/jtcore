package org.eclipse.jt.core.def.table;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ���ĳ��ӳ�䵽���ݱ�
 * 
 * @author Jeff Tang
 * 
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AsTable {

	/**
	 * ����
	 */
	String title() default "";

	/**
	 * ����
	 */
	String description() default "";

	String dbName() default "";

}
