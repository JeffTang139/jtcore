package org.eclipse.jt.core.impl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import org.eclipse.jt.core.misc.ExceptionCatcher;


/**
 * 数据库连接池连接项
 * 
 * @author Jeff Tang
 * 
 */
final class DBConnectionEntry {
	DBConnectionEntry next;
	final DataSourceImpl dataSource;
	final DBLang lang;
	private final Connection conn;

	final Connection testGetConnection() {
		return this.conn;
	}

	final void checkConnection() throws SQLException {
		SQLException sqlE = null;
		this.enter();
		try {
			Statement s = this.conn.createStatement();
			try {
				s.executeUpdate(this.lang.getCheckConnSql());
			} finally {
				s.close();
			}
		} catch (SQLException e) {
			throw sqlE = e;
		} finally {
			this.leave(sqlE);
		}
	}

	final void release(ExceptionCatcher catcher) {
		try {
			this.conn.setAutoCommit(true);
		} catch (SQLException e) {
			catcher.catchException(e, this);
		}
		this.dataSource.release(this, catcher, this.lastAccessException);
	}

	final Statement createStatement() throws SQLException {
		final Statement st = this.conn.createStatement();
		final int commandTimeoutS = this.dataSource.commandTimeoutS;
		if (commandTimeoutS > 0) {
			st.setQueryTimeout(commandTimeoutS);
		}
		return st;
	}

	final PreparedStatement prepareStatement(String sql) throws SQLException {
		final PreparedStatement pst = this.conn.prepareStatement(sql);
		final int commandTimeoutS = this.dataSource.commandTimeoutS;
		if (commandTimeoutS > 0) {
			pst.setQueryTimeout(commandTimeoutS);
		}
		return pst;
	}

	final CallableStatement prepareCall(String sql) throws SQLException {
		final CallableStatement cstmt = this.conn.prepareCall(sql);
		final int commandTimeoutS = this.dataSource.commandTimeoutS;
		if (commandTimeoutS > 0) {
			cstmt.setQueryTimeout(commandTimeoutS);
		}
		return cstmt;
	}

	final DatabaseMetaData getMetaData() throws SQLException {
		return this.conn.getMetaData();
	}

	/**
	 * 所有等待数据库返回的时间之和
	 */
	private long timeused;

	/**
	 * 所有等待数据库返回的时间之和
	 */
	final long getTimeused() {
		return this.timeused;
	}

	/**
	 * 最后一次访问时间
	 */
	private long lastAccess;

	/**
	 * 最后一次访问时间
	 */
	final long getLastAccess() {
		return this.lastAccess;
	}

	final void enter() {
		this.lastAccess = System.currentTimeMillis();
	}

	final void leave(SQLException e) {
		long now = System.currentTimeMillis();
		this.timeused = now - this.lastAccess;
		this.lastAccess = now;
		this.lastAccessException = e != null;
	}

	final String getCatalog() {
		return this.catalog;
	}

	private String catalog;
	private boolean isInTrans;
	private boolean isImpliedTrans;
	private boolean lastAccessException;

	final void attach(String catalog) throws SQLException {
		this.timeused = 0;
		if (catalog != null && catalog.length() > 0
				&& !catalog.equals(this.catalog)) {
			SQLException sqlE = null;
			this.enter();
			try {
				this.conn.setCatalog(catalog);
			} catch (SQLException e) {
				throw sqlE = e;
			} finally {
				this.leave(sqlE);
			}
			this.catalog = catalog;
		}
	}

	final void updateTrans(boolean isNeedTrans) throws SQLException {
		if (isNeedTrans) {
			if (!this.isInTrans) {
				if (!this.isImpliedTrans) {
					SQLException sqlE = null;
					this.enter();
					try {
						this.conn.setAutoCommit(false);
					} catch (SQLException e) {
						throw sqlE = e;
					} finally {
						this.leave(sqlE);
					}
					this.isImpliedTrans = true;
				}
				this.isInTrans = true;
			}
		} else if (this.isImpliedTrans) {
			SQLException sqlE = null;
			this.enter();
			try {
				this.conn.setAutoCommit(true);
			} catch (SQLException e) {
				throw sqlE = e;
			} finally {
				this.leave(sqlE);
			}
			this.isInTrans = false;
			this.isImpliedTrans = false;
		}
	}

	final boolean isInTrans() {
		return this.isInTrans;
	}

	final void resolveTranse(boolean commit, ExceptionCatcher catcher) {
		if (this.isInTrans) {
			SQLException th = null;
			this.enter();
			try {
				if (!this.conn.getAutoCommit()) {
					if (commit) {
						this.conn.commit();
					} else {
						this.conn.rollback();
					}
				}
			} catch (SQLException e) {
				th = e;
			} finally {
				this.leave(th);
			}
			if (th != null) {
				catcher.catchException(th, this);
			}
			this.isInTrans = false;
		}
	}

	final void close(ExceptionCatcher catcher) {
		try {
			this.conn.close();
		} catch (Throwable e) {
			catcher.catchException(e, this);
		}
	}

	DBConnectionEntry(DataSourceImpl dataSource) throws Throwable {
		if (dataSource == null) {
			throw new NullPointerException();
		}
		this.dataSource = dataSource;
		this.lang = dataSource.getLang();
		this.conn = dataSource.connect();
		try {
			this.lastAccess = System.currentTimeMillis();
			this.conn
					.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
			this.catalog = this.conn.getCatalog();
		} catch (Throwable e) {
			this.conn.close();
			throw e;
		}
	}

}
