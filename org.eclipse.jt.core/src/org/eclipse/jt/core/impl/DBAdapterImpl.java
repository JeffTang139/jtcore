package org.eclipse.jt.core.impl;

import java.lang.ref.ReferenceQueue;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.eclipse.jt.core.da.DBAdapter;
import org.eclipse.jt.core.da.RecordIterateAction;
import org.eclipse.jt.core.def.obja.DynamicObject;
import org.eclipse.jt.core.def.query.ModifyStatementDefine;
import org.eclipse.jt.core.def.query.QueryStatementDefine;
import org.eclipse.jt.core.def.query.StoredProcedureDefine;
import org.eclipse.jt.core.def.table.HierarchyDefine;
import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.type.GUID;


/**
 * 数据库适配器
 * 
 * <p>
 * 提供所有的数据库访问的接口,可以认为是JDBC的Connection的封装.
 * 
 * @author Jeff Tang
 * 
 */
final class DBAdapterImpl extends ReferenceQueue<PsHolderProxy> {

	private ContextImpl<?, ?, ?> context;

	final ContextImpl<?, ?, ?> getContext() {
		return this.context;
	}

	DBAdapterImpl nextInContext;

	final Connection testGetConnection() throws SQLException {
		this.ensureConn();
		return this.connection.testGetConnection();
	}

	final void checkAccessible() {
		if (this.isClosed) {
			throw new IllegalStateException("数据库适配器已经关闭");
		}
		this.context.checkValid();
	}

	public static final DBAdapterImpl toDBAdapter(DBAdapter anInterface)
			throws SQLException {
		if (anInterface instanceof ContextImpl<?, ?, ?>) {
			return ((ContextImpl<?, ?, ?>) anInterface).getDBAdapter();
		} else if (anInterface instanceof SituationImpl) {
			return ((SituationImpl) anInterface).getDBAdapter();
		} else if (anInterface == null) {
			return null;
		} else {
			throw new IllegalArgumentException("无效的接口");
		}
	}

	final int executeUpdate(ModifyStatementDefine statement,
			Object... argValues) {
		this.checkNotClosed();
		DBCommandProxy proxy = new DBCommandProxy(this, (IStatement) statement);
		try {
			proxy.setArgumentValues(argValues);
			return proxy.executeUpdate();
		} finally {
			proxy.unuse();
		}
	}

	final void executeUpdate(StoredProcedureDefine procedure,
			Object... argValues) {
		this.checkNotClosed();
		DBCommandProxy proxy = new DBCommandProxy(this, (IStatement) procedure);
		try {
			proxy.setArgumentValues(argValues);
			proxy.executeUpdate();
		} finally {
			proxy.unuse();
		}
	}

	final RecordSetImpl openQuery(QueryStatementDefine statement,
			Object... argValues) {
		this.checkNotClosed();
		DBCommandProxy proxy = new DBCommandProxy(this,
				(QueryStatementImpl) statement);
		try {
			proxy.setArgumentValues(argValues);
			return proxy.executeQuery();
		} finally {
			proxy.unuse();
		}
	}

	final RecordSetImpl openQueryTop(QueryStatementDefine statement, long top,
			Object... argValues) {
		this.checkNotClosed();
		DBCommandProxy proxy = new DBCommandProxy(this,
				(QueryStatementImpl) statement);
		try {
			proxy.setArgumentValues(argValues);
			return proxy.executeQueryTop(top);
		} finally {
			proxy.unuse();
		}
	}

	final RecordSetImpl openQueryLimit(QueryStatementDefine statement,
			long offset, long rowCount, Object... argValues) {
		if (offset < 0 || rowCount <= 0) {
			throw new IllegalArgumentException();
		}
		if (offset == 0) {
			return this.openQueryTop(statement, rowCount, argValues);
		}
		this.checkNotClosed();
		DBCommandProxy proxy = new DBCommandProxy(this,
				(QueryStatementImpl) statement);
		try {
			proxy.setArgumentValues(argValues);
			return proxy.executeQueryLimit(offset, rowCount);
		} finally {
			proxy.unuse();
		}
	}

