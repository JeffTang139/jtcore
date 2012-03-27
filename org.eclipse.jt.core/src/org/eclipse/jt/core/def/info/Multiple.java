package org.eclipse.jt.core.def.info;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ��ʾĳ��ؼ��ֿ��Զ���ʹ�ã��ȿ������ʹ��
 * 
 * @author Jeff Tang
 * 
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Multiple {
}
