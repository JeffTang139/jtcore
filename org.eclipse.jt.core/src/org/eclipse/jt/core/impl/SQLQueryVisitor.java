package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.exp.ValueExpression;
import org.eclipse.jt.core.def.query.GroupByType;
import org.eclipse.jt.core.def.query.ORMDeclarator;
import org.eclipse.jt.core.def.query.QueryStatementDeclarator;
import org.eclipse.jt.core.def.table.TableDefine;
import org.eclipse.jt.core.spi.sql.SQLAliasDuplicateException;
import org.eclipse.jt.core.spi.sql.SQLAliasUndefinedException;
import org.eclipse.jt.core.spi.sql.SQLClassNotFoundException;
import org.eclipse.jt.core.spi.sql.SQLColumnNotFoundException;
import org.eclipse.jt.core.spi.sql.SQLNotSupportedException;
import org.eclipse.jt.core.spi.sql.SQLRelationNotFoundException;
import org.eclipse.jt.core.spi.sql.SQLSyntaxException;
import org.eclipse.jt.core.spi.sql.SQLTableNotFoundException;

class SQLQueryVisitor extends SQLDmlVisitor<SQLQueryContext> {
	final static SQLQueryVisitor VISITOR = new SQLQueryVisitor();

	@Override
	public void visitQueryDeclare(SQLQueryContext visitorContext,
			NQueryDeclare q) {
		QueryStatementImpl query = new QueryStatementImpl(q.name.value,
				(QueryStatementDeclarator) visitorContext.declarator);
		visitorContext.rootStmt = query;
		visitorContext.query = query;
		this.appendArguments(visitorContext, q, query);
		q.body.accept(visitorContext, SQLQueryVisitor.VISITOR);
	}

	@Override
	public void visitOrmDeclare(SQLQueryContext visitorContext, NOrmDeclare o) {
		Class<?> entityClass = visitorContext.querier.get(Class.class,
				o.className);
		if (entityClass == null) {
			throw new SQLClassNotFoundException(o.startLine(), o.startCol(),
					o.className);
		}
		MappingQueryStatementImpl query = new MappingQueryStatementImpl(
				o.name.value, entityClass,
				(ORMDeclarator<?>) visitorContext.declarator);
		query.setAutoBind(true);
		visitorContext.rootStmt = query;
		visitorContext.query = query;
		this.appendArguments(visitorContext, o, query);
		o.body.accept(visitorContext, SQLQueryVisitor.VISITOR);
	}

	@Override
	public void visitOrmOverride(SQLQueryContext visitorContext, NOrmOverride o) {
		this.visitOrmDeclare(visitorContext, o);
	}

	@Override
	public void visitQueryStmt(SQLQueryContext visitorContext, NQueryStmt q) {
		QueryStatementBase query = (QueryStatementBase) visitorContext.query;
		if (query == null) {
			throw new IllegalStateException();
		}
		if (q.predefines != null && q.predefines.length > 0) {
			for (NQueryWith w : q.predefines) {
				visitorContext.build(this, query.newWith(w.name.value),
						w.query, null);
			}
		}
		if (q.expr.unions != null) {
			// 检查输出列名称
			for (NQuerySpecific s : q.expr.unions) {
				s.accept(q.expr, this.columnsChecker);
			}
		}
		SQLExprContext exprContext = new SQLExprContext(visitorContext, query);
		this.visitQuerySpecific(visitorContext, q.expr, exprContext);
		// 不支持orderby输出列
		if (q.expr.unions == null && q.order != null) {
			exprContext.canUseSetFunc = false;
			for (NOrderByColumn c : q.order.columns) {
				if (c.column != null) {
					ValueExpression value = exprContext.build(c.column);
					if (c.asc) {
						query.newOrderBy(value);
					} else {
						query.newOrderBy(value, true);
					}
				} else {
					QueryColumnImpl qc = query.columns.find(c.columnName.value);
					if (qc == null) {
						throw new SQLColumnNotFoundException(c.columnName.line,
								c.columnName.col, c.columnName.value);
					}
					if (c.asc) {
						query.newOrderBy(qc);
					} else {
						query.newOrderBy(qc, true);
					}
				}
			}
		}
	}

