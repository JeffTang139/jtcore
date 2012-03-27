package org.eclipse.jt.core.service;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.eclipse.jt.core.impl.ServiceBase;


/**
 * �������<br>
 * ���ģ���ĳ�����á�ĳģ�飨�µ�ȫ���������ã�������ĳ���ռ䣨�µ�ȫ���������ã��Ŀɼ���
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
	 * ����ģʽ
	 * 
	 * @author Jeff Tang
	 * 
	 */
	public static enum Mode {
		/**
		 * Ĭ�ϣ��븸Ԫ�ط�������ͬ
		 */
		DEFAULT,
		/**
		 * ������վ�������ģ��ɼ���Զ�̵�����ɼ�������Ҫ�ƶ�վ����ܷ���
		 */
		SITE_PUBLIC,
		/**
		 * �Ա�վ���Լ���վ�������ģ��ɼ�
		 */
		SITE_PROTECTED,
		/**
		 * �Ա�վ�������ģ��ɼ�
		 */
		PUBLIC,
		/**
		 * �Ա��ռ���ģ���Լ���ģ��ɼ�
		 */
		PROTECTED;
		/**
		 * ��ĳ����������л�ȡ����ģʽ
		 * 
		 * @param o
		 *            ����ȡģʽ�Ķ���
		 * @param defaultMode
		 *            Ĭ�ϵķ���ģʽ
		 * @return ���ط���ģʽ
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
