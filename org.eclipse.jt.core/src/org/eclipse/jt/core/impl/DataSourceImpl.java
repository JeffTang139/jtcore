package org.eclipse.jt.core.impl;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.eclipse.jt.core.misc.ExceptionCatcher;
import org.eclipse.jt.core.misc.Obfuscater;
import org.eclipse.jt.core.misc.SXElement;
import org.eclipse.jt.core.misc.SXMergeHelper;


/**
 * ���ӳ�
 * 
 * @author Jeff Tang
 * 
 */
final class DataSourceImpl extends NamedDefineImpl {

	final String dbLocation;
	private DataSource dbSource;
	/**
	 * ���ӳ����������
	 */
	private int maxConnections = DEFAULT_MAX_CONNECTIONS;
	private int minConnections = DEFAULT_MIX_CONNECTIONS;
	static final int DEFAULT_MAX_CONNECTIONS = 20;
	static final int DEFAULT_MIX_CONNECTIONS = 5;
	/**
	 * ���ӳص�ǰ�������
	 */
	private int connections;
	/**
	 * ����ʹ��
	 */
	private int inuses;
	/**
	 * ������������
	 */
	private int waitings;
	/**
	 * ���ӳ�ʱʱ��
	 */
	private int loginTimeoutS = DEFAULT_LOGIN_TIMEOUT;
	static final int DEFAULT_LOGIN_TIMEOUT = 20;// 20��
	/**
	 * ���ʱʱ��
	 */
	int commandTimeoutS = DEFAULT_COMMAND_TIMEOUT;
	static final int DEFAULT_COMMAND_TIMEOUT = 0;// ��, ������
	/**
	 * �������ӵı�����
	 */
	private int idleLifeMS = DEFAULT_IDLE_LIFE;
	static final int DEFAULT_IDLE_LIFE = 60 * 2 * 1000;// 2����
	private final int trustLifeMS = DEFAULT_IDLE_LIFE;
	static final int DEFAULT_TRUST_LIFE = 60 * 1 * 1000;// 1����
	/**
	 * �û�
	 */
	private String user;
	/**
	 * ����
	 */
	private String password;
	/**
	 * Ĭ��Ŀ¼
	 */
	private String defaultCatalog;
	/**
	 * ���Ӳ�������
	 */
	final Properties props = new Properties();
	/**
	 * �ܹ�����ʱ��
	 */
	private long timeused;

	/**
	 * �����ܹ�����ʱ��
	 */
	final long getTimeused() {
		return this.timeused;
	}

	/**
	 * ��������
	 */
	private DBConnectionEntry idles;

	/**
	 * ����������Ӧ���׳��쳣
	 */
	final void adjust(ExceptionCatcher catcher) {
		if (this.maxConnections == 0) {
			return;
		}
		// ��¼���������
		for (DBConnectionEntry needCheck = null;;) {
			final long now = System.currentTimeMillis();
			final long lastValidTime = now - this.idleLifeMS;
			final long lastTrustTime = now - this.trustLifeMS;
			DBConnectionEntry needClose = null;
			synchronizedThis: synchronized (this) {
				if (this.maxConnections == 0) {
					if (needCheck != null) {
						// ����ͬ�����ͷ�
						needClose = needCheck;
						needCheck = null;
						break synchronizedThis;
					}
					return;
				}
				DBConnectionEntry ce = this.idles;
				DBConnectionEntry last;
				if (needCheck != null) {
					// ����һ��ѭ�������ϵ����ӷŻؿ����ж�
					needCheck.next = ce;
					this.idles = needCheck;
					last = needCheck;
					needCheck = null;
					this.connections++;
				} else {
					last = null;
				}
				while (ce != null) {
					final DBConnectionEntry next = ce.next;
					final long lastAccess = ce.getLastAccess();
					if (lastAccess < lastValidTime
							&& this.connections > this.minConnections) {
						// ժ����������
						if (last == null) {
							this.idles = next;
						} else {
							last.next = next;
						}
						ce.next = needClose;
						needClose = ce;
						this.connections--;
					} else if (lastAccess < lastTrustTime) {
						// ժ����Ҫ��֤������
						if (last == null) {
							this.idles = next;
						} else {
							last.next = next;
						}
						needCheck = ce;
						this.connections--;
						// һ�δ���һ��
						break synchronizedThis;
					} else {
						last = ce;
					}
					ce = next;
				}
				if (needClose == null) {
					// ������С���������ӣ����õ�Ƶ�ȷǳ��٣����Բ��ÿ������ٽ����ⲿ����
					while (this.connections < this.minConnections) {
						try {
							ce = new DBConnectionEntry(this);
						} catch (Throwable e) {
							catcher.catchException(e, this);
							return;
						}
						ce.next = this.idles;
						this.idles = ce;
						this.connections++;
					}
				}
			}
			// �رն��������
			while (needClose != null) {
				needClose.close(catcher);
				needClose = needClose.next;
			}
			if (needCheck != null) {
				try {
					needCheck.checkConnection();
				} catch (Throwable e) {
					needCheck.close(catcher);
					needCheck = null;
				}
			} else {
				break;
			}
		}
	}

