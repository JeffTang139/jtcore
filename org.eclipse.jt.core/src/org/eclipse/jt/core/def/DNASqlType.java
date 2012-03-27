package org.eclipse.jt.core.def;

import org.eclipse.jt.core.def.query.DeleteStatementDeclarator;
import org.eclipse.jt.core.def.query.InsertStatementDeclarator;
import org.eclipse.jt.core.def.query.ORMDeclarator;
import org.eclipse.jt.core.def.query.QueryStatementDeclarator;
import org.eclipse.jt.core.def.query.UpdateStatementDeclarator;
import org.eclipse.jt.core.def.table.TableDeclarator;
import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.impl.DeclaratorBase;
import org.eclipse.jt.core.impl.DeleteStatementImpl;
import org.eclipse.jt.core.impl.InsertStatementImpl;
import org.eclipse.jt.core.impl.MappingQueryStatementImpl;
import org.eclipse.jt.core.impl.QueryStatementImpl;
import org.eclipse.jt.core.impl.TableDeclareStub;
import org.eclipse.jt.core.impl.UpdateStatementImpl;

/**
 * 原数据类型
 */
public enum DNASqlType {
	/**
	 * 抽象表
	 */
	ABSTRACT_TABLE(null, TableDeclareStub.class, "abstable"),
	/**
	 * 表
	 */
	TABLE(TableDeclarator.class, TableDeclareStub.class, "table"),
	/**
	 * 查询
	 */
	QUERY(QueryStatementDeclarator.class, QueryStatementImpl.class, "query"),
	/**
	 * ORM
	 */
	ORM(ORMDeclarator.class, MappingQueryStatementImpl.class, "orm"),
	/**
	 * 插入语句
	 */
	INSERT(InsertStatementDeclarator.class, InsertStatementImpl.class, "insert"),
	/**
	 * 删除语句
	 */
	DELETE(DeleteStatementDeclarator.class, DeleteStatementImpl.class, "delete"),
	/**
	 * 更新语句
	 */
	UPDATE(UpdateStatementDeclarator.class, UpdateStatementImpl.class, "update");

	/**
	 * 对应的声名器类型
	 */
	public final Class<?> declaratorBaseClass;
	/**
	 * 对应的声名类型
	 */
	public final Class<?> declareBaseClass;
	/**
	 * 对应的声名脚本的文件后缀名
	 */
	public final String declareScriptPostfix;

	DNASqlType(Class<?> declaratorBaseClass, Class<?> declareBaseClass,
	        String declareScriptPostfix) {
		this.declaratorBaseClass = declaratorBaseClass;
		this.declareBaseClass = declareBaseClass;
		this.declareScriptPostfix = declareScriptPostfix;
	}

	private final static DNASqlType[] DNASQLTYPES = DNASqlType.values();

	public static final DNASqlType typeOfDeclaratorClass(
	        Class<? extends DeclaratorBase> declaratorClass) {
		if (declaratorClass == null) {
			throw new NullArgumentException("declaratorClass");
		}
		final DNASqlType[] dts = DNASQLTYPES;
		final int etsl = dts.length;
		for (Class<?> clazz = declaratorClass.getSuperclass(); clazz != null; clazz = clazz
		        .getSuperclass()) {
			for (int i = 0; i < etsl; i++) {
				DNASqlType dt = dts[i];
				if (dt.declaratorBaseClass == clazz) {
					return dt;
				}
			}
		}
		return null;
	}

	public static final DNASqlType typeOfResourcePath(String path) {
		final int pathL;
		if (path == null || (pathL = path.length()) == 0) {
			throw new NullArgumentException("path");
		}
		for (DNASqlType type : DNASQLTYPES) {
			final int postfixL = type.declareScriptPostfix.length();
			final int pointAt = pathL - postfixL - 1;
			if (pointAt > 0
			        && path.charAt(pointAt) == '.'
			        && path.regionMatches(pointAt + 1,
			                type.declareScriptPostfix, 0, postfixL)) {
				return type;
			}
		}
		return null;
	}

	/**
	 * 根据声明器类型 获得元数据类型，未找到或不支持脚本则抛出异常
	 */
	public static final DNASqlType declareScriptSupportedTypeOfDeclaratorClass(
	        Class<? extends DeclaratorBase> declaratorClass) {
		final DNASqlType et = typeOfDeclaratorClass(declaratorClass);
		if (et == null || et.declareScriptPostfix == null) {
			throw new IllegalArgumentException("不支持脚本定义的声明器类型："
			        + declaratorClass);
		}
		return et;
	}

	public static final boolean declareScirptSupported(
	        Class<?> declaratorBaseClass) {
		if (declaratorBaseClass == null) {
			throw new NullArgumentException("declaratorBaseClass");
		}
		for (DNASqlType c : DNASQLTYPES) {
			if (c.declaratorBaseClass != null
			        && declaratorBaseClass
			                .isAssignableFrom(c.declaratorBaseClass)) {
				return true;
			}
		}
		return false;
	}
}
