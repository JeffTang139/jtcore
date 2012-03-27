package org.eclipse.jt.core.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.eclipse.jt.core.exception.DataSourceInitializationException;
import org.eclipse.jt.core.type.DataType;


abstract class DBLang {

	DBLang(String checkConnSql, String postfix) {
		this.checkConnSql = checkConnSql;
		this.postfix = postfix;
		try {
			InputStream is = this.getClass().getResourceAsStream(
					KEYWORDS_FILE + '.' + postfix);
			try {
				readLine(is, this.keywords);
			} finally {
				is.close();
			}
		} catch (IOException e) {
			System.err.println("读取" + postfix + "关键字列表文件错误");
			e.printStackTrace();
		}
	}

	static final String KEYWORDS_FILE = "keywords";

	final String checkConnSql;

	final String postfix;

	final String getCheckConnSql() {
		return this.checkConnSql;
	}

	final String getPostfix() {
		return this.postfix;
	}

	abstract String getDefaultSchema(DataSourceImpl source);

	private final HashSet<String> keywords = new HashSet<String>();

	final boolean filterKeyword(String name) {
		return this.keywords.contains(name.toUpperCase());
	}

	abstract int getMaxTableNameLength();

	abstract int getMaxColumnNameLength();

	abstract int getMaxIndexNameLength();

	abstract int getMaxTablePartCount();

	abstract int getDefaultPartSuggestion();

	abstract TableSynchronizer newSynchronizer(DBAdapterImpl dbAdapter)
			throws SQLException;

	abstract TablePartitioner newPartitioner();

	abstract void formatId(Appendable str, String name);

	abstract void format(Appendable str, DataType type);

	abstract ISqlCommandFactory sqlbuffers();

	abstract void setupPackage(Connection conn, String ds);

	static final String DNA_PACKAGE_SETUP = "dnapkg-setup";

	static final void datasourceInitException(String msg) {
		Throwable th = new DataSourceInitializationException(msg);
		th.printStackTrace();
	}

	static final void datasourceInitException(String msg, Throwable e) {
		Throwable th = new DataSourceInitializationException(msg, e);
		th.printStackTrace();
	}

	static final void execSqls(Connection conn, Class<?> clz, String resource,
			boolean replace) {
		String[] sqls = null;
		try {
			InputStream is = clz.getResourceAsStream(resource);
			if (is == null) {
				datasourceInitException("DNA包的安装配置文件[" + resource + "]不存在");
				return;
			}
			try {
				sqls = readLine(is);
			} finally {
				is.close();
			}
		} catch (IOException e) {
			datasourceInitException("读取DNA包的安装配置文件[" + resource + "]错误", e);
		}
		if (sqls == null || sqls.length == 0) {
			return;
		}
		try {
			Statement st = conn.createStatement();
			try {
				for (String sql : sqls) {
					String ddl = null;
					try {
						InputStream is = clz.getResourceAsStream(sql);
						if (is == null) {
							datasourceInitException("DNA包的安装脚本[" + sql + "]不存在");
							continue;
						}
						try {
							ddl = readString(is);
						} finally {
							is.close();
						}
					} catch (IOException e) {
						datasourceInitException("读取DNA包的安装脚本[" + sql + "]错误", e);
						continue;
					}
					if (ddl == null || ddl.trim().length() == 0) {
						continue;
					}
					if (replace) {
						ddl = ddl.replace("\r", "");
					}
					try {
						st.execute(ddl);
					} catch (SQLException e) {
						datasourceInitException("执行DNA包的安装脚本[" + sql + "]错误", e);
						continue;
					}
				}
			} finally {
				st.close();
			}
		} catch (SQLException e) {
			datasourceInitException("DNA包的安装错误", e);
		}
	}

	static final void readLine(InputStream is, Collection<String> c)
			throws IOException {
		InputStreamReader isr = new InputStreamReader(is);
		try {
			BufferedReader br = new BufferedReader(isr);
			try {
				for (String s = br.readLine(); s != null
						&& s.trim().length() > 0;) {
					c.add(s);
					s = br.readLine();
				}
			} finally {
				br.close();
			}
		} finally {
			isr.close();
		}
	}

	static final String[] readLine(InputStream is) throws IOException {
		ArrayList<String> l = new ArrayList<String>();
		readLine(is, l);
		return l.toArray(new String[l.size()]);
	}

	static final String readString(InputStream is) throws IOException {
		final InputStreamReader isr = new InputStreamReader(is, "UTF8");
		try {
			char[] str = new char[500];
			int strl = 0;
			for (int start = 0;;) {
				int l = str.length;
				int r = isr.read(str, start, l - start);
				if (r >= 0) {
					strl += r;
					if (strl == l) {
						char[] newstr = new char[l * 2];
						System.arraycopy(str, 0, newstr, 0, l);
						str = newstr;
					}
					start = strl;
				} else {
					break;
				}
			}
			return new String(str, 0, strl);
		} finally {
			isr.close();
		}
	}

}
