package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.ObjectQuerier;
import org.eclipse.jt.core.def.table.TableDefine;
import org.eclipse.jt.core.def.table.TableFieldDefine;
import org.eclipse.jt.core.def.table.TableRelationType;
import org.eclipse.jt.core.spi.sql.SQLColumnNotFoundException;
import org.eclipse.jt.core.spi.sql.SQLNameRedefineException;
import org.eclipse.jt.core.spi.sql.SQLNotSupportedException;
import org.eclipse.jt.core.spi.sql.SQLTableNotFoundException;
import org.eclipse.jt.core.type.DataType;

class SQLTableVisitor extends VisitorBase<SQLTableContext> {
	final static SQLTableVisitor VISITOR = new SQLTableVisitor();

	public final static TableDeclareStub build(ObjectQuerier querier,
			NTableDeclare t) {
		SQLTableContext visitorContext = new SQLTableContext(querier, null);
		t.accept(visitorContext, SQLTableVisitor.VISITOR);
		return (TableDeclareStub) visitorContext.rootStmt;
	}

	@Override
	public void visitAbstractTableDeclare(SQLTableContext visitorContext,
			NAbstractTableDeclare t) {
		// DO NOTHING
	}

	@Override
	public void visitTableDeclare(SQLTableContext visitorContext,
			NTableDeclare t) {
		TableDefineImpl table = new TableDefineImpl(t.name.value,
				visitorContext.declarator);
		NTablePrimary primary = t.getMergedPrimary();
		NTableExtend[] extend = t.getMergedExtend();
		NTableIndex[] index = t.getMergedIndex();
		NTableHierarchy[] hierarchy = t.getMergedHierarchy();
		NTablePartition partition = t.getMergedPartition();
		SQLExprContext exprContext = new SQLExprContext(visitorContext, null);
		for (NTableField f : primary.fields) {
			fillField(exprContext, table, table.primary, f);
		}
		if (extend != null) {
			for (NTableExtend ext : extend) {
				if (table.dbTables != null
						&& table.dbTables.find(ext.name.value) != null) {
					throw new SQLNameRedefineException(ext.name.line,
							ext.name.col, ext.name.value);
				}
				DBTableDefineImpl et = table.newDBTable(ext.name.value);
				for (NTableField f : ext.fields) {
					fillField(exprContext, table, et, f);
				}
			}
		}
		if (index != null) {
			for (NTableIndex i : index) {
				fillIndex(visitorContext, table, i);
			}
		}
		if (hierarchy != null) {
			for (NTableHierarchy h : hierarchy) {
				fillHierarchy(visitorContext, table, h);
			}
		}
		if (partition != null) {
			fillPartition(visitorContext, table, partition);
		}
		visitorContext.table = table;
		TableDeclareStub stub = new TableDeclareStub(visitorContext);
		stub.mergedPrimary = primary;
		stub.mergedExtend = extend;
		stub.mergedRelations = t.getMergedRelation();
		visitorContext.rootStmt = stub;
	}

	final void fillField(SQLExprContext visitorContext,
			TableDefineImpl primary, DBTableDefineImpl extend, NTableField f) {
		String name = f.name.value;
		DataType type = f.type.getType(visitorContext.querier);
		switch (TypeCategory.typeOf(type)) {
		case FIELD:
		case BOTH:
			break;
		default:
			throw new SQLNotSupportedException(f.name.line, f.name.col,
					"不支持字段类型 '" + type + "'");
		}
		if (primary.fields != null && primary.fields.find(name) != null) {
			throw new SQLNameRedefineException(f.name.line, f.name.col, name);
		}
		TableFieldDefineImpl field = extend.newField(name, type);
		if (f.primaryKey) {
			field.setPrimaryKey(true);
		}
		if (f.defaultValue != null) {
			field.setDefault(visitorContext.build(f.defaultValue));
		}
		if (f.notNull) {
			field.setKeepValid(true);
		}
	}

	final void fillIndex(SQLTableContext visitorContext, TableDefineImpl table,
			NTableIndex i) {
		String name = i.name.value;
		if (table.indexes != null && table.indexes.find(name) != null) {
			throw new SQLNameRedefineException(i.name.line, i.name.col, name);
		}
		NTableIndexField first = i.fields[0];
		TableFieldDefineImpl ff = table.fields.find(first.name.value);
		if (ff == null) {
			throw new SQLColumnNotFoundException(first.name.line,
					first.name.col, first.name.value);
		}
		IndexDefineImpl index = table.newIndex(name, ff);
		if (i.unique) {
			index.setUnique(true);
		}
		if (first.desc) {
			index.items.get(0).setDesc(true);
		}
		for (int j = 1, c = i.fields.length; j < c; j++) {
			NTableIndexField other = i.fields[j];
			TableFieldDefineImpl of = table.fields.find(other.name.value);
			if (of == null) {
				throw new SQLColumnNotFoundException(other.name.line,
						other.name.col, other.name.value);
			}
			if (index.owner != of.owner) {
				throw new SQLNotSupportedException(other.name.line,
						other.name.col, "不支持跨表索引 ，字段所在表与索引所在表不同 '" + other.name
								+ "'");
			}
			index.addItem(of, other.desc);
		}
	}

