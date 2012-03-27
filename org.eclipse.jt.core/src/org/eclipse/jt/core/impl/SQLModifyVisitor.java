package org.eclipse.jt.core.impl;

import java.util.ArrayList;

import org.eclipse.jt.core.def.exp.ValueExpression;
import org.eclipse.jt.core.def.query.DeleteStatementDeclarator;
import org.eclipse.jt.core.def.query.InsertStatementDeclarator;
import org.eclipse.jt.core.def.query.UpdateStatementDeclarator;
import org.eclipse.jt.core.def.table.TableDefine;
import org.eclipse.jt.core.spi.sql.SQLAliasDuplicateException;
import org.eclipse.jt.core.spi.sql.SQLAliasUndefinedException;
import org.eclipse.jt.core.spi.sql.SQLColumnNotFoundException;
import org.eclipse.jt.core.spi.sql.SQLNotSupportedException;
import org.eclipse.jt.core.spi.sql.SQLOperandTypeException;
import org.eclipse.jt.core.spi.sql.SQLRelationNotFoundException;
import org.eclipse.jt.core.spi.sql.SQLSyntaxException;
import org.eclipse.jt.core.spi.sql.SQLTableNotFoundException;


class SQLModifyVisitor extends SQLDmlVisitor<SQLModifyContext> {
	final static SQLModifyVisitor VISITOR = new SQLModifyVisitor();

	private TString getMasterTableName(NSource s) {
		NSource src = s;
		while (src instanceof NSourceJoin) {
			src = ((NSourceJoin) src).left;
		}
		if (src instanceof NSourceTable) {
			return ((NSourceTable) src).source.name;
		}
		if (src instanceof NSourceRelate) {
			NSourceRelate r = (NSourceRelate) src;
			return r.table;
		}
		if (src instanceof NSourceSubQuery) {
			throw new SQLNotSupportedException(src.startLine(), src.startCol(),
					"子查询不能作为主表");
		}
		throw new SQLSyntaxException(src.startLine(), src.startCol(),
				"无法获取主表名称");
	}

	@Override
	public void visitInsertDeclare(SQLModifyContext visitorContext,
			NInsertDeclare i) {
		TString name = i.body.insert.table.name;
		TableDefineImpl table = (TableDefineImpl) visitorContext.querier.find(
				TableDefine.class, name.value);
		if (table == null) {
			throw new SQLTableNotFoundException(name.line, name.col, name.value);
		}
		InsertStatementImpl insert = new InsertStatementImpl(i.name.value,
				table, (InsertStatementDeclarator) visitorContext.declarator);
		visitorContext.rootStmt = insert;
		this.appendArguments(visitorContext, i, insert);
		i.body.accept(visitorContext, this);
	}

	@Override
	public void visitUpdateDeclare(SQLModifyContext visitorContext,
			NUpdateDeclare u) {
		TString name = this.getMasterTableName(u.body.update.source);
		TableDefineImpl table = (TableDefineImpl) visitorContext.querier.find(
				TableDefine.class, name.value);
		if (table == null) {
			throw new SQLTableNotFoundException(name.line, name.col, name.value);
		}
		UpdateStatementImpl update = new UpdateStatementImpl(u.name.value,
				table, (UpdateStatementDeclarator) visitorContext.declarator);
		visitorContext.rootStmt = update;
		this.appendArguments(visitorContext, u, update);
		u.body.accept(visitorContext, this);
	}

	@Override
	public void visitDeleteDeclare(SQLModifyContext visitorContext,
			NDeleteDeclare d) {
		TString name = this.getMasterTableName(d.body.delete.source);
		TableDefineImpl table = (TableDefineImpl) visitorContext.querier.find(
				TableDefine.class, name.value);
		if (table == null) {
			throw new SQLTableNotFoundException(name.line, name.col, name.value);
		}
		DeleteStatementImpl delete = new DeleteStatementImpl(d.name.value,
				table, (DeleteStatementDeclarator) visitorContext.declarator);
		visitorContext.rootStmt = delete;
		this.appendArguments(visitorContext, d, delete);
		d.body.accept(visitorContext, this);
	}

	@Override
	public void visitInsertStmt(SQLModifyContext visitorContext, NInsertStmt i) {
		InsertStatementImpl insert = (InsertStatementImpl) visitorContext.rootStmt;
		if (insert == null) {
			throw new IllegalStateException();
		}
		i.values.accept(visitorContext, this);
	}

