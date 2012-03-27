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
	 * 结构字段标识
	 */
	String name() default "";

	/**
	 * 标题
	 */
	String title() default "";

	/**
	 * 描述
	 */
	String description() default "";

	/**
	 * 当Java类型为long时要求StructField作为时间日期类型（毫秒）对待
	 */
	boolean asDate() default false;

	/**
	 * 是否是状态字段(复制时拷贝值而非引用，序列化时关心数据)
	 */
	boolean stateField() default true;
}
