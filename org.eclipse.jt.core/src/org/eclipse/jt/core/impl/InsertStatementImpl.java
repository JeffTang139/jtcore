package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.MetaElementType;
import org.eclipse.jt.core.def.arg.ArgumentDefine;
import org.eclipse.jt.core.def.exp.ValueExpression;
import org.eclipse.jt.core.def.query.InsertStatementDeclarator;
import org.eclipse.jt.core.def.query.InsertStatementDeclare;
import org.eclipse.jt.core.def.table.TableFieldDefine;
import org.eclipse.jt.core.exception.InvalidStatementDefineException;
import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.exception.UnsupportedAssignmentException;
import org.eclipse.jt.core.type.AssignCapability;
import org.eclipse.jt.core.type.DataType;

/**
 * ������䶨��ʵ��
 * 
 * @author Jeff Tang
 * 
 */
public final class InsertStatementImpl extends ModifyStatementImpl implements
		InsertStatementDeclare, Declarative<InsertStatementDeclarator> {

	public final InsertStatementDeclarator getDeclarator() {
		return this.declarator;
	}

	public final MetaElementType getMetaElementType() {
		return MetaElementType.INSERT;
	}

	public final void assignConst(TableFieldDefine field, Object value) {
		if (field == null) {
			throw new NullArgumentException("������");
		}
		TableFieldDefineImpl f = (TableFieldDefineImpl) field;
		ValueExpr expr = value == null ? NullExpr.NULL : f.getType().detect(
				ConstExpr.parser, value);
		this.assign(f, expr);

	}

	public final void assignExpression(TableFieldDefine field,
			ValueExpression value) {
		if (value == null) {
			throw new NullArgumentException("����ֵ");
		}
		this.assign(field, (ValueExpr) value);
	}

	public final void assignArgument(TableFieldDefine field,
			ArgumentDefine argument) {
		StructFieldDefineImpl arg = (StructFieldDefineImpl) argument;
		if (arg.owner != this.arguments) {
			throw new IllegalStateException();
		}
		this.assign(field,
				new ArgumentRefExpr((StructFieldDefineImpl) argument));
	}

	public final StructFieldDefineImpl assignArgument(TableFieldDefine field,
			String name, DataType type) {
		if (name == null || name.length() == 0) {
			throw new NullArgumentException("��������");
		}
		if (type == null) {
			throw new NullArgumentException("��������");
		}
		StructFieldDefineImpl arg = this.newArgument(name, type);
		this.assign(field, new ArgumentRefExpr(arg));
		return arg;
	}

	public final StructFieldDefineImpl assignArgument(TableFieldDefine field) {
		if (field == null) {
			throw new NullArgumentException("������");
		}
		StructFieldDefineImpl arg = this.newArgument(field);
		this.assign(field, new ArgumentRefExpr(arg));
		return arg;
	}

	public final DerivedQueryImpl getInsertValues() {
		return this.values;
	}

	@Override
	public final String getXMLTagName() {
		return xml_name_insert;
	}

	static final String xml_name_insert = "insert-statement";

	/**
	 * ��������Ŀ���ֶμ���Ӧֵ
	 * 
	 * <p>
	 * insert���ʵ�����ǲ���һ����ϵ,ʹ��query�ṹ.<br>
	 * ע�⵽values�Ӿ���ʵ��select�Ӿ��˻������й�ϵʱ������.
	 * 
	 * <p>
	 * query��ÿһ����е�name��expr�ֱ��ӦĿ���ֶμ�ֵ
	 */
	final DerivedQueryImpl values = new DerivedQueryImpl(null);

	final InsertStatementDeclarator declarator;

	public InsertStatementImpl(String name, TableDefineImpl table) {
		super(name, table);
		this.declarator = null;
	}

	public InsertStatementImpl(String name, TableDefineImpl table,
			InsertStatementDeclarator declarator) {
		super(name, table);
		this.declarator = declarator;
	}

	InsertStatementImpl(String name, StructDefineImpl argumentsRef,
			TableDefineImpl table) {
		super(name, argumentsRef, table);
		this.declarator = null;
	}

	final void assign(TableFieldDefine field, ValueExpr value) {
		this.checkModifiable();
		if (field == null) {
			throw new NullArgumentException("�����ж���");
		}
		if (value == null) {
			throw new NullArgumentException("����ֵ");
		}
		TableFieldDefineImpl fi = (TableFieldDefineImpl) field;
		if (this.moTableRef.target != fi.owner) {
			throw new InvalidStatementDefineException("ָ���ĸ�����[" + fi.name
					+ "]������Ŀ���[" + this.moTableRef.target.name + "].");
		}
		AssignCapability ac = fi.getType().isAssignableFrom(value.getType());
		if (ac == AssignCapability.NO || ac == AssignCapability.CONVERT) {
			if (SystemVariables.VALIDATE_ASSIGN_TYPE) {
				throw new UnsupportedAssignmentException(this, field,
						value.getType());
			} else {
				System.err.println("������䶨��[" + this.getName() + "]��,����Ϊ["
						+ field.getType().toString() + "]���ֶ�["
						+ field.getName() + "],���ܽ�������Ϊ["
						+ value.getType().toString() + "]��ֵ.");
			}
		}
		DerivedQueryColumnImpl column = this.values.columns.find(fi.name);
		if (column == null) {
			this.values.newColumn(value, fi.name);
		} else {
			column.setExpression(value);
		}
	}

	final boolean isSubqueried() {
		return this.values.rootRelationRef() != null;
	}

	private Sql sql;

	@Override
	public final Sql getSql(DBAdapterImpl dbAdapter) {
		this.ensurePrepared(dbAdapter.getContext(), false);
		Sql sql = this.sql;
		if (sql == null) {
			synchronized (this) {
				sql = this.sql;
				if (sql == null) {
					this.sql = sql = new InsertSql(dbAdapter.lang, this);
				}
			}
		}
		return sql;
	}

	@Override
	protected final void doPrepare(DBLang lang) throws Throwable {
		super.doPrepare(lang);
		this.sql = null;
	}

	public final <TContext> void visit(OMVisitor<TContext> visitor,
			TContext context) {
		visitor.visitInsertStatement(this, context);
	}

}