	@Override
	public void visitInsertValues(SQLModifyContext visitorContext,
			NInsertValues v) {
		if (v.columns == null || v.columns.length == 0) {
			throw new SQLSyntaxException(v.startLine(), v.startCol(), "缺少字段列表");
		}
		if (v.values == null || v.values.length == 0) {
			throw new SQLSyntaxException(v.startLine(), v.startCol(), "缺少值列表");
		}
		if (v.columns.length != v.values.length) {
			throw new SQLSyntaxException(v.startLine(), v.startCol(),
					"值的数目与字段数目不相等");
		}
		int count = v.columns.length;
		ArrayList<TableFieldDefineImpl> arr = new ArrayList<TableFieldDefineImpl>(
				count);
		boolean found = false;
		InsertStatementImpl insert = (InsertStatementImpl) visitorContext.rootStmt;
		TableDefineImpl table = insert.moTableRef.target;
		SQLExprContext exprContext = new SQLExprContext(visitorContext, insert);
		for (int j = 0; j < count; j++) {
			TString c = v.columns[j];
			TableFieldDefineImpl f = table.fields.find(c.value);
			if (f == null) {
				throw new SQLColumnNotFoundException(c.line, c.col, c.value);
			}
			if (arr.contains(f)) {
				throw new SQLSyntaxException(c.line, c.col, "字段已经存在列表中");
			}
			NValueExpr value = v.values[j];
			ValueExpression val = exprContext.build(value);
			switch (f.getType().isAssignableFrom(val.getType())) {
			case CONVERT:
			case NO:
				throw new SQLOperandTypeException(value.startLine(),
						value.startCol(), "值类型与字段类型不同");
			}
			insert.assignExpression(f, val);
			if (f.isRECID()) {
				found = true;
			}
		}
		if (!found) {
			throw new SQLNotSupportedException(v.startLine(), v.startCol(),
					"INSERT语句缺少RECID字段的插入值");
		}
	}

	@Override
	public void visitInsertSubQuery(SQLModifyContext visitorContext,
			NInsertSubQuery q) {
		InsertStatementImpl insert = (InsertStatementImpl) visitorContext.rootStmt;
		DerivedQueryImpl sub = insert.getInsertValues();
		new SQLQueryContext(visitorContext).build(sub, q.query, null);
		boolean found = false;
		if (q.query == null) {
			throw new SQLSyntaxException(q.query.startLine(),
					q.query.startCol(), "语法错误");
		}
		TableDefineImpl table = insert.moTableRef.target;
		for (NQueryColumn c : q.query.select.columns) {
			if (c.alias == null) {
				throw new SQLSyntaxException(c.expr.endLine(), c.expr.endCol(),
						"缺少别名");
			}
			TableFieldDefineImpl f = table.fields.find(c.alias.value);
			if (f == null) {
				throw new SQLSyntaxException(c.alias.line, c.alias.col,
						"INSERT语句目标表中不存在列'" + c.alias.value + "'");
			}
			if (f.isRECID()) {
				found = true;
			}
		}
		if (!found) {
			throw new SQLNotSupportedException(q.startLine(), q.startCol(),
					"INSERT语句缺少RECID字段的插入值");
		}
	}

	@Override
	public void visitUpdateStmt(SQLModifyContext visitorContext, NUpdateStmt u) {
		UpdateStatementImpl update = (UpdateStatementImpl) visitorContext.rootStmt;
		if (update == null) {
			throw new IllegalStateException();
		}
		visitorContext.returnRef = null;
		u.update.source.accept(visitorContext, this);
		SQLExprContext exprContext = new SQLExprContext(visitorContext, update);
		TableDefineImpl table = update.moTableRef.target;
		for (NUpdateColumnValue c : u.set.columns) {
			TableFieldDefineImpl f = table.getFields().find(c.column.value);
			if (f == null) {
				throw new SQLColumnNotFoundException(c.startLine(),
						c.startCol(), c.column.value);
			}
			update.assignExpression(f,
					exprContext.build(c.value, visitorContext));
		}
		if (u.where != null) {
			update.setCondition(exprContext.build(u.where.expr, visitorContext));
		}
	}

