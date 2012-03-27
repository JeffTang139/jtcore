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
 * ԭ��������
 */
public enum DNASqlType {
	/**
	 * �����
	 */
	ABSTRACT_TABLE(null, TableDeclareStub.class, "abstable"),
	/**
	 * ��
	 */
	TABLE(TableDeclarator.class, TableDeclareStub.class, "table"),
	/**
	 * ��ѯ
	 */
	QUERY(QueryStatementDeclarator.class, QueryStatementImpl.class, "query"),
	/**
	 * ORM
	 */
	ORM(ORMDeclarator.class, MappingQueryStatementImpl.class, "orm"),
	/**
	 * �������
	 */
	INSERT(InsertStatementDeclarator.class, InsertStatementImpl.class, "insert"),
	/**
	 * ɾ�����
	 */
	DELETE(DeleteStatementDeclarator.class, DeleteStatementImpl.class, "delete"),
	/**
	 * �������
	 */
	UPDATE(UpdateStatementDeclarator.class, UpdateStatementImpl.class, "update");

	/**
	 * ��Ӧ������������
	 */
	public final Class<?> declaratorBaseClass;
	/**
	 * ��Ӧ����������
	 */
	public final Class<?> declareBaseClass;
	/**
	 * ��Ӧ�������ű����ļ���׺��
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
	 * �������������� ���Ԫ�������ͣ�δ�ҵ���֧�ֽű����׳��쳣
	 */
	public static final DNASqlType declareScriptSupportedTypeOfDeclaratorClass(
	        Class<? extends DeclaratorBase> declaratorClass) {
		final DNASqlType et = typeOfDeclaratorClass(declaratorClass);
		if (et == null || et.declareScriptPostfix == null) {
			throw new IllegalArgumentException("��֧�ֽű���������������ͣ�"
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
