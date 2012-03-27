package org.eclipse.jt.core.service;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.eclipse.jt.core.impl.ServiceBase;


/**
 * 发布标记<br>
 * 标记模块的某个调用、某模块（下的全部公开调用）、或者某个空间（下的全部公开调用）的可见性
 * 
 * @author Jeff Tang
 * 
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Publish {
	public Mode value() default Mode.DEFAULT;

	/**
	 * 发布模式
	 * 
	 * @author Jeff Tang
	 * 
	 */
	public static enum Mode {
		/**
		 * 默认，与父元素访问性相同
		 */
		DEFAULT,
		/**
		 * 对所有站点的所有模块可见，远程调用亦可见，但需要制定站点才能访问
		 */
		SITE_PUBLIC,
		/**
		 * 对本站点以及子站点的所有模块可见
		 */
		SITE_PROTECTED,
		/**
		 * 对本站点的所有模块可见
		 */
		PUBLIC,
		/**
		 * 对本空间下模块以及子模块可见
		 */
		PROTECTED;
		/**
		 * 从某个对象的类中获取发布模式
		 * 
		 * @param o
		 *            被获取模式的对象
		 * @param defaultMode
		 *            默认的发布模式
		 * @return 返回发布模式
		 */
		public static final Mode getMode(Class<?> clazz, Mode defaultMode) {
			if (clazz != null && clazz != ServiceBase.class) {
				Publish publish = clazz.getAnnotation(Publish.class);
				if (publish != null) {
					Publish.Mode m = publish.value();
					if (m != null) {
						return m;
					}
				}
			}
			return defaultMode;
		}
	}
}