	final void iterateQuery(QueryStatementDefine query,
			RecordIterateAction action, Object... argValues) {
		this.checkNotClosed();
		DBCommandProxy proxy = new DBCommandProxy(this,
				(QueryStatementImpl) query);
		try {
			proxy.setArgumentValues(argValues);
			proxy.iterateQuery(action);
		} finally {
			proxy.unuse();
		}
	}

	final void iterateQueryTop(QueryStatementDefine query, long rowCount,
			RecordIterateAction action, Object... argValues) {
		this.checkNotClosed();
		DBCommandProxy proxy = new DBCommandProxy(this,
				(QueryStatementImpl) query);
		try {
			proxy.setArgumentValues(argValues);
			proxy.iterateQueryTop(rowCount, action);
		} finally {
			proxy.unuse();
		}
	}

	final void iterateQueryLimit(QueryStatementDefine query, long offset,
			long rowCount, RecordIterateAction action, Object... argValues) {
		if (offset < 0 || rowCount <= 0) {
			throw new IllegalArgumentException();
		}
		if (offset == 0) {
			this.iterateQueryTop(query, rowCount, action, argValues);
			return;
		}
		this.checkNotClosed();
		DBCommandProxy proxy = new DBCommandProxy(this,
				(QueryStatementImpl) query);
		try {
			proxy.setArgumentValues(argValues);
			proxy.iterateQueryLimit(action, offset, rowCount);
		} finally {
			proxy.unuse();
		}
	}

	final Object executeScalar(QueryStatementBase statement,
			Object... argValues) {
		this.checkNotClosed();
		DBCommandProxy proxy = new DBCommandProxy(this, statement);
		try {
			proxy.setArgumentValues(argValues);
			return proxy.executeScalar();
		} finally {
			proxy.unuse();
		}
	}

	final long rowCountOf(QueryStatementBase statement, Object[] argValues) {
		this.checkNotClosed();
		DBCommandProxy proxy = new DBCommandProxy(this, statement);
		try {
			proxy.setArgumentValues(argValues);
			return proxy.rowCountOf();
		} finally {
			proxy.unuse();
		}
	}

	final long rowCountOf(QueryStatementBase statement,
			DynamicObject argValueObj) {
		this.checkNotClosed();
		CommonExecutor executor = new CommonExecutor(this,
				statement.getQueryRowCountSql(this));
		try {
			return executor.executeLongScalar(argValueObj);
		} finally {
			executor.unuse();
		}
	}

	final DBCommandProxy prepareStatement(IStatement statement) {
		this.checkNotClosed();
		return new DBCommandProxy(this, statement);
	}

	public final <TEntity> ORMAccessorProxy<TEntity> newORMAccessor(
			MappingQueryStatementImpl mStatement) {
		this.checkNotClosed();
		if (mStatement == null) {
			throw new NullArgumentException("mappingQuery");
		}
		return new ORMAccessorProxy<TEntity>(this, mStatement);
	}

	final void hierarchyMoveTo(HierarchyDefine hierarchy, GUID parent,
			GUID child) {
		if (parent == null) {
			throw new NullPointerException("级次父节点GUID为空");
		}
		if (child == null) {
			throw new NullPointerException("级次子节点GUID为空");
		}
		this.ensureHierarchyUpater(hierarchy);
		try {
			this.hierarchyUpdater.executeUpdate(child, parent);
		} finally {
			this.hierarchyUpdater.unuse();
		}
	}

	final void hierarchyMoveTo(HierarchyDefine hierarchy, GUID parent,
			GUID child, GUID... others) {
		if (parent == null) {
			throw new NullPointerException("级次父节点GUID为空");
		}
		if (child == null) {
			throw new NullPointerException("级次子节点GUID为空");
		}
		this.ensureHierarchyUpater(hierarchy);
		if (others != null && others.length > 0) {
			for (GUID other : others) {
				if (other != null) {
					this.hierarchyUpdater.executeUpdate(other, parent);
				}
			}
		}
		this.hierarchyUpdater.unuse();
	}