	private final VisitorBase<NQuerySpecific> columnsChecker = new VisitorBase<NQuerySpecific>() {
		@Override
		public void visitQuerySpecific(NQuerySpecific visitorContext,
				NQuerySpecific s) {
			if (visitorContext != s) {
				if (visitorContext.select.columns.length != s.select.columns.length) {
					throw new SQLSyntaxException(s.startLine(), s.startCol(),
							"输出列数目与主select不一致");
				}
				for (int i = 0, c = visitorContext.select.columns.length; i < c; i++) {
					TString union = s.select.columns[i].alias;
					if (union != null) {
						TString primary = visitorContext.select.columns[i].alias;
						if (primary == null) {
							NValueExpr e = visitorContext.select.columns[i].expr;
							if (e instanceof NColumnRefExpr) {
								primary = ((NColumnRefExpr) e).field;
							}
						}
						if (!union.equals(primary)) {
							throw new SQLSyntaxException(union.line, union.col,
									"输出列名称与主select不一致");
						}
					}
				}
				if (s.unions != null) {
					for (NQuerySpecific u : s.unions) {
						u.accept(visitorContext, this);
					}
				}
			}
		}
	};

	private void visitQuerySpecific(SQLQueryContext visitorContext,
			NQuerySpecific s, SQLExprContext exprContext) {
		SelectImpl<?, ?> q = visitorContext.query;
		if (q == null) {
			throw new IllegalStateException();
		}
		if (s.select.quantifier == SetQuantifier.DISTINCT) {
			q.setDistinct(true);
		}
		visitorContext.returnRef = null;
		for (NSource src : s.from.sources) {
			src.accept(visitorContext, this);
			visitorContext.returnRef = null;
		}
		if (s.group != null && s.group.option == GroupByType.ROLL_UP) {
			exprContext.canUseSetFuncWithDistinct = false;
		} else {
			exprContext.canUseSetFuncWithDistinct = true;
		}
		if (visitorContext.resolver != null) {
			exprContext.resolver = new SQLSourceList(visitorContext,
					visitorContext.resolver);
		} else {
			exprContext.resolver = visitorContext;
		}
		exprContext.canUseSetFunc = true;
		for (NQueryColumn c : s.select.columns) {
			ValueExpression e = exprContext.build(c.expr);
			String name = null;
			if (c.alias != null) {
				name = c.alias.value;
			} else if (c.expr instanceof NColumnRefExpr) {
				// 字段引用省略别名时将字段名作为别名
				name = ((NColumnRefExpr) c.expr).field.value;
			}
			if (name != null) {
				if (q.getColumns().find(name) != null) {
					if (c.alias != null) {
						throw new SQLAliasDuplicateException(c.alias.line,
								c.alias.col, name);
					} else {
						throw new SQLAliasDuplicateException(c.startLine(), c
								.startCol(), name);
					}
				}
				q.newColumn(e, name);
			} else {
				q.newColumn(e);
			}
		}
		exprContext.canUseSetFunc = false;
		if (s.where != null) {
			q.setCondition(exprContext.build(s.where.expr));
		}
		if (s.group != null) {
			// TODO 检查列选表达式是否是GROUPBY表达式
			for (NValueExpr c : s.group.columns) {
				q.newGroupBy(exprContext.build(c));
			}
			q.setGroupByType(s.group.option);
		}
		if (s.having != null) {
			exprContext.canUseSetFunc = true;
			q.setHaving(exprContext.build(s.having.expr));
		}
		if (s.unions != null) {
			for (NQuerySpecific u : s.unions) {
				DerivedQueryImpl sub = q.newDerivedQuery();
				visitorContext.query = sub;
				if (u.unionAll) {
					q.union(sub);
				} else {
					q.unionAll(sub);
				}
				u.accept(visitorContext, this);
			}
			visitorContext.query = q;
		}
	}

	@Override
	public void visitQuerySpecific(SQLQueryContext visitorContext,
			NQuerySpecific s) {
		visitQuerySpecific(visitorContext, s, new SQLExprContext(
				visitorContext, visitorContext.query));
	}

	@Override
	public void visitSourceJoin(SQLQueryContext visitorContext, NSourceJoin j) {
		j.left.accept(visitorContext, this);
		// 总是返回左边的引用
		QuRelationRefImpl<?, ?, ?> ref = visitorContext.returnRef;
		j.right.accept(visitorContext, this);
		QuJoinedRelationRefImpl<?> join = (QuJoinedRelationRefImpl<?>) visitorContext.returnRef;
		visitorContext.returnRef = ref;
		join.setJoinType(j.joinType);
		SQLExprContext exprContext = new SQLExprContext(visitorContext,
				visitorContext.query);
		exprContext.resolver = visitorContext;
		join.setJoinCondition(exprContext.build(j.condition));
	}