	synchronized final void doDispose(ExceptionCatcher catcher) {
		this.maxConnections = 0;
		if (this.connections > 0) {
			DBConnectionEntry ce = this.idles;
			if (ce != null) {
				do {
					ce.close(catcher);
					this.connections--;
					ce = ce.next;
				} while (ce != null);
				this.idles = null;
			}
			if (this.waitings > 0) {
				this.notifyAll();
			}
		}
	}

	private void attach(DBConnectionEntry ce, DataSourceRef info)
			throws SQLException {
		final String catalog = info.catalog != null
				&& info.catalog.length() > 0 ? info.catalog
				: this.defaultCatalog;
		ce.attach(catalog);
	}

	final DBConnectionEntry alloc(DataSourceRef info) throws SQLException {
		DBConnectionEntry ce;
		do {
			synchronized (this) {
				for (;;) {
					if (this.maxConnections == 0) {
						throw new IllegalStateException("���ӳ��Ѿ��ر�");
					}
					ce = this.idles;
					if (ce != null) {
						this.idles = ce.next;
						ce.next = null;
						break;
					} else if (this.connections < this.maxConnections) {
						// �뿪�ٽ���ȥ����������ʱ��ռ���ٽ���
						this.connections++;
						break;
					} else {
						this.waitings++;
						try {
							this.wait();
						} catch (InterruptedException e) {
							throw Utils.tryThrowException(e);
						} finally {
							this.waitings--;
						}
					}
				}
				this.inuses++;
			}
			if (ce == null) {
				try {
					// �뿪�ٽ�������������ʱ��ռ���ٽ���
					ce = new DBConnectionEntry(this);
				} catch (Throwable e) {
					synchronized (this) {
						this.connections--;
						this.inuses--;
					}
					throw Utils.tryThrowException(e);
				}
			} else if (System.currentTimeMillis() - ce.getLastAccess() > this.trustLifeMS) {
				// �������Ч
				try {
					ce.checkConnection();
				} catch (Throwable e) {
					this.release(ce, this.manager.application.catcher, true);
					this.manager.application.catcher.catchException(e, ce);
					continue;
				}
			}
		} while (false);
		this.attach(ce, info);
		return ce;
	}

	final void release(DBConnectionEntry used, ExceptionCatcher catcher,
			boolean closeUsed) {
		try {
			used.resolveTranse(false, catcher);
		} finally {
			synchronized (this) {
				this.inuses--;
				this.timeused += used.getTimeused();
				if (closeUsed || this.connections > this.maxConnections) {
					this.connections--;
				} else {
					used.next = this.idles;
					this.idles = used;
					if (this.waitings > 0) {
						this.notify();
					}
					return;
				}
			}
			used.close(catcher);
		}
	}

	private DBLang lang;
	private Driver driver;

	private final Connection tryJdbcUrl() throws SQLException {
		JdbcDriver drv = JdbcDrivers.find(this.dbLocation);
		if (drv != null) {
			this.driver = drv.getDriver();
			drv.initDefualtProperties(this.props);
			final Connection conn = this.driver.connect(this.dbLocation,
					this.props);
			if (conn == null) {
				throw this.NotSupportedURLException();
			}
			this.lang = drv.getAdjustedLang(conn);
			return conn;
		}
		return null;
	}

	private final Connection tryJndiDatasource() throws SQLException {
		try {
			final InitialContext ctx = new InitialContext();
			Object o = ctx.lookup(this.dbLocation);
			if (o instanceof DataSource) {
				this.dbSource = (DataSource) o;
			} else if (o instanceof Connection) {
				try {
					((Connection) o).close();
				} catch (Throwable e) {
				}
			} else if (o instanceof Closeable) {
				try {
					((Closeable) o).close();
				} catch (Throwable e) {
				}
			}
		} catch (NamingException e) {
			throw new SQLException("�޷���ʼInitalContext");
		}
		Connection conn;
		if (this.dbSource != null) {
			conn = this.dbSource.getConnection();
			if (conn == null) {
				throw this.NotSupportedURLException();
			}
			JdbcDriver drv = JdbcDrivers.find(conn);
			if (drv == null) {
				throw this.NotSupportedURLException();
			}
			this.driver = drv.getDriver();
			this.lang = drv.getAdjustedLang(conn);
			return conn;
		}
		return null;
	}

