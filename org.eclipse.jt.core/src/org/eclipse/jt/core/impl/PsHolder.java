package org.eclipse.jt.core.impl;

import java.lang.ref.WeakReference;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * ͨ������ӿ��ṩ����ʹ�õ����ݿ���Դ�ķ�����
 * 
 * <p>
 * �ö����ʹ�����ݿ���Դ,�ڶ�������ǰ�����ͷ�ʹ�õ���Դ.
 * 
 * <p>
 * TProxyΪ�ṩ�û�ʹ�õ�Holder�Ľӿ�,���������ʹ��(ռ��)���ݿ���Դ��Holder,��ͨ��Holder���ṩ�ӿڵ�ʵ��.
 * ��TProxy������Ϊǿ�ɼ�����ʱ,����ʾ�����Holder���Ա��ͷ���.
 * 
 * 
 * @author Jeff Tang
 * 
 * @param <TProxy>
 *            �ṩ�û�ʹ�õ�Holder�Ľӿ�
 */
abstract class PsHolder<TProxy extends PsHolderProxy> extends
		WeakReference<TProxy> {

	final DBAdapterImpl adapter;

	/**
	 * ��ǰ�����
	 */
	final short depth;

	PsHolder(DBAdapterImpl dbAdapter, TProxy referent) {
		super(referent, dbAdapter);
		this.adapter = dbAdapter;
		this.depth = dbAdapter.getContext().getDepth();
	}

	/**
	 * ��ǰ������ӵ�е��������
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