	@Override
	public void visitDeleteStmt(SQLModifyContext visitorContext, NDeleteStmt d) {
		DeleteStatementImpl delete = (DeleteStatementImpl) visitorContext.rootStmt;
		if (delete == null) {
			throw new IllegalStateException();
		}
		visitorContext.returnRef = null;
		d.delete.source.accept(visitorContext, this);
		if (d.where != null) {
			SQLExprContext exprContext = new SQLExprContext(visitorContext,
					delete);
			delete.setCondition(exprContext.build(d.where.expr, visitorContext));
		}
	}

	@Override
	public void visitSourceJoin(SQLModifyContext visitorContext, NSourceJoin j) {
		j.left.accept(visitorContext, this);
		// 总是返回左边的引用
		MoRelationRefImpl<?, ?, ?> ref = visitorContext.returnRef;
		j.right.accept(visitorContext, this);
		MoJoinedRelationRefImpl<?> join = (MoJoinedRelationRefImpl<?>) visitorContext.returnRef;
		visitorContext.returnRef = ref;
		join.setJoinType(j.joinType);
		SQLExprContext exprContext = new SQLExprContext(visitorContext,
				visitorContext.rootStmt);
		exprContext.resolver = visitorContext;
		join.setJoinCondition(exprContext.build(j.condition));
	}

	@Override
	public void visitSourceRelate(SQLModifyContext visitorContext,
			NSourceRelate r) {
		r.source.accept(visitorContext, this);
		SQLRelationProvider provider = visitorContext.findProvider(
				SQLRelationProvider.class, r.table.value);
		if (provider == null) {
			throw new SQLAliasUndefinedException(r.table.startLine(),
					r.table.startCol(), r.table.value);
		}
		TableRelationDefineImpl rel = provider.findRelation(r.rel.value);
		if (rel == null) {
			throw new SQLRelationNotFoundException(r.rel.line, r.rel.col,
					r.rel.value);
		}
		ModifyStatementImpl stmt = (ModifyStatementImpl) visitorContext.rootStmt;
		String alias;
		if (r.alias != null) {
			alias = r.alias.value;
			if (stmt.findRelationRef(alias) != null) {
				throw new SQLAliasDuplicateException(r.alias.line, r.alias.col,
						alias);
			}
		} else {
			// 省略别名，则将关系名作为别名
			alias = r.rel.value;
			if (stmt.findRelationRef(alias) != null) {
				throw new SQLAliasDuplicateException(r.rel.line, r.rel.col,
						alias);
			}
		}
		MoJoinedTableRef join = visitorContext.returnRef.newJoin(rel, alias);
		join.setJoinType(r.joinType);
	}

	@Override
	public void visitSourceTable(SQLModifyContext visitorContext, NSourceTable t) {
		ModifyStatementImpl stmt = (ModifyStatementImpl) visitorContext.rootStmt;
		String alias;
		if (t.alias != null) {
			alias = t.alias.value;
			if (stmt.findRelationRef(alias) != null) {
				throw new SQLAliasDuplicateException(t.alias.line, t.alias.col,
						alias);
			}
		} else {
			// 省略别名，则将表名作为别名
			alias = t.source.name.value;
			if (stmt.findRelationRef(alias) != null) {
				throw new SQLAliasDuplicateException(t.source.name.line,
						t.source.name.line, alias);
			}
		}
		TString name = t.source.name;
		TableDefineImpl table = (TableDefineImpl) visitorContext.querier.find(
				TableDefine.class, name.value);
		if (table == null) {
			throw new SQLTableNotFoundException(name.line, name.col, name.value);
		}
		MoRelationRefImpl<?, ?, ?> ref = stmt.moTableRef;
		if (visitorContext.returnRef != null) {
			ref = visitorContext.returnRef.newJoin(table, alias);
		} else {
			visitorContext.rootAlias = alias;
		}
		visitorContext.returnRef = ref;
	}

	@Override
	public void visitSourceSubQuery(SQLModifyContext visitorContext,
			NSourceSubQuery q) {
		String alias = q.alias.value;
		ModifyStatementImpl stmt = (ModifyStatementImpl) visitorContext.rootStmt;
		if (stmt.findRelationRef(alias) != null) {
			throw new SQLAliasDuplicateException(q.alias.line, q.alias.col,
					alias);
		}
		DerivedQueryImpl sub = stmt.newDerivedQuery();
		new SQLQueryContext(visitorContext).build(sub, q.query, null);
		if (visitorContext.returnRef == null) {
			throw new IllegalStateException();
		}
		MoJoinedQueryRef ref = visitorContext.returnRef.newJoin(sub,
				q.alias.value);
		visitorContext.returnRef = ref;
	}
}