	@Override
	public void visitSourceRelate(SQLQueryContext visitorContext,
			NSourceRelate r) {
		r.source.accept(visitorContext, this);
		SQLRelationProvider provider = visitorContext.findProvider(
				SQLRelationProvider.class, r.table.value);
		if (provider == null) {
			throw new SQLAliasUndefinedException(r.table.startLine(), r.table
					.startCol(), r.table.value);
		}
		TableRelationDefineImpl rel = provider.findRelation(r.rel.value);
		if (rel == null) {
			throw new SQLRelationNotFoundException(r.rel.line, r.rel.col,
					r.rel.value);
		}
		String alias;
		if (r.alias != null) {
			alias = r.alias.value;
			if (visitorContext.query.findRelationRef(alias) != null) {
				throw new SQLAliasDuplicateException(r.alias.line, r.alias.col,
						alias);
			}
		} else {
			// 省略别名，则将关系名作为别名
			alias = r.rel.value;
			if (visitorContext.query.findRelationRef(alias) != null) {
				throw new SQLAliasDuplicateException(r.rel.line, r.rel.col,
						alias);
			}
		}
		QuJoinedTableRef join = visitorContext.returnRef.newJoin(rel, alias);
		join.setJoinType(r.joinType);
	}

	@Override
	public void visitSourceTable(SQLQueryContext visitorContext, NSourceTable t) {
		SelectImpl<?, ?> q = visitorContext.query;
		String alias;
		if (t.alias != null) {
			alias = t.alias.value;
			if (q.findRelationRef(alias) != null) {
				throw new SQLAliasDuplicateException(t.alias.line, t.alias.col,
						alias);
			}
		} else {
			// 省略别名，则将表名作为别名
			alias = t.source.name.value;
			if (q.findRelationRef(alias) != null) {
				throw new SQLAliasDuplicateException(t.source.name.line,
						t.source.name.col, alias);
			}
		}
		QuRelationRefImpl<?, ?, ?> ref = null;
		// 查找WITH中预定义的子查询
		if (visitorContext.rootStmt instanceof QueryStatementBase) {
			// QueryStatementImpl或者MappingQueryStatementImpl
			QueryStatementBase query = (QueryStatementBase) visitorContext.rootStmt;
			NamedDefineContainerImpl<DerivedQueryImpl> withs = query.getWiths();
			if (withs != null) {
				DerivedQueryImpl sub = withs.find(t.source.name.value);
				if (sub != null) {
					if (t.forUpdate) {
						throw new SQLNotSupportedException(
								t.source.startLine(), t.source.startCol(),
								"子查询不支持FOR UPDATE");
					}
					if (visitorContext.returnRef != null) {
						ref = visitorContext.returnRef.newJoin(sub, alias);
					} else {
						ref = q.newReference(sub, alias);
					}
				}
			}
		}
		// 查找表
		if (ref == null) {
			TString name = t.source.name;
			TableDefineImpl table = (TableDefineImpl) visitorContext.querier
					.find(TableDefine.class, name.value);
			if (table == null) {
				throw new SQLTableNotFoundException(name.line, name.col,
						name.value);
			}
			if (visitorContext.returnRef != null) {
				ref = visitorContext.returnRef.newJoin(table, alias);
			} else {
				ref = q.newReference(table, alias);
			}
			if (t.forUpdate) {
				ref.setForUpdate(true);
			}
		}
		visitorContext.returnRef = ref;
	}

	@Override
	public void visitSourceSubQuery(SQLQueryContext visitorContext,
			NSourceSubQuery q) {
		SelectImpl<?, ?> query = visitorContext.query;
		// 子查询必须定义别名
		String alias = q.alias.value;
		if (query.findRelationRef(alias) != null) {
			throw new SQLAliasDuplicateException(q.alias.line, q.alias.col,
					alias);
		}
		DerivedQueryImpl sub = query.newDerivedQuery();
		visitorContext.build(this, sub, q.query, visitorContext.resolver);
		QuRelationRefImpl<?, ?, ?> ref;
		if (visitorContext.returnRef != null) {
			ref = visitorContext.returnRef.newJoin(sub, alias);
		} else {
			ref = query.newReference(sub, alias);
		}
		visitorContext.returnRef = ref;
	}
}
