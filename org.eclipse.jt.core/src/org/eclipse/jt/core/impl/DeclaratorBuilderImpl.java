package org.eclipse.jt.core.impl;

import static java.lang.reflect.Modifier.ABSTRACT;
import static java.lang.reflect.Modifier.FINAL;
import static java.lang.reflect.Modifier.PRIVATE;
import static java.lang.reflect.Modifier.PROTECTED;
import static java.lang.reflect.Modifier.PUBLIC;
import static java.lang.reflect.Modifier.STATIC;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jt.core.ObjectQuerier;
import org.eclipse.jt.core.cb.DeclaratorBuilder;
import org.eclipse.jt.core.cb.DefineProvider;
import org.eclipse.jt.core.def.MetaElementType;
import org.eclipse.jt.core.def.MissingDefineException;
import org.eclipse.jt.core.def.NamedDefine;
import org.eclipse.jt.core.def.arg.ArgumentDefine;
import org.eclipse.jt.core.def.exp.ConstExpression;
import org.eclipse.jt.core.def.query.DeleteStatementDeclarator;
import org.eclipse.jt.core.def.query.InsertStatementDeclarator;
import org.eclipse.jt.core.def.query.ORMDeclarator;
import org.eclipse.jt.core.def.query.QueryColumnDefine;
import org.eclipse.jt.core.def.query.QueryStatementDeclarator;
import org.eclipse.jt.core.def.query.UpdateStatementDeclarator;
import org.eclipse.jt.core.def.table.DBTableDeclare;
import org.eclipse.jt.core.def.table.HierarchyDeclare;
import org.eclipse.jt.core.def.table.HierarchyDefine;
import org.eclipse.jt.core.def.table.IndexDeclare;
import org.eclipse.jt.core.def.table.TableDeclarator;
import org.eclipse.jt.core.def.table.TableFieldDeclare;
import org.eclipse.jt.core.def.table.TableFieldDefine;
import org.eclipse.jt.core.def.table.TableRelationDeclare;
import org.eclipse.jt.core.def.table.TableRelationDefine;
import org.eclipse.jt.core.def.table.TableRelationType;
import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.type.DataType;
import org.eclipse.jt.core.type.TypeFactory;


/**
 * 声明器的代码生成器实现
 * 
 * @author Jeff Tang
 * 
 */