	final void hierarchyMoveTo(HierarchyDefine hierarchy, GUID parent,
			List<GUID> children) {
		if (parent == null) {
			throw new NullPointerException("级次父节点GUID为空");
		}
		if (children == null || children.size() == 0) {
			throw new NullPointerException("级次子节点GUID为空");
		}
		this.ensureHierarchyUpater(hierarchy);
		for (GUID child : children) {
			if (child != null) {
				this.hierarchyUpdater.executeUpdate(child, parent);
			}
		}
		this.hierarchyUpdater.unuse();
	}

	final void hierarchyMoveTo(HierarchyDefine hierarchy, GUID parent,
			Iterable<GUID> children) {
		if (parent == null) {
			throw new NullArgumentException("级次父节点GUID为空");
		}
		if (children == null) {
			throw new NullArgumentException("级次子节点GUID为空");
		}
		this.ensureHierarchyUpater(hierarchy);
		for (GUID child : children) {
			if (child != null) {
				this.hierarchyUpdater.executeUpdate(child, parent);
			}
		}
		this.hierarchyUpdater.unuse();
	}

	private HierarchyUpdater hierarchyUpdater;

	private final void ensureHierarchyUpater(HierarchyDefine hierarchy) {
		if (this.hierarchyUpdater == null) {
			this.hierarchyUpdater = new HierarchyUpdater(this,
					(HierarchyDefineImpl) hierarchy);
		} else if (this.hierarchyUpdater.hierarchy != hierarchy) {
			this.hierarchyUpdater.unuse();
			this.hierarchyUpdater = new HierarchyUpdater(this,
					(HierarchyDefineImpl) hierarchy);
		}
	}

	private static final class HierarchyUpdater extends
			PsExecutor<HierarchyMoveSql> {

		final HierarchyDefineImpl hierarchy;

		HierarchyUpdater(DBAdapterImpl adapter, HierarchyDefineImpl hierarchy) {
			super(adapter, hierarchy.getHierarchyMoveSql(adapter.lang));
			this.hierarchy = hierarchy;
		}

		final void executeUpdate(GUID from, GUID to) {
			try {
				super.use(true);
				this.sql.from.setBytes(this.ps, from.toBytes());
				this.sql.to.setBytes(this.ps, to.toBytes());
				this.adapter.jdbcUpdate(this);
			} catch (SQLException e) {
				Utils.tryThrowException(e);
			}
		}

	}

	final void checkNotClosed() {
		if (this.isClosed) {
			throw Utils.tryThrowException(new SQLException("数据库连接适配器已经关闭"));
		}
	}

	final void unuse() {
		if (this.synchronizer != null) {
			this.synchronizer.unuse();
		}
		if (this.hierarchyUpdater != null) {
			this.hierarchyUpdater.unuse();
		}
		if (this.connection != null) {
			DBConnectionEntry ce = this.connection;
			this.connection = null;
			this.unuseOldAccessor(0);
			ce.release(this.context.catcher);
		}
	}

	final void close() {
		if (!this.isClosed) {
			this.isClosed = true;
			this.unuse();
			this.context = null;
		}
	}

	private final void startStatement(Statement statement) {
		this.connection.enter();
		final ContextImpl<?, ?, ?> context = this.context;
		if (context != null) {
			context.processingStatement = statement;
		}
	}

	private final void finishStatement(SQLException th) {
		final ContextImpl<?, ?, ?> context = this.context;
		if (context != null) {
			context.processingStatement = null;
		}
		this.connection.leave(th);
	}

	private static final void debugSql(String sql) {
		System.out.println(sql);
	}

	private static final void debugSql(String sql, DBConnectionEntry conn) {
		System.out.println("[ " + conn.getTimeused() + "ms ] " + sql);
	}

	final boolean executeDdl(Statement statement, String sql)
			throws SQLException {
		SQLException th = null;
		this.startStatement(statement);
		try {
			return statement.execute(sql);
		} catch (SQLException e) {
			debugSql(sql);
			throw th = e;
		} finally {
			this.finishStatement(th);
			if (th == null) {
				if (SystemVariables.DEBUG_SQL_DURATION) {
					debugSql(sql, this.connection);
				} else if (SystemVariables.DEBUG_SQL_DDL) {
					debugSql(sql);
				}
			}
		}
	}

