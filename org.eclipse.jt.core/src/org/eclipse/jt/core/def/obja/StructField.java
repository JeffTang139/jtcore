/**
 * Copyright (C) 2007-2008 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File StateField.java
 * Date 2008-6-30
 */
package org.eclipse.jt.core.def.obja;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * @author Jeff Tang
 * @version 1.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface StructField {
	/**
	 * �ṹ�ֶα�ʶ
	 */
	String name() default "";

	/**
	 * ����
	 */
	String title() default "";

	/**
	 * ����
	 */
	String description() default "";

	/**
	 * ��Java����ΪlongʱҪ��StructField��Ϊʱ���������ͣ����룩�Դ�
	 */
	boolean asDate() default false;

	/**
	 * �Ƿ���״̬�ֶ�(����ʱ����ֵ�������ã����л�ʱ��������)
	 */
	boolean stateField() default true;
}
