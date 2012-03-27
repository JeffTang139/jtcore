package org.eclipse.jt.core.impl;

import java.lang.ref.WeakReference;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * 通过代理接口提供开发使用的数据库资源的访问器
 * 
 * <p>
 * 该对象会使用数据库资源,在对象被销毁前必须释放使用的资源.
 * 
 * <p>
 * TProxy为提供用户使用的Holder的接口,组合了真正使用(占有)数据库资源的Holder,并通过Holder来提供接口的实现.
 * 当TProxy对象不再为强可及对象时,即表示其组合Holder可以被释放了.
 * 
 * 
 * @author Jeff Tang
 * 
 * @param <TProxy>
 *            提供用户使用的Holder的接口
 */
abstract class PsHolder<TProxy extends PsHolderProxy> extends
		WeakReference<TProxy> {

	final DBAdapterImpl adapter;

	/**
	 * 当前命令代
	 */
	final short depth;

	PsHolder(DBAdapterImpl dbAdapter, TProxy referent) {
		super(referent, dbAdapter);
		this.adapter = dbAdapter;
		this.depth = dbAdapter.getContext().getDepth();
	}

	/**
	 * 当前容器所拥有的语句数量
	 */
	private int statements;

	final void activeChanged(boolean active) {
		if (active) {
			if (this.statements++ == 0) {
				this.adapter.active(this);
			}
		} else {
			if (--this.statements == 0) {
				this.adapter.disactive(this);
			}
		}
	}

	final PsHolder<?> removeFromChain(PsHolder<?> head) {
		final PsHolder<?> prev = this.prevHolder;
		final PsHolder<?> next = this.nextHolder;
		if (next != null) {
			next.prevHolder = prev;
			this.nextHolder = null;
		}
		if (prev != null) {
			prev.nextHolder = next;
			this.prevHolder = null;
		} else if (this == head) {
			head = next;
		}
		return head;
	}

	PsHolder<?> prevHolder;

	PsHolder<?> nextHolder;

	protected abstract void unuse();

	static final void setArgumentValues(Object argValueObj,
			IStatement statement, Object... argValues) {
		final ArrayList<StructFieldDefineImpl> args = statement.getArguments();
		for (int i = 0, c = Math.min(args.size(), argValues.length); i < c; i++) {
			args.get(i).setFieldValueAsObject(argValueObj, argValues[i]);
		}
	}

	static abstract class HoldedExecutor<TSql extends Sql> extends
			PsExecutor<TSql> {

		final PsHolder<?> holder;

		HoldedExecutor<?> next;

		HoldedExecutor(PsHolder<?> holder, TSql sql) {
			super(holder.adapter, sql);
			this.holder = holder;
		}

		@Override
		protected void activeChanged(boolean active) {
			this.holder.activeChanged(active);
		}

	}

	static final class StatementExecutor extends HoldedExecutor<Sql> {

		StatementExecutor(PsHolder<?> holder, IStatement statement) {
			super(holder, statement.getSql(holder.adapter));
		}

	}

	static final class TopQuerier extends HoldedExecutor<QueryTopSql> {

		TopQuerier(PsHolder<?> holder, QueryStatementBase query) {
			super(holder, query.getQueryTopSql(holder.adapter));
		}

		final ResultSet executeQuery(Object argsObj, long limit) {
			try {
				super.use(false);
				this.flushParameters(argsObj);
				this.sql.top.setLong(this.ps, limit);
				return this.adapter.jdbcQuery(this);
			} catch (SQLException e) {
				throw Utils.tryThrowException(e);
			}
		}

	}

	static final class LimitQuerier extends HoldedExecutor<QueryLimitSql> {

		LimitQuerier(PsHolder<?> holder, QueryStatementBase query) {
			super(holder, query.getQueryLimitSql(holder.adapter));
		}

		final ResultSet executeQuery(Object argsObj, long limit, long offset) {
			try {
				super.use(false);
				this.flushParameters(argsObj);
				this.sql.limit.setLong(this.ps, limit);
				this.sql.offset.setLong(this.ps, offset);
				return this.adapter.jdbcQuery(this);
			} catch (SQLException e) {
				throw Utils.tryThrowException(e);
			}
		}

	}

	static final class RowCountQuerier extends HoldedExecutor<QueryRowCountSql> {

		RowCountQuerier(PsHolder<?> holder, QueryStatementBase statement) {
			super(holder, statement.getQueryRowCountSql(holder.adapter));
		}

	}

}