	final int jdbcUpdate(PsExecutor<?> pe) throws SQLException {
		SQLException th = null;
		PreparedStatement ps = pe.ps;
		this.startStatement(ps);
		try {
			return ps.executeUpdate();
		} catch (SQLException e) {
			debugSql(pe.sql.sqlstr());
			throw th = e;
		} finally {
			this.finishStatement(th);
			ps.clearParameters();
			if (th == null) {
				if (SystemVariables.DEBUG_SQL_DURATION) {
					debugSql(pe.sql.sqlstr(), this.connection);
				} else if (SystemVariables.DEBUG_SQL_DML) {
					debugSql(pe.sql.sqlstr());
				}
			}
		}
	}

	final ResultSet jdbcQuery(PsExecutor<?> pe) throws SQLException {
		SQLException th = null;
		PreparedStatement ps = pe.ps;
		this.startStatement(ps);
		try {
			return ps.executeQuery();
		} catch (SQLException e) {
			debugSql(pe.sql.sqlstr());
			throw th = e;
		} finally {
			this.finishStatement(th);
			ps.clearParameters();
			if (th == null) {
				if (SystemVariables.DEBUG_SQL_DURATION) {
					debugSql(pe.sql.sqlstr(), this.connection);
				} else if (SystemVariables.DEBUG_SQL_DML) {
					debugSql(pe.sql.sqlstr());
				}
			}
		}
	}

	@Deprecated
	final int jdbcUpdate(PreparedStatement statement) throws SQLException {
		SQLException th = null;
		this.startStatement(statement);
		try {
			return statement.executeUpdate();
		} catch (SQLException e) {
			throw th = e;
		} finally {
			this.finishStatement(th);
			statement.clearParameters();
		}
	}

	private TableSynchronizer synchronizer;

	final void syncTable(TableDefineImpl table) throws SQLException {
		if (table == null) {
			throw new NullPointerException();
		}
		this.ensureConn();
		if (this.synchronizer == null) {
			this.synchronizer = this.lang.newSynchronizer(this);
		}
		try {
			this.synchronizer.sync(table);
		} finally {
			this.tryUnuse();
		}
	}

	final void dropTable(TableDefineImpl table) throws SQLException {
		if (table == null) {
			throw new NullPointerException();
		}
		this.ensureConn();
		if (this.synchronizer == null) {
			this.synchronizer = this.lang.newSynchronizer(this);
		}
		try {
			this.synchronizer.drop(table);
		} finally {
			this.tryUnuse();
		}
	}

	final void postTable(TableDefineImpl post, TableDefineImpl runtime)
			throws SQLException {
		if (post == null) {
			throw new NullPointerException();
		}
		this.ensureConn();
		if (this.synchronizer == null) {
			this.synchronizer = this.lang.newSynchronizer(this);
		}
		try {
			this.synchronizer.post(post, runtime);
		} finally {
			this.tryUnuse();
		}
	}

	final void tryUnuse() {
		if (this.statements == 0 && this.connection != null
				&& !this.connection.isInTrans()) {
			this.unuse();
		}
	}

	/**
	 * 返回是否在该方法中第一次确定了连接
	 */
	private final boolean ensureConn() throws SQLException {
		if (this.isClosed) {
			this.checkNotClosed();
		}
		if (this.connection == null) {
			this.connection = this.dataSourceRef.allocDBConnectionEntry();
			return true;
		} else {
			return false;
		}
	}

	final void resolveTranse(boolean commit) {
		if (this.connection != null) {
			this.connection.resolveTranse(commit, this.context.catcher);
			this.unuse();
		}
	}

	final Statement createStatement() throws SQLException {
		Statement st;
		this.ensureConn();
		try {
			st = this.connection.createStatement();
		} catch (Throwable e) {
			if (this.statements == 0 && !this.connection.isInTrans()) {
				this.unuse();
			}
			throw Utils.tryThrowException(e);
		}
		this.statements++;
		return st;
	}