public final class DeclaratorBuilderImpl extends DefineHolderImpl implements
		DeclaratorBuilder {

	public static final DeclaratorBuilderImpl newInstance() {
		return new DeclaratorBuilderImpl();
	}

	public final void build(Appendable out, MetaElementType type, String name,
			DefineProvider provider) {
		CodeBuilder builder = new CodeBuilder(out);
		try {
			this.provider = Utils.coalesce(provider, this.provider);
			TwoKeyEntry entry = this.get(type, name);
			if (entry == null && provider != null) {
				provider.demand(this, type, name);
				entry = this.get(type, name);
			}
			if (entry == null) {
				throw new MissingDefineException("不存在类型为[" + type + "],名称为["
						+ name + "]的元数据定义.");
			}
			switch (type) {
				case TABLE:
					Object define = entry.define;
					if (define instanceof TableDeclareStub) {
						TableDeclareStub nTable = (TableDeclareStub) define;
						// 会抛出异常
						nTable.fillRelations(this);
						entry.define = nTable.getTable();
						this.buildTableDeclarator(builder, nTable.getTable(),
								entry.pkgname, entry.clzname);
					} else if (define instanceof NAbstractTableDeclare) {
						NAbstractTableDeclare table = (NAbstractTableDeclare) define;
						this.buildAbstractTableDeclarator(builder, table,
								entry.pkgname, entry.clzname);
					} else if (define instanceof TableDefineImpl) {
						TableDefineImpl table = (TableDefineImpl) define;
						// 会抛出异常
						this.helper.resolveDelayAction(table, null);
						this.buildTableDeclarator(builder, table,
								entry.pkgname, entry.clzname);
					} else {
						throw new UnsupportedOperationException();
					}
					break;
				case INSERT:
				case DELETE:
				case UPDATE:
					NDmlDeclare statement = (NDmlDeclare) entry.define;
					buildModifyStatementDeclarator(builder, statement,
							entry.pkgname, entry.clzname);
					break;
				case QUERY:
					NQueryDeclare query = (NQueryDeclare) entry.define;
					buildQueryStatementDeclarator(builder, query,
							entry.pkgname, entry.clzname);
					break;
				case ORM:
					NOrmDeclare orm = (NOrmDeclare) entry.define;
					buildORMDeclarator(builder, orm, entry.pkgname,
							entry.clzname);
					break;
				default:
					throw new UnsupportedOperationException();
			}
		} catch (IOException e) {
			throw Utils.tryThrowException(e);
		}
	}

	private final void buildTableDeclarator(CodeBuilder builder,
			TableDefineImpl table, String pkgname, String clzname)
			throws IOException {
		clzname = replace(clzname, table.name);
		final boolean fielded = exceed(table.fields, 2);
		final boolean dbTabled = exceed(table.dbTables, 1);
		final boolean indexed = exceed(table.indexes, 0);
		final boolean relationed = exceed(table.relations, 0);
		final boolean hierarchied = exceed(table.hierarchies, 0);
		final boolean fieldSetAttr = toSetAttrs(table.fields, 2);
		final boolean indexSetAttr = toSetAttrs(table.indexes, 0);
		final boolean hierarchySetAttr = toSetAttrs(table.hierarchies, 0);
		declarePackage(builder, pkgname);
		// *********** import ***********
		builder.importClass(TableDeclarator.class);
		if (fielded) {
			builder.importClass(TypeFactory.class);
			builder.importClass(TableFieldDefine.class);
		}
		if (fieldSetAttr) {
			builder.importClass(TableFieldDeclare.class);
		}
		if (dbTabled) {
			builder.importClass(DBTableDeclare.class);
		}
		if (indexSetAttr) {
			builder.importClass(IndexDeclare.class);
		}
		if (relationed) {
			builder.importClass(TableRelationDeclare.class);
			builder.importClass(TableRelationDefine.class);
			builder.importClass(TableRelationType.class);
			builder.importClass(ObjectQuerier.class);
			for (TableRelationDefineImpl relation : table.relations) {
				TwoKeyEntry e = this.get(MetaElementType.TABLE,
						relation.target.name);
				if (e == null) {
					throw new MissingDefineException("表关系[" + relation.name
							+ "]的目标表[" + relation.target.name + "]不能存在.");
				}
				if (!replace(e.pkgname, "").equals(replace(pkgname, ""))) {
					builder.importClass(e.pkgname, e.clzname);
				}
			}
		}
		if (hierarchied) {
			builder.importClass(HierarchyDefine.class);
		}
		if (hierarchySetAttr) {
			builder.importClass(HierarchyDeclare.class);
		}
		if (toSetDef(table.fields, 2)) {
			builder.importClass(ConstExpression.class);
		}
		builder.appendLine();
		// *********** class ***********
		beginClass(builder, PUBLIC | FINAL, clzname, TableDeclarator.class);
		// *********** members ***********
		declareStringWithAssign(builder, PUBLIC | STATIC | FINAL, TABLE_NAME,
				table.name);
		builder.appendLine();
		if (fielded) {
			declareDefines(builder, PUBLIC | FINAL, TableFieldDefine.class,
					table.fields, 2);
			builder.appendLine();
			for (TableFieldDefineImpl field : table.fields) {
				if (field == table.f_recid || field == table.f_recver) {
					continue;
				}
				declareStringWithAssign(builder, PUBLIC | STATIC | FINAL,
						declareNameOfFN(field), field.name);
			}
			builder.appendLine();
		}
		if (relationed) {
			declareDefines(builder, PRIVATE, TableRelationDeclare.class,
					table.relations, 0);
			builder.appendLine();
		}
		if (hierarchied) {
			declareDefines(builder, PUBLIC | FINAL, HierarchyDefine.class,
					table.hierarchies, 0);
			builder.appendLine();
		}
		if (relationed) {
			for (TableRelationDefineImpl relation : table.relations) {
				builder.append("public final TableRelationDefine ");
				builder.append(declareNameOf(relation));
				builder.appendLine("() {").pi();
				builder.append("return this.");
				builder.append(declareNameOf(relation));
				builder.appendLine(';').ri();
				builder.appendLine('}');
				builder.appendLine();
			}
		}
		// *********** constructor ***********
		builder.appendLine("//不可调用该构造方法.当前类只能由框架实例化.");
		beginConstructor(builder, PRIVATE, clzname);
		builder.appendLine("super(%s);", TABLE_NAME);
		setAttrs(builder, table, THIS_TABLE);
		if (fielded) {
			if (fieldSetAttr) {
				declareVariable(builder, TableFieldDeclare.class, FIELD);
			}
			for (TableFieldDefineImpl field : table.fields) {
				if (field.dbTable != table.primary || field.isRECID()
						|| field.isRECVER()) {
					continue;
				}
				final boolean toSet = toSetAttrs(field);
				construct(builder, field, THIS_TABLE, toSet);
				if (toSet) {
					setAttrs(builder, field, FIELD);
				}
			}
		}
		if (dbTabled) {
			declareVariable(builder, DBTableDeclare.class, DBTABLE);
			for (DBTableDefineImpl dbTable : table.dbTables) {
				if (dbTable == table.primary) {
					continue;
				}
				builder.appendLine("%s = %s.newDBTable(\"%s\");", DBTABLE,
						THIS_TABLE, dbTable.getName());
				setAttrs(builder, dbTable, DBTABLE);
				for (TableFieldDefineImpl field : table.fields) {
					if (field.dbTable == dbTable) {
						final boolean toSet = toSetAttrs(field);
						construct(builder, field, DBTABLE, toSet);
						if (toSet) {
							setAttrs(builder, field, FIELD);
						}
					}
				}
			}
		}
		if (indexed) {
			if (indexSetAttr) {
				declareVariable(builder, IndexDeclare.class, INDEX);
			}
			for (IndexDefineImpl index : table.indexes) {
				final boolean toSet = toSetAttrs(index);
				builder.append("%s%s.newIndex(\"%s\"", toSet ? INDEX + " = "
						: "", THIS_TABLE, index.getName());
				for (IndexItemImpl item : index.items) {
					builder.append(",%s", declareNameOf(item.getField()));
				}
				builder.appendLine(");");
				if (toSet) {
					setAttrs(builder, index, INDEX);
				}
			}
		}
		if (hierarchied) {
			if (hierarchySetAttr) {
				declareVariable(builder, HierarchyDeclare.class, HIERARCHY);
			}
			for (HierarchyDefineImpl hierarchy : table.hierarchies) {
				final boolean toSet = toSetAttrs(hierarchy);
				builder.appendLine("this.%s %s= %s.newHierarchy(\"%s\", %s);",
						declareNameOf(hierarchy), toSet ? "= " + HIERARCHY
								+ " " : "", THIS_TABLE, hierarchy.name,
						hierarchy.getMaxLevel());
				if (toSet) {
					setAttrs(builder, hierarchy, HIERARCHY);
				}
			}
		}
		endConstructor(builder);
		// *********** declareUseRef ***********
		if (relationed) {
			beginDeclareUseRef(builder);
			ArrayList<TableDefineImpl> targets = new ArrayList<TableDefineImpl>();
			for (TableRelationDefineImpl relation : table.relations) {
				if (!targets.contains(relation.target)) {
					TwoKeyEntry entry = this.get(MetaElementType.TABLE,
							relation.target.name);
					builder.appendLine("%1$s %2$s = querier.get(%1$s.class);",
							entry.clzname, declareNameOf(relation.target));
					targets.add(relation.target);
				}
				String rn = declareNameOf(relation);
				builder.appendLine("this.%s = %s.newRelation("
						+ "\"%s\", %s, %s.%s);", rn, THIS_TABLE, relation.name,
						declareNameOf(relation.target),
						TableRelationType.class.getSimpleName(),
						relation.type.toString());
				builder.appendLine("this.%s.setJoinCondition(", rn);
				builder.pi();
				if (relation.condition == null) {
					throw new NullArgumentException("关系条件");
				}
				relation.condition.visit(builder, null);
				builder.appendLine().ri();
				builder.appendLine(");");
				if (toSetAttrs(relation)) {
					setAttrs(builder, relation, "this." + rn);
				}
			}
			endDeclareUseRef(builder);
		}
		builder.ri();
		builder.appendLine('}');
	}

	private final void buildAbstractTableDeclarator(CodeBuilder builder,
			final NAbstractTableDeclare table, String pkgname, String clzname)
			throws IOException {
		clzname = replace(clzname, table.name.value);
		if (table.base != null) {
			throw new IllegalArgumentException("抽象表继承关系错误.");
		}
		final boolean dbTabled = notEmpty(table.extend);
		final boolean indexed = notEmpty(table.index);
		final boolean toSetFieldAttrs = toSetFieldAttrs(table);
		final boolean toSetFieldDef = toSetFieldDef(table);
		declarePackage(builder, pkgname);
		builder.importClass(TableDeclarator.class);
		builder.importClass(TypeFactory.class);
		builder.importClass(TableFieldDefine.class);
		if (toSetFieldAttrs) {
			builder.importClass(TableFieldDeclare.class);
		}
		if (dbTabled) {
			builder.importClass(DBTableDeclare.class);
		}
		if (indexed && toSetAttr(table.index)) {
			builder.importClass(IndexDeclare.class);
		}
		if (toSetFieldDef) {
			builder.importClass(ConstExpression.class);
		}
		builder.appendLine();
		beginClass(builder, PUBLIC | ABSTRACT, clzname,
				TableDeclarator.class.getSimpleName());
		builder.appendLine();
		for (NTableField field : table.primary.fields) {
			declareDefine(builder, PUBLIC | FINAL, TableFieldDefine.class,
					declareNameOf(field));
		}
		if (dbTabled) {
			for (NTableExtend extend : table.extend) {
				for (NTableField field : extend.fields) {
					declareDefine(builder, PUBLIC | FINAL,
							TableFieldDefine.class, declareNameOf(field));
				}
			}
		}
		builder.appendLine();
		for (NTableField field : table.primary.fields) {
			declareStringWithAssign(builder, PUBLIC | FINAL | STATIC,
					declareNameOfFN(field), field.name.value);
		}
		if (dbTabled) {
			for (NTableExtend extend : table.extend) {
				for (NTableField field : extend.fields) {
					declareStringWithAssign(builder, PUBLIC | FINAL | STATIC,
							declareNameOfFN(field), field.name.value);
				}
			}
		}
		builder.appendLine();
		builder.appendLine("%s %s(%s %s) {", Modifier.toString(PROTECTED),
				clzname, String.class.getSimpleName(), TABLE_NAME).pi();
		builder.appendLine("super(%s);", TABLE_NAME);
		if (toSetFieldAttrs) {
			declareVariable(builder, TableFieldDeclare.class, FIELD);
		}
		for (NTableField field : table.primary.fields) {
			construct(builder, field, true);
		}
		if (dbTabled) {
			declareVariable(builder, DBTableDeclare.class, DBTABLE);
			for (NTableExtend extend : table.extend) {
				builder.appendLine("%s = %s.newDBTable(\"%s\");", DBTABLE,
						THIS_TABLE, extend.name.value);
				for (NTableField field : extend.fields) {
					construct(builder, field, false);
				}
			}
		}
		if (indexed) {
			if (toSetAttr(table.index)) {
				declareVariable(builder, IndexDeclare.class, INDEX);
			}
			for (NTableIndex index : table.index) {
				final boolean toSet = toSetAttr(index);
				builder.append("%s%s.newIndex(\"%s\"", toSet ? INDEX + " = "
						: "", THIS_TABLE, index.name.value);
				for (NTableIndexField field : index.fields) {
					builder.append(",%s", declareNameOf(field));
				}
				builder.appendLine(");");
				if (toSet) {
					if (index.unique) {
						builder.append("%s.setUnique(true);", INDEX);
					}
					for (int i = 0, c = index.fields.length; i < c; i++) {
						if (index.fields[i].desc) {
							builder.appendLine("%s.getItems().get(%s)"
									+ ".setDesc(true);", INDEX, i);
						}
					}
				}

			}
		}
		endConstructor(builder);
		endClass(builder);
	}

	private static final void buildQueryStatementDeclarator(
			CodeBuilder builder, NQueryDeclare query, String pkgname,
			String clzname) throws IOException {
		clzname = replace(clzname, query.name.value);
		declarePackage(builder, pkgname);
		final boolean arged = notEmpty(query.params);
		builder.importClass(ObjectQuerier.class);
		if (arged) {
			builder.importClass(ArgumentDefine.class);
		}
		builder.importClass(QueryColumnDefine.class);
		builder.importClass(QueryStatementDeclarator.class);
		builder.appendLine();
		beginClass(builder, PUBLIC | FINAL, clzname,
				QueryStatementDeclarator.class);
		builder.appendLine().pi();
		if (arged) {
			for (NParamDeclare arg : query.params) {
				declareDefine(builder, PUBLIC | FINAL, ArgumentDefine.class,
						declareNameOf(arg));
			}
			builder.appendLine();
		}
		NQuerySpecific s = query.body.getMasterSelect();
		for (NQueryColumn column : s.select.columns) {
			declareDefine(builder, PUBLIC | FINAL, QueryColumnDefine.class,
					declareNameOf(column));
		}
		builder.appendLine();
		builder.appendLine("public %s() {", clzname);
		builder.pi();
		int i = 0;
		if (arged) {
			for (NParamDeclare arg : query.params) {
				builder.appendLine(
						"this.%s = this.query.getArguments().get(%s);",
						declareNameOf(arg), i++);
			}
		}
		i = 0;
		for (NQueryColumn column : s.select.columns) {
			builder.appendLine("this.%s = this.query.getColumns().get(%s);",
					declareNameOf(column), i++);
		}
		builder.ri().appendLine('}');
		builder.ri().appendLine('}');
	}

	private static final void buildORMDeclarator(CodeBuilder builder,
			NOrmDeclare orm, String pkgname, String clzname) throws IOException {
		clzname = replace(clzname, orm.name.value);
		declarePackage(builder, pkgname);
		final boolean arged = notEmpty(orm.params);
		builder.importClass(ObjectQuerier.class);
		if (arged) {
			builder.importClass(ArgumentDefine.class);
		}
		builder.importClass(QueryColumnDefine.class);
		builder.importClass(ORMDeclarator.class);
		builder.appendLine();
		builder.appendLine("public class %s extends ORMDeclarator<%s> {",
				clzname, orm.className);
		builder.appendLine().pi();
		if (arged) {
			for (NParamDeclare arg : orm.params) {
				declareDefine(builder, PUBLIC | FINAL, ArgumentDefine.class,
						declareNameOf(arg));
			}
			builder.appendLine();
		}
		NQuerySpecific s = orm.body.getMasterSelect();
		for (NQueryColumn column : s.select.columns) {
			declareDefine(builder, PUBLIC | FINAL, QueryColumnDefine.class,
					declareNameOf(column));
		}
		builder.appendLine();
		builder.appendLine("public %s() {", clzname);
		builder.pi();
		int i = 0;
		if (arged) {
			for (NParamDeclare arg : orm.params) {
				builder.appendLine(
						"this.%s = this.orm.getArguments().get(%s);",
						declareNameOf(arg), i++);
			}
		}
		i = 0;
		for (NQueryColumn column : s.select.columns) {
			builder.appendLine("this.%s = this.orm.getColumns().get(%s);",
					declareNameOf(column), i++);
		}
		builder.ri().appendLine('}');
		builder.ri().appendLine('}');
	}

	private static final void buildModifyStatementDeclarator(
			CodeBuilder builder, NDmlDeclare statement, String pkgname,
			String clzname) throws IOException {
		clzname = replace(clzname, statement.name.value);
		declarePackage(builder, pkgname);
		final boolean arged = notEmpty(statement.params);
		builder.importClass(ObjectQuerier.class);
		if (arged) {
			builder.importClass(ArgumentDefine.class);
		}
		Class<?> extendsClz = null;
		if (statement instanceof NInsertDeclare) {
			extendsClz = InsertStatementDeclarator.class;
		} else if (statement instanceof NDeleteDeclare) {
			extendsClz = DeleteStatementDeclarator.class;
		} else if (statement instanceof NUpdateDeclare) {
			extendsClz = UpdateStatementDeclarator.class;
		} else {
			throw new IllegalArgumentException("不支持的类型.");
		}
		builder.importClass(extendsClz);
		builder.appendLine();
		beginClass(builder, PUBLIC | FINAL, clzname,
				InsertStatementDeclarator.class);
		builder.appendLine().pi();
		if (arged) {
			for (NParamDeclare arg : statement.params) {
				declareDefine(builder, PUBLIC | FINAL, ArgumentDefine.class,
						declareNameOf(arg));
			}
			builder.appendLine();
		}
		builder.appendLine();
		builder.appendLine("public %s() {", clzname);
		builder.pi();
		int i = 0;
		if (arged) {
			for (NParamDeclare arg : statement.params) {
				builder.appendLine(
						"this.%s = this.statement.getArguments().get(%s);",
						declareNameOf(arg), i++);
			}
		}
		builder.ri().appendLine('}');
		builder.ri().appendLine('}');
	}

	// XXX not good
	static final String THIS_TABLE = "this.table";
	private static final String TABLE_NAME = "TABLE_NAME";
	private static final String FIELD = "field";
	private static final String DBTABLE = "dbTable";
	private static final String INDEX = "index";
	private static final String HIERARCHY = "hierarchy";

	private static final String replace(String str, String replace) {
		return notNull(str) ? str : replace;
	}

	private static final <E> boolean exceed(List<E> list, int valve) {
		return list != null && list.size() > valve;
	}

	private static final <E> boolean notEmpty(E[] arr) {
		return arr != null && arr.length > 0;
	}

	private static final String defineType(DataType type) {
		return "TypeFactory.".concat(type.toString().toUpperCase());
	}

	static final String declareNameOf(NamedDefineImpl define) {
		if (define instanceof TableFieldDefineImpl) {
			return "f_".concat(define.name);
		} else if (define instanceof TableRelationDefineImpl) {
			return "r_".concat(define.name);
		} else if (define instanceof HierarchyDefineImpl) {
			return "h_".concat(define.name);
		} else if (define instanceof TableDefineImpl) {
			return "t_".concat(define.name);
		} else if (define instanceof QueryColumnImpl) {
			return "c_".concat(define.name);
		} else if (define instanceof ArgumentDefine) {
			return "arg_".concat(define.name);
		}
		throw new UnsupportedOperationException("不支持的定义类型");
	}

	private static final String declareNameOf(NTableField field) {
		return "f_".concat(field.name.value);
	}

	private static final String declareNameOf(NTableIndexField field) {
		return "f_".concat(field.name.value);
	}

	private static final String declareNameOf(NParamDeclare arg) {
		return "arg_".concat(arg.argumentName);
	}

	private static final String declareNameOf(NQueryColumn column) {
		return "c_".concat(column.alias.value);
	}

	private static final String declareNameOfFN(TableFieldDefineImpl field) {
		return "FN_".concat(field.name);
	}

	private static final String declareNameOfFN(NTableField field) {
		return "FN_".concat(field.name.value);
	}

	private static final boolean toSetAttrs(NamedDefine define) {
		if (notNull(define.getDescription()) || notNull(define.getTitle())) {
			return true;
		} else if (define instanceof TableFieldDefineImpl) {
			TableFieldDefineImpl f = (TableFieldDefineImpl) define;
			return f.isKeepValid() || f.getDefault() != null
					|| notNull(f.getNameInDB())
					&& !f.getNameInDB().equals(f.name.toUpperCase());
		} else if (define instanceof IndexDefineImpl) {
			IndexDefineImpl i = (IndexDefineImpl) define;
			if (i.isUnique()) {
				return true;
			}
			for (IndexItemImpl item : i.items) {
				if (item.isDesc()) {
					return true;
				}
			}
			return false;
		}
		return false;
	}

	private static final boolean toSetAttrs(
			List<? extends NamedDefine> defines, int offset) {
		for (int i = offset; i < defines.size(); i++) {
			if (toSetAttrs(defines.get(i))) {
				return true;
			}
		}
		return false;
	}

	private static final boolean toSetDef(TableFieldDefineImpl field) {
		return field.getDefault() != null;
	}

	private static final boolean toSetDef(List<TableFieldDefineImpl> fields,
			int offset) {
		for (int i = offset; i < fields.size(); i++) {
			if (toSetDef(fields.get(i))) {
				return true;
			}
		}
		return false;
	}

	private static final void declarePackage(CodeBuilder builder, String pkgname)
			throws IOException {
		if (notNull(pkgname)) {
			builder.appendLine("package %s;", pkgname);
			builder.appendLine();
		}
	}

	private static final void declareVariable(CodeBuilder builder,
			Class<?> clz, String identifier) throws IOException {
		builder.appendLine("%s %s;", clz.getSimpleName(), identifier);
	}

	private static final void beginClass(CodeBuilder builder, int modifier,
			String clzname, String extendsClass) throws IOException {
		builder.append("%s class %s %s", Modifier.toString(modifier), clzname,
				notNull(extendsClass) ? "extends " + extendsClass : "");
		builder.appendLine(" {").appendLine().pi();
	}

	private static final void beginClass(CodeBuilder builder, int modifier,
			String clzname, Class<?> extClz) throws IOException {
		builder.append("%s class %s %s", Modifier.toString(modifier), clzname,
				extClz != null ? "extends " + extClz.getSimpleName() : "");
		builder.appendLine(" {").appendLine().pi();
	}

	private static final void endClass(CodeBuilder builder) throws IOException {
		builder.ri();
		builder.appendLine('}');
	}

	private static final void declareDefine(CodeBuilder builder, int modifier,
			Class<?> clz, String name) throws IOException {
		builder.appendLine("%s %s %s;", Modifier.toString(modifier),
				clz.getSimpleName(), name);
	}

	private static final void declareDefines(CodeBuilder builder, int modifier,
			Class<?> clz, List<? extends NamedDefineImpl> defines, int offset)
			throws IOException {
		for (int i = offset; i < defines.size(); i++) {
			declareDefine(builder, modifier, clz, declareNameOf(defines.get(i)));
		}
	}

	private static final void declareStringWithAssign(CodeBuilder builder,
			int modifier, String identifier, String assignValue)
			throws IOException {
		// 没有转义
		builder.appendLine("%s String %s%s;", Modifier.toString(modifier),
				identifier, assignValue != null ? " =\"" + assignValue + "\""
						: "");
	}

	private static final void setAttrs(CodeBuilder builder, NamedDefine define,
			String from) throws IOException {
		if (notNull(define.getDescription())) {
			builder.appendLine("%s.setDescription(\"%s\");", from,
					define.getDescription());
		}
		if (notNull(define.getTitle())) {
			builder.appendLine("%s.setTitle(\"%s\");", from, define.getTitle());
		}
		if (define instanceof TableDefineImpl) {
			TableDefineImpl table = (TableDefineImpl) define;
			if (notNull(table.getCategory())) {
				builder.appendLine("%s.setCategory(\"%s\");", THIS_TABLE,
						table.getCategory());
			}
		} else if (define instanceof TableFieldDefineImpl) {
			TableFieldDefineImpl field = (TableFieldDefineImpl) define;
			if (field.isKeepValid()) {
				builder.appendLine("field.setKeepValid(true);");
			}
			if (field.getDefault() != null) {
				builder.append("field.setDefault(");
				field.getDefault().visit(builder, null);
				builder.appendLine(");");
			}
			String nameInDB = field.getNameInDB();
			if (notNull(nameInDB)
					&& !nameInDB.equals(field.getName().toUpperCase())) {
				builder.appendLine("field.setNameInDB(\"%s\");", nameInDB);
			}
		} else if (define instanceof IndexDefineImpl) {
			IndexDefineImpl index = (IndexDefineImpl) define;
			if (index.isUnique()) {
				builder.appendLine("%s.setUnique(true);", INDEX);
			}
			for (int i = 0, c = index.items.size(); i < c; i++) {
				if (index.items.get(i).isDesc()) {
					builder.appendLine("%s.getItems().get(%s)"
							+ ".setDesc(true);", INDEX, i);
				}
			}
		}
	}

	private static final void construct(CodeBuilder builder,
			TableFieldDefineImpl field, String from, boolean toSet)
			throws IOException {
		builder.appendLine("this.%s %s= " + "%s.%sField(%s, %s);",
				declareNameOf(field), toSet ? "= " + FIELD + " " : "", from,
				field.isPrimaryKey() ? "newPrimary" : "new",
				declareNameOfFN(field), defineType(field.getType()));
	}

	private static final boolean toSetAttrs(NTableField f) {
		return f.defaultValue != null || f.notNull;
	}

	private static final boolean toSetAttrs(NTableField[] arr) {
		for (NTableField field : arr) {
			if (toSetAttrs(field)) {
				return true;
			}
		}
		return false;
	}

	private static final boolean toSetFieldAttrs(NAbstractTableDeclare table) {
		if (toSetAttrs(table.primary.fields)) {
			return true;
		}
		if (notEmpty(table.extend)) {
			for (NTableExtend extend : table.extend) {
				if (notEmpty(extend.fields)) {
					if (toSetAttrs(extend.fields)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private static final boolean toSetFieldDef(NAbstractTableDeclare table) {
		for (NTableField field : table.primary.fields) {
			if (field.defaultValue != null) {
				return true;
			}
		}
		if (notEmpty(table.extend)) {
			for (NTableExtend extend : table.extend) {
				if (notEmpty(extend.fields)) {
					for (NTableField field : extend.fields) {
						if (field.defaultValue != null) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	private static final boolean toSetAttr(NTableIndex index) {
		if (index.unique) {
			return true;
		}
		for (NTableIndexField field : index.fields) {
			if (field.desc) {
				return true;
			}
		}
		return false;
	}

	private static final boolean toSetAttr(NTableIndex[] indexes) {
		for (NTableIndex index : indexes) {
			if (toSetAttr(index)) {
				return true;
			}
		}
		return false;
	}

	private static final void construct(CodeBuilder builder, NTableField field,
			boolean fromPrimary) throws IOException {
		final boolean set = toSetAttrs(field);
		builder.appendLine("this.%s %s= %s.%sField(%s, %s);",
				declareNameOf(field), set ? "= field " : "",
				fromPrimary ? THIS_TABLE : DBTABLE,
				field.primaryKey ? "newPrimary" : "new",
				declareNameOfFN(field), defineType(field.type.getType(null)));
		if (set) {
			if (field.defaultValue != null) {
				ConstExpr expr = (ConstExpr) new SQLExprContext(null, false)
						.build(field.defaultValue);
				builder.append("%s.setDefault(", FIELD);
				expr.visit(builder, null);
				builder.appendLine(");");
			}
			if (field.notNull) {
				builder.appendLine("%s.setKeepValid(true);", FIELD);
			}
		}
	}

	private static final void beginConstructor(CodeBuilder builder,
			int modifier, String clzname) throws IOException {
		builder.appendLine("%s %s() {", Modifier.toString(modifier), clzname)
				.pi();
	}

	private static final void endConstructor(CodeBuilder builder)
			throws IOException {
		builder.ri();
		builder.appendLine('}');
		builder.appendLine();
	}

	private static final void beginDeclareUseRef(CodeBuilder builder)
			throws IOException {
		builder.appendLine("@Override");
		builder.appendLine("protected void declareUseRef(%s querier) {",
				ObjectQuerier.class.getSimpleName());
		builder.pi();
	}

	private static final void endDeclareUseRef(CodeBuilder builder)
			throws IOException {
		builder.ri();
		builder.appendLine('}');
	}

}