	final void fillHierarchy(SQLTableContext visitorContext,
			TableDefineImpl table, NTableHierarchy h) {
		String name = h.name.value;
		if (table.hierarchies != null && table.hierarchies.find(name) != null) {
			throw new SQLNameRedefineException(h.name.line, h.name.col, name);
		}
		int limit = h.limit.value;
		if (limit <= 0) {
			throw new SQLNotSupportedException(h.limit.line, h.limit.col,
					"不支持0和负数 '" + limit + "'");
		}
		table.newHierarchy(name, limit);
	}

	final void fillPartition(SQLTableContext visitorContext,
			TableDefineImpl table, NTablePartition p) {
		TString tok = p.fields[0];
		TableFieldDefineImpl first = table.fields.find(tok.value);
		if (first == null) {
			throw new SQLColumnNotFoundException(tok.line, tok.col, tok.value);
		}
		int len = p.fields.length;
		if (len > 1) {
			TableFieldDefineImpl[] others = new TableFieldDefineImpl[len - 1];
			for (int i = 1; i < len; i++) {
				tok = p.fields[i];
				TableFieldDefineImpl f = table.fields.find(tok.value);
				if (f == null) {
					throw new SQLColumnNotFoundException(tok.line, tok.col,
							tok.value);
				}
				others[i - 1] = f;
			}
			table.setPartitionFields(first, others);
		} else {
			table.setPartitionFields(first);
		}
		if (p.limit.value <= 0) {
			throw new SQLNotSupportedException(p.limit.line, p.limit.col,
					"不支持0和负数 '" + p.limit.value + "'");
		}
		table.setMaxPartitionCount(p.limit.value);
		if (p.size.value <= 0) {
			throw new SQLNotSupportedException(p.size.line, p.size.col,
					"不支持0和负数 '" + p.size.value + "'");
		}
		table.setParitionSuggestion(p.size.value);
	}

	final void fillRelations(SQLTableContext visitorContext,
			TableDeclareStub stub) {
		if (stub.mergedPrimary != null) {
			TableDefineImpl table = stub.getTable();
			for (NTableField f : stub.mergedPrimary.fields) {
				this.fillForeignKey(visitorContext, table, f);
			}
			if (stub.mergedExtend != null) {
				for (NTableExtend ext : stub.mergedExtend) {
					for (NTableField f : ext.fields) {
						this.fillForeignKey(visitorContext, table, f);
					}
				}
				stub.mergedExtend = null;
			}
			if (stub.mergedRelations != null) {
				for (NTableRelation r : stub.mergedRelations) {
					this.fillRelation(visitorContext, table, r);
				}
				stub.mergedRelations = null;
			}
			stub.mergedPrimary = null;
		}
	}

	final void fillForeignKey(SQLTableContext visitorContext,
			TableDefineImpl primary, NTableField f) {
		if (f.foreignKey != null) {
			NTableForeignKey fk = f.foreignKey;
			String refName = fk.name.value;
			String refTable = fk.refTable.value;
			String refField = fk.refField.value;
			TableRelationDefineImpl ref;
			TableDefine target = visitorContext.querier.find(TableDefine.class,
					refTable);
			if (target == null) {
				throw new SQLTableNotFoundException(fk.refTable.line,
						fk.refTable.col, refTable);

			}
			ref = primary.newRelation(refName, target,
					TableRelationType.REFERENCE);
			TableFieldDefine rf = target.getFields().find(refField);
			if (rf == null) {
				throw new SQLColumnNotFoundException(fk.refField.line,
						fk.refField.col, refField);
			}
			ref.setJoinCondition(ref.expOf(rf).xEq(
					primary.expOf(primary.getFields().get(f.name.value))));
		}
	}

	final void fillRelation(SQLTableContext visitorContext,
			TableDefineImpl table, NTableRelation r) {
		String name = r.name.value;
		if (table.relations != null && table.relations.find(name) != null) {
			throw new SQLNameRedefineException(r.name.line, r.name.col, name);
		}
		TableDefine target = visitorContext.querier.find(TableDefine.class,
				r.target.value);
		if (target == null) {
			throw new SQLTableNotFoundException(r.target.line, r.target.col,
					r.target.value);
		}
		table.newRelation(name, target, TableRelationType.REFERENCE)
				.setJoinCondition(
						new SQLExprContext(visitorContext, null).build(r.expr,
								visitorContext));
	}
}
