package org.eclipse.jt.core.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.eclipse.jt.core.da.RecordIterateAction;
import org.eclipse.jt.core.def.arg.ArgumentDefine;
import org.eclipse.jt.core.def.obja.DynamicObject;
import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.type.DataType;


/**
 * 数据库命令实现类
 * 
 * @author Jeff Tang
 * 
 */
final class DBCommandImpl extends PsHolder<DBCommandProxy> {

	final int executeUpdate() {
		this.adapter.checkAccessible();
		if (this.statement instanceof ModifyStatementImpl
				|| this.statement instanceof StoredProcedureDefineImpl) {
			return this.executor.executeUpdate(this.argValueObj);
		}
		throw new UnsupportedOperationException("语句定义不支持该操作");
	}

	final RecordSetImpl executeQuery() {
		this.adapter.checkAccessible();
		if (this.statement instanceof QueryStatementImpl) {
			RecordSetImpl rs = new RecordSetImpl(
					(QueryStatementImpl) this.statement);
			try {
				rs.loadRecordSet(this.executor.executeQuery(this.argValueObj));
			} catch (SQLException e) {
				throw Utils.tryThrowException(e);
			}
			return rs;
		}
		throw new UnsupportedOperationException("语句定义不支持该操作");
	}

	final RecordSetImpl executeQueryTop(long rowCount) {
		this.adapter.checkAccessible();
		if (this.statement instanceof QueryStatementImpl) {
			RecordSetImpl rs = new RecordSetImpl(
					(QueryStatementImpl) this.statement);
			try {
				rs.loadRecordSet(this.ensureTopQuerier().executeQuery(
						this.argValueObj, rowCount));
			} catch (SQLException e) {
				throw Utils.tryThrowException(e);
			}
			return rs;
		}
		throw new UnsupportedOperationException("语句定义不支持该操作");
	}

	final RecordSetImpl executeQueryLimit(long offset, long rowCount) {
		this.adapter.checkAccessible();
		if (this.statement instanceof QueryStatementImpl) {
			RecordSetImpl rs = new RecordSetImpl(
					(QueryStatementImpl) this.statement);
			try {
				rs.loadRecordSet(this.ensureLimitQuerier().executeQuery(
						this.argValueObj, rowCount, offset));
			} catch (SQLException e) {
				throw Utils.tryThrowException(e);
			}
			return rs;
		}
		throw new UnsupportedOperationException();
	}

	final void iterateQuery(RecordIterateAction action) {
		this.adapter.checkAccessible();
		if (action == null) {
			throw new NullArgumentException("查询迭代操作");
		}
		if (this.statement instanceof QueryStatementImpl) {
			final RecordSetImpl rs = new RecordSetImpl(
					(QueryStatementImpl) this.statement);
			try {
				rs.iterateResultSet(this.adapter.getContext(),
						this.executor.executeQuery(this.argValueObj), action);
			} catch (Throwable e) {
				throw Utils.tryThrowException(e);
			}
			return;
		}
		throw new UnsupportedOperationException();
	}

	final void iterateQueryTop(RecordIterateAction action, long rowCount) {
		this.adapter.checkAccessible();
		if (action == null) {
			throw new NullArgumentException("查询迭代操作");
		}
		if (this.statement instanceof QueryStatementImpl) {
			RecordSetImpl rs = new RecordSetImpl(
					(QueryStatementImpl) this.statement);
			try {
				rs.iterateResultSet(
						this.adapter.getContext(),
						this.ensureTopQuerier().executeQuery(this.argValueObj,
								rowCount), action);
			} catch (Throwable e) {
				throw Utils.tryThrowException(e);
			}
			return;
		}
		throw new UnsupportedOperationException();
	}

	final void iterateQueryLimit(RecordIterateAction action, long offset,
			long rowCount) {
		this.adapter.checkAccessible();
		if (action == null) {
			throw new NullArgumentException("查询迭代操作");
		}
		if (this.statement instanceof QueryStatementImpl) {
			RecordSetImpl rs = new RecordSetImpl(
					(QueryStatementImpl) this.statement);
			try {
				rs.iterateResultSet(
						this.adapter.getContext(),
						this.ensureLimitQuerier().executeQuery(
								this.argValueObj, rowCount, offset), action);
			} catch (Throwable e) {
				throw Utils.tryThrowException(e);
			}
			return;
		}
		throw new UnsupportedOperationException();
	}

