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
 * 插入语句定义实现
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
			throw new NullArgumentException("插入列");
		}
		TableFieldDefineImpl f = (TableFieldDefineImpl) field;
		ValueExpr expr = value == null ? NullExpr.NULL : f.getType().detect(
				ConstExpr.parser, value);
		this.assign(f, expr);

	}

	public final void assignExpression(TableFieldDefine field,
			ValueExpression value) {
		if (value == null) {
			throw new NullArgumentException("插入值");
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
			throw new NullArgumentException("参数名称");
		}
		if (type == null) {
			throw new NullArgumentException("参数类型");
		}
		StructFieldDefineImpl arg = this.newArgument(name, type);
		this.assign(field, new ArgumentRefExpr(arg));
		return arg;
	}

	public final StructFieldDefineImpl assignArgument(TableFieldDefine field) {
		if (field == null) {
			throw new NullArgumentException("插入列");
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
	 * 插入语句的目标字段及对应值
	 * 
	 * <p>
	 * insert语句实际上是插入一个关系,使用query结构.<br>
	 * 注意到values子句其实是select子句退化到单行关系时的特例.
	 * 
	 * <p>
	 * query的每一输出列的name与expr分别对应目标字段及值
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
			throw new NullArgumentException("插入列定义");
		}
		if (value == null) {
			throw new NullArgumentException("插入值");
		}
		TableFieldDefineImpl fi = (TableFieldDefineImpl) field;
		if (this.moTableRef.target != fi.owner) {
			throw new InvalidStatementDefineException("指定的更新列[" + fi.name
					+ "]不属于目标表[" + this.moTableRef.target.name + "].");
		}
		AssignCapability ac = fi.getType().isAssignableFrom(value.getType());
		if (ac == AssignCapability.NO || ac == AssignCapability.CONVERT) {
			if (SystemVariables.VALIDATE_ASSIGN_TYPE) {
				throw new UnsupportedAssignmentException(this, field,
						value.getType());
			} else {
				System.err.println("插入语句定义[" + this.getName() + "]中,类型为["
						+ field.getType().toString() + "]的字段["
						+ field.getName() + "],不能接受类型为["
						+ value.getType().toString() + "]的值.");
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
