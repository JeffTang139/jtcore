package org.eclipse.jt.core.def.table;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ORM���ֶΰ󶨱��<br>
 * ���ΪAsTableField��ʵ���ֶ�,���������ֶε�����,���Ƽ�ע�ⶨ��,���ɶ�Ӧ�����ݿ���ֶ�.<br>
 * 
 * <ol>
 * <li><b>boolean</b>
 * </ol>
 * 
 * @author Jeff Tang
 * 
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface AsTableField {
	/**
	 * �߼����ֶ���,ָ���߼��������ֶε�����,Ϊ����Ϊʵ���ֶε�����
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
	 * ���ݿ��ֶ���,Ϊ����Ϊʵ���ֶε�����
	 */
	String nameInDB() default "";

	/**
	 * ���ֶ��Ƿ������ݿ��ı����ֶ�
	 */
	boolean isRequired() default false;

	/**
	 * �������,���ڻ����0ָʾ�ֶ�Ϊ�߼�����,�����ͬʱ���ֶ�˳���������˳��
	 */
	int pkOrdinal() default -1;

	/**
	 * �Ƿ���recid�ֶ�,ͬһʵ����,ֻ������һ���ֶ�Ϊrecid,�����׳��쳣
	 */
	boolean isRecid() default false;

	/**
	 * �Ƿ���recver�ֶ�,ͬһʵ����,ֻ������һ���ֶ�Ϊrecver,�����׳��쳣
	 */
	boolean isRecver() default false;

	/**
	 * ���ݿ�����<br>
	 * Ϊ������ʵ���ֶ����;������ݿ���ֶ�����<br>
	 * Ӱ���ϵ����(��DNA������ͱ�ʾ,ʵ�ʵ����ݿ��ֶ����������ݿ����)
	 * <ol>
	 * <li><b>boolean</b>=>Boolean
	 * <li><b>short</b>=>Short
	 * <li><b>int</b>=>Int
	 * <li><b>long</b>=>Long
	 * <li><b>float</b>=>Float
	 * <li><b>double</b>=>Double
	 * <li><b>String</b>=>Varchar(32)
	 * <li><b>byte[]</b>=>Varbinary(32)
	 * <li><b>GUID</b>=>GUID
	 * <li><b>Date</b>=>Date
	 * <li><b>enum</b>=>int
	 * </ol>
	 * �����Ҫʵ���ֶ����ɷ�Ĭ�����͵����ݿ���ֶ�,����Ҫ��ȷָ���ֶε�dbType
	 */
	public enum DBType {
		/**
		 * ���ֶ����;������ݿ�����
		 */
		Default,
		/**
		 * ������
		 */
		Numeric,
		/**
		 * �����ַ���
		 */
		Char,
		/**
		 * �䳤�ַ���
		 */
		Varchar,
		/**
		 * ���ı�
		 */
		Text,
		/**
		 * ����Unicode�ַ���
		 */
		NChar,
		/**
		 * �䳤Unicode�ַ���
		 */
		NVarchar,
		/**
		 * Unicode���ı�
		 */
		NText,
		/**
		 * �����������ַ���
		 */
		Binary,
		/**
		 * �䳤�������ַ���
		 */
		Varbinary,
		/**
		 * ��������ı�
		 */
		Blob,
		/**
		 * ����ʱ��
		 */
		Date;
	}

	/**
	 * ���ݿ�����,Ĭ����ʵ���ֶ����;���
	 * 
	 * @see AsTableField.DBType.Default
	 */
	DBType dbType() default DBType.Default;

	/**
	 * ����,char,nchar,varchar,nvarchar,binary,varbinary������Ч,Ĭ�ϳ���32
	 */
	int length() default 32;

	/**
	 * ����,numeric������Ч,Ĭ�Ͼ���19
	 */
	int precision() default 19;

	/**
	 * С��λ,numeric������Ч,Ĭ��С��λ4
	 */
	int scale() default 4;
}
