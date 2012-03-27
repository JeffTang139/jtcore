package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.MetaElementType;
import org.eclipse.jt.core.def.arg.ArgumentDefine;
import org.eclipse.jt.core.def.exp.ValueExpression;
import org.eclipse.jt.core.def.query.UpdateStatementDeclarator;
import org.eclipse.jt.core.def.query.UpdateStatementDeclare;
import org.eclipse.jt.core.def.table.TableFieldDefine;
import org.eclipse.jt.core.exception.InvalidStatementDefineException;
import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.exception.UnsupportedAssignmentException;
import org.eclipse.jt.core.type.AssignCapability;
import org.eclipse.jt.core.type.DataType;

/**
 * 更新语句定义实现
 * 
 * @author Jeff Tang
 * 
 */
public final class UpdateStatementImpl extends ConditionalStatementImpl
		implements UpdateStatementDeclare,
		Declarative<UpdateStatementDeclarator> {

	public final UpdateStatementDeclarator getDeclarator() {
		return this.declarator;
	}

	public final MetaElementType getMetaElementType() {
		return MetaElementType.UPDATE;
	}

	public final void assignConst(TableFieldDefine field, Object value) {
		if (field == null) {
			throw new NullArgumentException("更新列");
		}
		TableFieldDefineImpl f = (TableFieldDefineImpl) field;
		ValueExpr v = value == null ? NullExpr.NULL : f.getType().detect(
				ConstExpr.parser, value);
		this.assign(field, v);
	}

	public final void assignArgument(TableFieldDefine field,
			ArgumentDefine argument) {
		if (argument == null) {
			throw new NullArgumentException("参数定义");
		}
		this.assign(field,
				new ArgumentRefExpr((StructFieldDefineImpl) argument));
	}

	public final StructFieldDefineImpl assignArgument(TableFieldDefine field,
			String name, DataType type) {
		if (name == null) {
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
			throw new NullArgumentException("更新列");
		}
		TableFieldDefineImpl fi = (TableFieldDefineImpl) field;
		StructFieldDefineImpl arg = this.newArgument(field);
		this.assign(fi, new ArgumentRefExpr(arg));
		return arg;
	}

	public final void assignExpression(TableFieldDefine field,
			ValueExpression value) {
		this.assign(field, value == null ? NullExpr.NULL : (ValueExpr) value);
	}

	@Override
	public final String getXMLTagName() {
		return xml_name;
	}

	static final String xml_name = "update-statement";

	final UpdateStatementDeclarator declarator;

	static final class FieldAssign {

		final TableFieldDefineImpl field;

		ValueExpr value;

		final ValueExpr value() {
			return this.value;
		}

		final int hash;

		private FieldAssign next;

		private FieldAssign(TableFieldDefineImpl field, ValueExpr value) {
			this.field = field;
			this.value = value;
			this.hash = field.hashCode();
		}

		private FieldAssign(TableFieldDefineImpl field, ValueExpr value,
				FieldAssign next) {
			this.field = field;
			this.value = value;
			this.hash = field.hashCode();
			this.next = next;
		}
	}

	static final class FieldAssigns {

		private FieldAssigns() {
		}

		final int size() {
			return this.size;
		}

		private final void set(TableFieldDefineImpl field, ValueExpr value) {
			FieldAssign[] map = this.map;
			final int hash = field.hashCode();
			if (map == null) {
				map = new FieldAssign[8];
				FieldAssign fa = new FieldAssign(field, value);
				map[hash & 7] = fa;
				this.map = map;
				this.list = new FieldAssign[8];
				this.list[0] = fa;
				this.size = 1;
			} else {
				final int index = hash & (map.length - 1);
				for (FieldAssign fa = map[index]; fa != null; fa = fa.next) {
					if (hash == fa.hash && field == fa.field) {
						fa.value = value;
						return;
					}
				}
				this.ensureList(this.size + 1);
				FieldAssign fa = new FieldAssign(field, value, map[index]);
				map[index] = fa;
				this.list[this.size] = fa;
				this.size++;
				if (this.size > this.map.length * 0.75f) {
					final int nlength = this.map.length * 2;
					final int mask = nlength - 1;
					FieldAssign[] nmap = new FieldAssign[nlength];
					for (FieldAssign e : map) {
						while (e != null) {
							final FieldAssign next = e.next;
							final int nindex = e.field.hashCode() & mask;
							e.next = nmap[nindex];
							nmap[nindex] = e;
							e = next;
						}
					}
					this.map = nmap;
				}
			}
		}

		final boolean contains(TableFieldDefineImpl field) {
			final FieldAssign[] map = this.map;
			if (map == null) {
				return false;
			}
			final int hash = field.hashCode();
			final int index = hash & (map.length - 1);
			for (FieldAssign fa = map[index]; fa != null; fa = fa.next) {
				if (hash == fa.hash && field == fa.field) {
					return true;
				}
			}
			return false;
		}

		private void ensureList(int size) {
			int oldSize = this.list.length;
			if (size > oldSize) {
				int newSize = oldSize * 2;
				if (newSize < size) {
					newSize = size;
				}
				FieldAssign[] old = this.list;
				this.list = new FieldAssign[newSize];
				System.arraycopy(old, 0, this.list, 0, oldSize);
			}
		}

		final FieldAssign get(int i) {
			return this.list[i];
		}

		private FieldAssign[] map;

		// 一定要配合size使用该list
		private FieldAssign[] list;

		private int size;

	}

	final FieldAssigns assigns = new FieldAssigns();

	public UpdateStatementImpl(String name, TableDefineImpl table) {
		super(name, table);
		this.declarator = null;
	}

	public UpdateStatementImpl(String name, TableDefineImpl table,
			UpdateStatementDeclarator declarator) {
		super(name, table);
		this.declarator = null;
	}

	UpdateStatementImpl(String name, StructDefineImpl argumentsRef,
			TableDefineImpl table) {
		super(name, argumentsRef, table);
		this.declarator = null;
	}

	/**
	 * 设定指定字段的常量更新值
	 * 
	 * @param field
	 *            指定字段
	 * @param value
	 *            值表达式
	 */
	final void assign(TableFieldDefine field, ValueExpr value) {
		this.checkModifiable();
		if (field == null) {
			throw new NullArgumentException("更新列");
		}
		if (value == null) {
			throw new NullArgumentException("更新值");
		}
		TableFieldDefineImpl fi = (TableFieldDefineImpl) field;
		if (this.moTableRef.target != fi.owner) {
			throw new InvalidStatementDefineException("指定的更新列[" + fi.name
					+ "]不属于目标表[" + this.moTableRef.target.name + "].");
		}
		AssignCapability ac = field.getType().isAssignableFrom(value.getType());
		if (ac == AssignCapability.NO || ac == AssignCapability.CONVERT) {
			if (SystemVariables.VALIDATE_ASSIGN_TYPE) {
				throw new UnsupportedAssignmentException(this, field,
						value.getType());
			} else {
				System.err.println("更新语句定义[" + this.getName() + "]中,类型为["
						+ field.getType().toString() + "]的字段["
						+ field.getName() + "],不能接受类型为["
						+ value.getType().toString() + "]的值.");
			}
		}
		this.assigns.set(fi, value);
	}

	private UpdateSql sql;

	@Override
	public final UpdateSql getSql(DBAdapterImpl dbAdapter) {
		this.ensurePrepared(dbAdapter.getContext(), false);
		UpdateSql sql = this.sql;
		if (sql == null) {
			synchronized (this) {
				sql = this.sql;
				if (sql == null) {
					this.sql = sql = new UpdateSql(dbAdapter.lang, this);
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
		visitor.visitUpdateStatement(this, context);
	}
}