	private final void loadLangAndDriver() throws SQLException {
		Connection conn = null;
		try {
			conn = this.tryJdbcUrl();
			if (conn == null) {
				conn = this.tryJndiDatasource();
			}
			if (conn == null) {
				throw this.NotSupportedURLException();
			}
			this.info(conn);
			this.lang.setupPackage(conn, this.name);
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
	}

	private final void info(Connection conn) throws SQLException {
		DatabaseMetaData dbmd = conn.getMetaData();
		ResolveHelper.logStartInfo("��������Դ[" + this.name + "],���ݿ�["
				+ dbmd.getDatabaseProductName() + ", "
				+ dbmd.getDatabaseProductVersion() + "],����["
				+ dbmd.getDriverName() + ", " + dbmd.getDriverVersion() + "]");
	}

	final String getUser() {
		return this.user;
	}

	final Driver getDriver() throws SQLException {
		if (this.driver == null) {
			this.loadLangAndDriver();
		}
		return this.driver;
	}

	private final SQLException NotSupportedURLException() {
		return new SQLException("��֧�ֵ����ݿ�����URL:" + this.dbLocation);
	}

	final DBLang getLang() throws SQLException {
		if (this.lang == null) {
			this.loadLangAndDriver();
		}
		return this.lang;
	}

	final Connection connect() throws SQLException {
		final Connection conn;
		if (this.dbSource != null) {
			conn = this.dbSource.getConnection();
		} else {
			conn = this.getDriver().connect(this.dbLocation, this.props);
		}
		if (conn == null) {
			throw this.NotSupportedURLException();
		}
		return conn;
	}

	// /////////// xml //////////////
	final static String xml_element_datasource = "datasource";
	static final String xml_attr_location = "location";
	static final String xml_attr_max_connections = "max-connections";
	static final String xml_attr_min_connections = "min-connections";
	static final String xml_attr_idle_life = "idle-life-ms";
	static final String xml_attr_login_timeout = "login-timeout-s";
	static final String xml_attr_command_timeout = "command-timeout-s";
	static final String xml_attr_user = "user";
	static final String xml_attr_password = "password";
	final static String xml_attr_catalog = "catalog";

	@Override
	public final String getXMLTagName() {
		return xml_element_datasource;
	}

	@Override
	public final void render(SXElement element) {
		super.render(element);
		element.setAttribute(xml_attr_location, this.dbLocation);
		element.setInt(xml_attr_max_connections, this.maxConnections);
		element.setInt(xml_attr_min_connections, this.minConnections);
		element.setInt(xml_attr_idle_life, this.idleLifeMS);
		element.setInt(xml_attr_login_timeout, this.loginTimeoutS);
		element.setInt(xml_attr_command_timeout, this.commandTimeoutS);
		element.setAttribute(xml_attr_user, this.user);
		if (this.password != null) {
			element.setAttribute(xml_attr_password,
					Obfuscater.obfuscate(this.password));
		}
	}

	final static String jdbc_prop_logintimeout = "loginTimeout";

	@Override
	final void merge(SXElement element, SXMergeHelper helper) {
		super.merge(element, helper);
		this.maxConnections = element.getInt(xml_attr_max_connections,
				this.maxConnections);
		this.minConnections = element.getInt(xml_attr_min_connections,
				this.minConnections);
		this.idleLifeMS = element.getInt(xml_attr_idle_life, this.idleLifeMS);
		this.loginTimeoutS = element.getInt(xml_attr_login_timeout,
				this.loginTimeoutS);
		this.commandTimeoutS = element.getInt(xml_attr_command_timeout,
				this.commandTimeoutS);
		this.user = element.getAttribute(xml_attr_user, this.user);
		String pw = element.getAttribute(xml_attr_password, null);
		if (pw != null) {
			this.password = Obfuscater.unobfuscate(pw);
		}
		this.defaultCatalog = element.getAttribute(xml_attr_catalog,
				this.defaultCatalog);
		this.props.clear();
		this.props.put(xml_attr_user, this.user);
		this.props.put(xml_attr_password, this.password);
		if (this.loginTimeoutS > 0) {
			this.props.put(jdbc_prop_logintimeout,
					Integer.toString(this.loginTimeoutS));
		} else {
			this.props.remove(jdbc_prop_logintimeout);
		}
	}

	final DataSourceManager manager;

	DataSourceImpl(DataSourceManager manager, SXElement element)
			throws SQLException {
		super(element.getString(xml_attr_name));
		this.manager = manager;
		this.dbLocation = element.getString(xml_attr_location);
		this.merge(element, null);
		this.loadLangAndDriver();// TODO δ����Ҫ�������ݿ���ʱ���Ӳ���
	}

}
