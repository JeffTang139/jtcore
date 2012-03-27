package org.eclipse.jt.core.def.table;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ORM表字段绑定标记<br>
 * 标记为AsTableField的实体字段,将根据其字段的类型,名称及注解定义,生成对应的数据库表字段.<br>
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
	 * 逻辑表字段名,指定逻辑表定义中字段的名称,为空则为实体字段的名称
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
	 * 数据库字段名,为空则为实体字段的名称
	 */
	String nameInDB() default "";

	/**
	 * 该字段是否是数据库表的必填字段
	 */
	boolean isRequired() default false;

	/**
	 * 主键序号,大于或等于0指示字段为逻辑主键,序号相同时由字段顺序决定主键顺序
	 */
	int pkOrdinal() default -1;

	/**
	 * 是否是recid字段,同一实体中,只能申明一个字段为recid,否则抛出异常
	 */
	boolean isRecid() default false;

	/**
	 * 是否是recver字段,同一实体中,只能申明一个字段为recver,否则抛出异常
	 */
	boolean isRecver() default false;

	/**
	 * 数据库类型<br>
	 * 为空则以实体字段类型决定数据库的字段类型<br>
	 * 影射关系如下(以DNA框架类型表示,实际的数据库字段类型由数据库决定)
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
	 * 如果需要实体字段生成非默认类型的数据库表字段,则需要明确指定字段的dbType
	 */
	public enum DBType {
		/**
		 * 由字段类型决定数据库类型
		 */
		Default,
		/**
		 * 定点型
		 */
		Numeric,
		/**
		 * 定长字符串
		 */
		Char,
		/**
		 * 变长字符串
		 */
		Varchar,
		/**
		 * 大文本
		 */
		Text,
		/**
		 * 定长Unicode字符串
		 */
		NChar,
		/**
		 * 变长Unicode字符串
		 */
		NVarchar,
		/**
		 * Unicode大文本
		 */
		NText,
		/**
		 * 定长二进制字符串
		 */
		Binary,
		/**
		 * 变长二进制字符串
		 */
		Varbinary,
		/**
		 * 大二进制文本
		 */
		Blob,
		/**
		 * 日期时间
		 */
		Date;
	}

	/**
	 * 数据库类型,默认由实体字段类型决定
	 * 
	 * @see AsTableField.DBType.Default
	 */
	DBType dbType() default DBType.Default;

	/**
	 * 长度,char,nchar,varchar,nvarchar,binary,varbinary类型有效,默认长度32
	 */
	int length() default 32;

	/**
	 * 精度,numeric类型有效,默认精度19
	 */
	int precision() default 19;

	/**
	 * 小数位,numeric类型有效,默认小数位4
	 */
	int scale() default 4;
}
