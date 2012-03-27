package org.eclipse.jt.core.impl;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.eclipse.jt.core.type.Convert;
import org.eclipse.jt.core.type.DataType;


final class DB2Lang extends DBLang {

	static final DB2Lang lang = new DB2Lang();

	private DB2Lang() {
		super(CHECK_CONN, "db2");
	}

	static final String CHECK_CONN = "select 1 from sysibm.dual";

	@Override
	final String getDefaultSchema(DataSourceImpl source) {
		return source.getUser().toUpperCase();
	}

	@Override
	final int getMaxTableNameLength() {
		return 128;
	}

	@Override
	final int getMaxColumnNameLength() {
		return 128;
	}

	@Override
	final int getMaxIndexNameLength() {
		return 128;
	}

	@Override
	final int getMaxTablePartCount() {
		// HCL Auto-generated method stub
		return 0;
	}

	@Override
	final int getDefaultPartSuggestion() {
		// HCL Auto-generated method stub
		return 0;
	}

	@Override
	final TableSynchronizer newSynchronizer(DBAdapterImpl dbAdapter)
			throws SQLException {
		return new DB2TableSynchronizer(dbAdapter, this);
	}

	@Override
	final TablePartitioner newPartitioner() {
		return null;
	}

	@Override
	final void formatId(Appendable str, String name) {
		try {
			str.append('"').append(name).append('"');
		} catch (IOException e) {
			throw Utils.tryThrowException(e);
		}
	}

	@Override
	final void format(Appendable str, DataType type) {
		type.detect(DB2TypeFormatter.INSTANCE, str);
	}

	final void format(Appendable str, byte[] value) {
		try {
			str.append("x\'");
			str.append(Convert.bytesToHex(value, false, false));
			str.append("\')");
		} catch (Throwable e) {
			throw Utils.tryThrowException(e);
		}
	}

	@Override
	final ISqlCommandFactory sqlbuffers() {
		return DB2CommandFactory.INSTANCE;
	}

	private static final String select_dna_func = "select specificname from syscat.routines where routineschema= ? and routinename like 'DNA_%' and routinetype = 'F'";

	private static final void dropExistingDnaFunctions(Connection conn) {
		try {
			PreparedStatement ps = conn.prepareStatement(select_dna_func);
			try {
				ps.setString(1, conn.getMetaData().getUserName().toUpperCase());
				ResultSet rs = ps.executeQuery();
				try {
					Statement st = null;
					try {
						while (rs.next()) {
							if (st == null) {
								st = conn.createStatement();
							}
							final String routine = rs.getString(1);
							try {
								st.execute("drop specific function "
										.concat(routine));
							} catch (Throwable e) {
								datasourceInitException("删除旧版本的自定义函数["
										+ routine + "]错误", e);
							}
						}
					} finally {
						if (st != null) {
							st.close();
						}
					}
				} finally {
					rs.close();
				}
			} finally {
				ps.close();
			}
		} catch (SQLException e) {
			datasourceInitException("数据源初始化异常");
		}
	}

	@Override
	final void setupPackage(Connection conn, String ds) {
		dropExistingDnaFunctions(conn);
		execSqls(conn, this.getClass(), DNA_PACKAGE_SETUP + "." + this.postfix,
				false);
	}

}