	final PreparedStatement prepareStatement(String sql) throws SQLException {
		PreparedStatement pst;
		this.ensureConn();
		try {
			pst = this.connection.prepareStatement(sql);
		} catch (Throwable e) {
			this.tryUnuse();
			throw Utils.tryThrowException(e);
		}
		this.statements++;
		return pst;
	}

	final CallableStatement prepareCall(String sql) throws SQLException {
		CallableStatement cstmt;
		this.ensureConn();
		try {
			cstmt = this.connection.prepareCall(sql);
		} catch (Throwable e) {
			this.tryUnuse();
			throw Utils.tryThrowException(e);
		}
		this.statements++;
		return cstmt;
	}

	final void updateTrans(boolean forUpdate) throws SQLException {
		if (this.connection == null) {
			this.checkNotClosed();
			throw new IllegalStateException();
		}
		this.context.occorAt.site.state.checkDBAccess(forUpdate);
		if (forUpdate) {
			this.connection.updateTrans(true);
		}
	}

	final void freeStatement(Statement statement) {
		try {
			statement.close();
		} catch (Throwable e) {
			this.context.catcher.catchException(e, this);
		} finally {
			// 命令被动关闭时this.connection会为null
			this.statements--;
			this.tryUnuse();
		}
	}

	final DatabaseMetaData getMetaData() throws SQLException {
		this.ensureConn();
		return this.connection.getMetaData();
	}

	final String getCatalog() {
		return this.connection.getCatalog();
	}

	final String getDefaultSchema() {
		return this.lang.getDefaultSchema(this.dataSourceRef.dataSource);
	}

	// ***************** 游标管理 *****************

	/**
	 * 申请了数据库资源的对象,双向不成环链表相互强引用,不会被gc回收!
	 */
	private PsHolder<?> activedHead;

	/**
	 * 释放弱引用
	 */
	private void releaseWeaked() {
		PsHolder<?> head = this.activedHead;
		for (;;) {
			// 循环引用列表,弱引用对象不为空,则表示Accessor对应的Proxy已经不存在任何强引用
			// 意味着Accessor可以被释放了!
			PsHolder<?> weaked = (PsHolder<?>) this.poll();
			if (weaked != null && (weaked.prevHolder != null || weaked == head)) {
				head = weaked.removeFromChain(head);
				weaked.unuse();
			} else {
				break;
			}
		}
		this.activedHead = head;
	}

	/**
	 * 激活目标对象,置于链表头
	 * 
	 * @param accessor
	 *            申请了数据库资源的对象
	 */
	final void active(PsHolder<?> accessor) {
		this.releaseWeaked();
		accessor.nextHolder = this.activedHead;
		if (this.activedHead != null) {
			this.activedHead.prevHolder = accessor;
		}
		this.activedHead = accessor;
	}

	/**
	 * 释放accessor的资源,并从链表中移除
	 * 
	 * @param accessor
	 */
	final void disactive(PsHolder<?> accessor) {
		this.activedHead = accessor.removeFromChain(this.activedHead);
	}

	/**
	 * 释放已经过期的accessor
	 * 
	 * @param maxdepth
	 *            context的最大调用深度
	 */
	final void unuseOldAccessor(int maxdepth) {
		PsHolder<?> head = this.activedHead;
		while (head != null && head.depth >= maxdepth) {
			// depth更大的一定在链表前部!!
			final PsHolder<?> one = head;
			head = one.removeFromChain(head);
			one.unuse();
		}
		this.activedHead = head;
		this.releaseWeaked();
	}

	final DBLang lang;
	final DataSourceRef dataSourceRef;
	private DBConnectionEntry connection;
	private boolean isClosed;
	private int statements;

	DBAdapterImpl(ContextImpl<?, ?, ?> context, DataSourceRef dataSourceRef,
			DBAdapterImpl nextInContext) throws SQLException {
		if (dataSourceRef == null || context == null) {
			throw new NullPointerException();
		}
		this.context = context;
		this.dataSourceRef = dataSourceRef;
		this.lang = dataSourceRef.getLang();
		this.nextInContext = nextInContext != null ? nextInContext : this;
	}
}
