package org.eclipse.jt.core.def.obja;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记某类的全部子段都需要映射结构信息
 * 
 * @author Jeff Tang
 * 
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface StructClass {
	// Nothing
}
