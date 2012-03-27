package org.eclipse.jt.core.impl;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.jt.core.def.FieldDefine;
import org.eclipse.jt.core.def.exp.ValueExpression;
import org.eclipse.jt.core.def.obja.DynamicObject;
import org.eclipse.jt.core.def.query.QueryStatementDeclare;
import org.eclipse.jt.core.def.query.RelationColumnDefine;
import org.eclipse.jt.core.def.query.SelectDefine;
import org.eclipse.jt.core.exception.InvalidStatementDefineException;
import org.eclipse.jt.core.exception.NamedDefineExistingException;
import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.type.DataTypable;
import org.eclipse.jt.core.type.DataType;
import org.eclipse.jt.core.type.Digester;
import org.eclipse.jt.core.type.Type;
import org.eclipse.jt.core.type.TypeDetector;


public abstract class QueryStatementBase extends
		SelectImpl<QueryStatementBase, QueryColumnImpl> implements
		QueryStatementDeclare, IStatement, Withable {

	public final boolean ignorePrepareIfDBInvalid() {
		return true;
	}

	public final Type getRootType() {
		return this;
	}

	public final void digestType(Digester digester) {
		digester.update(TypeCodeSet.QUERY_H);
		this.digestAuthAndName(digester);
		short c = (short) this.columns.size();
		digester.update(c);
		for (int i = 0; i < c; i++) {
			this.columns.get(i).digestType(digester);
		}
	}

	public final <TResult, TUserData> TResult detect(
			TypeDetector<TResult, TUserData> detector, TUserData userData)
			throws UnsupportedOperationException {
		try {
			return detector.inQuery(userData, this);
		} catch (Throwable e) {
			throw Utils.tryThrowException(e);
		}
	}

	public final NamedDefineContainerImpl<DerivedQueryImpl> getWiths() {
		return this.withs;
	}

	public final DerivedQueryImpl getWith(String name) {
		return this.withs.get(name);
	}

	public final DerivedQueryImpl newWith(String name) {
		if (name == null || name.length() == 0) {
			throw new NullArgumentException("内联查询名称");
		}
		if (this.withs.contains(name)) {
			throw new NamedDefineExistingException("名称为[" + name
					+ "]的with块定义已经存在.");
		}
		DerivedQueryImpl query = new DerivedQueryImpl(this, name, true);
		this.withs.add(query);
		return query;
	}

	public final DerivedQueryImpl newDerivedQuery(SelectDefine sample) {
		DerivedQueryImpl query = new DerivedQueryImpl(this);
		SelectImpl<?, ?> from = (SelectImpl<?, ?>) sample;
		if (from instanceof Withable) {
			Withable withable = (Withable) from;
			for (DerivedQueryImpl with : withable.getWiths()) {
				DerivedQueryImpl withClone = this.newWith(with.getName());
				with.cloneSelectTo(withClone, this);
			}
		}
		from.cloneSelectTo(query, this);
		return query;
	}

	@Override
	public final MetaBaseContainerImpl<? extends OrderByItemImpl> getOrderBys() {
		return this.orderbys;
	}

	@Override
	public final OrderByItemImpl newOrderBy(ValueExpression expr) {
		return this.newOrderBy(expr, false);
	}

	@Override
	public final OrderByItemImpl newOrderBy(ValueExpression expr, boolean isDesc) {
		this.checkModifiable();
		if (expr == null) {
			throw new NullArgumentException("排序表达式");
		}
		ValueExpr value = (ValueExpr) expr;
		if (SystemVariables.VALIDATE_EXPR_DOMAIN) {
			value.validateDomain(this);
		}
		OrderByItemImpl orderby = new OrderByItemImpl(this, value);
		orderby.setDesc(isDesc);
		return this.addOrderBy(orderby);
	}

	@Override
	public final OrderByItemImpl newOrderBy(RelationColumnDefine column) {
		return this.newOrderBy(column, false);
	}

	@Override
	public final OrderByItemImpl newOrderBy(RelationColumnDefine column,
			boolean isDesc) {
		this.checkModifiable();
		if (column == null) {
			throw new NullArgumentException("排序的关系列定义");
		}
		OrderByItemImpl orderby;
		if (column instanceof QueryColumnImpl) {
			orderby = new OrderByItemImpl(this, new QueryColumnRefExpr(
					(QueryColumnImpl) column));
		} else {
			orderby = new OrderByItemImpl(this, this.exprOf(column));
		}
		orderby.setDesc(isDesc);
		return this.addOrderBy(orderby);
	}

	public final NamedDefineContainerImpl<StructFieldDefineImpl> getArguments() {
		return this.args.fields;
	}

	public final StructFieldDefineImpl newArgument(String name, DataType type) {
		return this.args.newField(name, type);
	}

	public final StructFieldDefineImpl newArgument(String name,
			DataTypable typable) {
		return this.args.newField(name, typable);
	}

	public final StructFieldDefineImpl newArgument(FieldDefine sample) {
		return this.args.newField(sample);
	}

	public final Class<?> getAOClass() {
		return this.args.soClass;
	}

	public final Object newAO() {
		return this.args.newInitedSO();
	}

	public final Object newAO(Object... args) {
		return this.args.valuesAsSo(args);
	}

	public final StructFieldDefineImpl getArgument(String name) {
		return this.args.fields.get(name);
	}

	public final StructDefineImpl getArgumentsDefine() {
		return this.args;
	}

	final StructDefineImpl args;

	final NamedDefineContainerImpl<DerivedQueryImpl> withs = new NamedDefineContainerImpl<DerivedQueryImpl>();

	MetaBaseContainerImpl<OrderByItemImpl> orderbys;

	StructDefineImpl mapping;

	QueryStatementBase(String name) {
		super(name);
		this.args = new ArgumentsDefine(DynamicObject.class);
	}

	QueryStatementBase(String name, StructDefineImpl args) {
		super(name);
		if (args == null) {
			throw new NullPointerException();
		}
		this.args = args;
	}

	public final RelationRefDomain getDomain() {
		return null;
	}

	final QueryColumnImpl newColumn(String name, ValueExpr expr,
			StructFieldDefineImpl sf) {
		QueryColumnImpl c = super.newColumn(name, expr);
		if (sf != null && sf.owner == this.mapping) {
			c.field = sf;
		}
		return c;
	}

	@Override
	protected QueryColumnImpl newColumnOnly(String name, ValueExpr expr) {
		return new QueryColumnImpl(this, name, expr);
	}

	private final OrderByItemImpl addOrderBy(OrderByItemImpl orderby) {
		if (this.orderbys == null) {
			this.orderbys = new MetaBaseContainerImpl<OrderByItemImpl>();
		}
		this.orderbys.add(orderby);
		return orderby;
	}

	final HashMap<QuRelationRef, Boolean> forUpdates = new HashMap<QuRelationRef, Boolean>();

	final void setForUpdate(QuRelationRef relationRef, boolean forUpdate) {
		if (relationRef.getOwner() != this) {
			throw new IllegalArgumentException();
		}
		this.forUpdates.put(relationRef, forUpdate);

	}

	final boolean isForUpdate(QuRelationRef relationRef) {
		if (relationRef.getOwner() != this) {
			throw new IllegalArgumentException();
		}
		Boolean forUpdate = this.forUpdates.get(relationRef);
		return forUpdate == null ? false : forUpdate.booleanValue();
	}

	final void cloneTo(QueryStatementBase target) {
		this.args.cloneFieldsTo(target.args);
		this.cloneWithsTo(target);
		super.cloneSelectTo(target, target);
		this.cloneOrderbysTo(target);
	}

	private final void cloneWithsTo(QueryStatementBase target) {
		for (DerivedQueryImpl with : this.getWiths()) {
			DerivedQueryImpl clone = target.newWith(with.getName());
			with.cloneSelectTo(clone, target);
		}
	}

	private final void cloneOrderbysTo(QueryStatementBase target) {
		if (this.orderbys != null) {
			for (int i = 0, c = this.orderbys.size(); i < c; i++) {
				this.orderbys.get(i).cloneTo(target, target);
			}
		}
	}

	@Override
	public final <TContext> void visit(OMVisitor<TContext> visitor,
			TContext context) {
		if (this.withs != null && this.withs.size() > 0) {
			for (int i = 0, c = this.withs.size(); i < c; i++) {
				this.withs.get(i).visit(visitor, context);
			}
		}
		super.visit(visitor, context);
	}

	final void renderWiths(ISqlQueryBuffer buffer, TableUsages usages) {
		if (this.withs != null) {
			for (int i = 0, c = this.withs.size(); i < c; i++) {
				DerivedQueryImpl with = this.withs.get(i);
				ISqlSelectBuffer wb = buffer.newWith(with.name);
				with.render(wb, usages);
			}
		}
	}

	final void renderOrderbys(ISqlQueryBuffer buffer, TableUsages usages) {
		if (this.orderbys != null && this.orderbys.size() > 0) {
			for (int i = 0, c = this.orderbys.size(); i < c; i++) {
				this.orderbys.get(i).render(buffer, usages);
			}
		}
	}

	final void setRootKeys(TableFieldDefineImpl[] fields,
			ArgumentReserver[] args) {
		if (this.rootRelationRef() instanceof QuRootTableRef) {
			QuRootTableRef tableRef = (QuRootTableRef) this.rootRelationRef();
			TableDefineImpl table = tableRef.getTarget();
			final ArrayList<IndexItemImpl> items = table.logicalKey.items;
			final int c = items.size();
			for (int i = 0; i < c; i++) {
				IndexItemImpl item = items.get(i);
				QueryColumnImpl column = this.findColumn(tableRef,
						item.getField());
				if (column == null) {
					throw new InvalidStatementDefineException("没有输出逻辑表字段["
							+ items.get(i).getField().name + "]");
				}
				TableFieldDefineImpl field = item.field;
				fields[i] = field;
				args[i] = new ArgumentReserver(column.field, field.getType());
			}
			return;
		}
		throw new InvalidStatementDefineException("");
	}

	final QueryColumnImpl[] getRootKeyColumns() {
		if (this.rootRelationRef() instanceof QuRootTableRef) {
			QuRootTableRef tableRef = (QuRootTableRef) this.rootRelationRef();
			TableDefineImpl table = tableRef.getTarget();
			table.checkLogicalKeyAvaiable();
			final ArrayList<IndexItemImpl> items = table.logicalKey.items;
			final int c = items.size();
			QueryColumnImpl[] columns = new QueryColumnImpl[c];
			for (int i = 0; i < c; i++) {
				QueryColumnImpl column = this.findColumn(tableRef, items.get(i)
						.getField());
				if (column == null) {
					throw new InvalidStatementDefineException("没有输出逻辑表字段["
							+ items.get(i).getField().name + "]");
				}
				columns[i] = column;
			}
			return columns;
		}
		throw new InvalidStatementDefineException("");
	}

	final void checkModifyRootOnly() {
		QuRootRelationRef root = this.rootRelationRef();
		for (QuRelationRef relationRef : root) {
			boolean isRoot = relationRef == root;
			boolean isUpdate = this.isForUpdate(relationRef);
			if (isRoot && !isUpdate || !isRoot && isUpdate) {
				throw new InvalidStatementDefineException("查询定义不是只更新根表引用");
			}
		}
	}

	private volatile boolean prepared;

	public final boolean isPrepared() {
		return this.prepared;
	}

	public final void ensurePrepared(ContextImpl<?, ?, ?> context,
			boolean rePrepared) {
		if (rePrepared || !this.prepared) {
			try {
				synchronized (this) {
					if (rePrepared || !this.prepared) {
						this.prepared = true;
						this.doPrepare();
					}
				}
			} catch (Throwable e) {
				throw Utils.tryThrowException(e);
			}
		}
	}

	final boolean supportModify(QuRelationRef relationRef) {
		return this.forUpdates.get(relationRef)
				&& relationRef instanceof QuTableRef;
	}

	void doPrepare() throws Throwable {
		this.args.prepareAccessInfo();
		this.querySql = null;
		this.queryTopSql = null;
		this.queryLimitSql = null;
		this.rowInsertSql = null;
		this.rowDeleteSql = null;
		this.rowUpdateSql = null;
	}

	private volatile Sql querySql;

	public final Sql getSql(DBAdapterImpl dbAdapter) {
		this.ensurePrepared(dbAdapter.getContext(), false);
		Sql querySql = this.querySql;
		if (querySql == null) {
			synchronized (this) {
				querySql = this.querySql;
				if (querySql == null) {
					this.querySql = querySql = new QuerySql(dbAdapter.lang,
							this);
				}
			}
		}
		return querySql;
	}

	private volatile QueryTopSql queryTopSql;

	final QueryTopSql getQueryTopSql(DBAdapterImpl dbAdapter) {
		this.ensurePrepared(dbAdapter.getContext(), false);
		QueryTopSql queryTopSql = this.queryTopSql;
		if (queryTopSql == null) {
			synchronized (this) {
				queryTopSql = this.queryTopSql;
				if (queryTopSql == null) {
					this.queryTopSql = queryTopSql = new QueryTopSql(
							dbAdapter.lang, this);
				}
			}
		}
		return queryTopSql;
	}

	private volatile QueryLimitSql queryLimitSql;

	final QueryLimitSql getQueryLimitSql(DBAdapterImpl dbAdapter) {
		this.ensurePrepared(dbAdapter.getContext(), false);
		QueryLimitSql queryLimitSql = this.queryLimitSql;
		if (queryLimitSql == null) {
			synchronized (this) {
				queryLimitSql = this.queryLimitSql;
				if (queryLimitSql == null) {
					this.queryLimitSql = queryLimitSql = new QueryLimitSql(
							dbAdapter.lang, this);
				}
			}
		}
		return queryLimitSql;
	}

	private volatile QueryRowCountSql queryRowCountSql;

	final QueryRowCountSql getQueryRowCountSql(DBAdapterImpl dbAdapter) {
		this.ensurePrepared(dbAdapter.getContext(), false);
		QueryRowCountSql queryRowCountSql = this.queryRowCountSql;
		if (queryRowCountSql == null) {
			synchronized (this) {
				queryRowCountSql = this.queryRowCountSql;
				if (queryRowCountSql == null) {
					this.queryRowCountSql = queryRowCountSql = new QueryRowCountSql(
							dbAdapter.lang, this);
				}
			}
		}
		return queryRowCountSql;
	}

	private volatile RowInsertSql rowInsertSql;

	final RowInsertSql getRowInsertSql(DBAdapterImpl dbAdapter) {
		this.ensurePrepared(dbAdapter.getContext(), false);
		RowInsertSql recordInsertSql = this.rowInsertSql;
		if (recordInsertSql == null) {
			synchronized (this) {
				recordInsertSql = this.rowInsertSql;
				if (recordInsertSql == null) {
					this.rowInsertSql = recordInsertSql = new RowInsertSql(
							dbAdapter.lang, this);
				}
			}
		}
		return recordInsertSql;
	}

	private volatile RowUpdateSql rowUpdateSql;

	final RowUpdateSql getRowUpdateSql(DBAdapterImpl dbAdapter) {
		this.ensurePrepared(dbAdapter.getContext(), false);
		RowUpdateSql recordUpdateSql = this.rowUpdateSql;
		if (recordUpdateSql == null) {
			synchronized (this) {
				recordUpdateSql = this.rowUpdateSql;
				if (recordUpdateSql == null) {
					this.rowUpdateSql = recordUpdateSql = new RowUpdateSql(
							dbAdapter.lang, this);
				}
			}
		}
		return recordUpdateSql;
	}

	private volatile RowDeleteSql rowDeleteSql;

	final RowDeleteSql getRowDeleteSql(DBAdapterImpl dbAdapter) {
		this.ensurePrepared(dbAdapter.getContext(), false);
		RowDeleteSql recordDeleteSql = this.rowDeleteSql;
		if (recordDeleteSql == null) {
			synchronized (this) {
				recordDeleteSql = this.rowDeleteSql;
				if (recordDeleteSql == null) {
					this.rowDeleteSql = recordDeleteSql = new RowDeleteSql(
							dbAdapter.lang, this);
				}
			}
		}
		return recordDeleteSql;
	}

}
