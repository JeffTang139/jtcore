package org.eclipse.jt.core.def.model;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记某子段作为模型的属性
 * 
 * @author Jeff Tang
 * 
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface AsModelProperty {
	/**
	 * 属性名
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
}