	final long rowCountOf() {
		if (this.statement instanceof QueryStatementImpl) {
			this.adapter.checkAccessible();
			return this.ensureRowCountQuerier().executeLongScalar(
					this.argValueObj);
		}
		throw new UnsupportedOperationException();
	}

	final Object executeScalar() {
		if (this.statement instanceof QueryStatementImpl) {
			this.adapter.checkAccessible();
			try {
				ResultSet rs = this.executor.executeQuery(this.argValueObj);
				if (rs.next()) {
					DataType dt = ((QueryStatementImpl) this.statement).columns
							.get(0).value().getType();
					return dt.detect(ResultSetScalarReader.reader, rs);
				}
			} catch (SQLException e) {
				throw Utils.tryThrowException(e);
			}
			return null;
		}
		throw new UnsupportedOperationException();
	}

	final IStatement getStatement() {
		this.adapter.checkAccessible();
		return this.statement;
	}

	final DynamicObject getArgumentsObj() {
		this.adapter.checkAccessible();
		return this.argValueObj;
	}

	final void setArgumentValues(Object... argValues) {
		this.adapter.checkAccessible();
		setArgumentValues(this.argValueObj, this.statement, argValues);
	}

	final void setArgumentValue(int argumentIndex, Object argValue) {
		this.adapter.checkAccessible();
		this.statement.getArguments().get(argumentIndex)
				.setFieldValueAsObject(this.argValueObj, argValue);
	}

	final void setArgumentValue(ArgumentDefine arg, Object argValue) {
		this.adapter.checkAccessible();
		if (arg.getOwner() != this.statement.getArgumentsDefine()) {
			throw new IllegalArgumentException();
		}
		((StructFieldDefineImpl) arg).setFieldValueAsObject(this.argValueObj,
				argValue);
	}

	@Override
	protected final void unuse() {
		this.executor.unuse();
		if (this.topQuerier != null) {
			this.topQuerier.unuse();
		}
		if (this.limitQuerier != null) {
			this.limitQuerier.unuse();
		}
		if (this.rowCountQuerier != null) {
			this.rowCountQuerier.unuse();
		}
	}

	/**
	 * 参数值对象
	 */
	final DynamicObject argValueObj;

	/**
	 * 语句定义
	 */
	final IStatement statement;

	DBCommandImpl(DBAdapterImpl adapter, IStatement statement,
			DBCommandProxy proxy) {
		super(adapter, proxy);
		this.statement = statement;
		this.argValueObj = new DynamicObject();
		this.executor = new StatementExecutor(this, statement);
		this.initDefaultValues();
	}

	private final void initDefaultValues() {
		final ArrayList<StructFieldDefineImpl> args = this.statement
				.getArguments();
		for (int i = 0, c = args.size(); i < c; i++) {
			StructFieldDefineImpl arg = args.get(i);
			if (arg.defaultValue != null && arg.defaultValue != NullExpr.NULL) {
				try {
					arg.setFieldValue(this.argValueObj,
							(ConstExpr) arg.defaultValue);
				} catch (ClassCastException e) {
					throw new IllegalArgumentException("不支持的默认值类型", e);
				}
			}
		}
	}

	final StatementExecutor executor;

	private TopQuerier topQuerier;

	private final TopQuerier ensureTopQuerier() {
		if (this.topQuerier == null) {
			this.topQuerier = new TopQuerier(this,
					(QueryStatementImpl) this.statement);
		}
		return this.topQuerier;
	}

	private LimitQuerier limitQuerier;

	private final LimitQuerier ensureLimitQuerier() {
		if (this.limitQuerier == null) {
			this.limitQuerier = new LimitQuerier(this,
					(QueryStatementImpl) this.statement);
		}
		return this.limitQuerier;
	}

	private RowCountQuerier rowCountQuerier;

	private final RowCountQuerier ensureRowCountQuerier() {
		if (this.rowCountQuerier == null) {
			this.rowCountQuerier = new RowCountQuerier(this,
					(QueryStatementImpl) this.statement);
		}
		return this.rowCountQuerier;
	}

}
