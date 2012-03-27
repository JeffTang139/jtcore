package org.eclipse.jt.core.impl;

import static org.eclipse.jt.core.impl.TableSynchronizerImpl.HierarchyState.CREATE_NEW;
import static org.eclipse.jt.core.impl.TableSynchronizerImpl.HierarchyState.DO_NOTHING;
import static org.eclipse.jt.core.impl.TableSynchronizerImpl.HierarchyState.EXTEND_PATH;
import static org.eclipse.jt.core.impl.TypeAlterability.Always;
import static org.eclipse.jt.core.impl.TypeAlterability.ColumnNull;
import static org.eclipse.jt.core.impl.TypeAlterability.ExceedExist;
import static org.eclipse.jt.core.impl.TypeAlterability.Never;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.eclipse.jt.core.type.Convert;
import org.eclipse.jt.core.type.DataType;
import org.eclipse.jt.core.type.DateParser;
import org.eclipse.jt.core.type.TypeFactory;


/**
 * @author Jeff Tang
 * 
 */
final class OracleTableSynchronizer
		extends
		TableSynchronizerImpl<OracleLang, OracleTable, OracleColumn, OracleIndex> {

	OracleTableSynchronizer(OracleLang lang, DBAdapterImpl adapter)
			throws SQLException {
		super(adapter, lang);
	}

	@Override
	final OracleTableSync newTableSync() throws SQLException {
		return new OracleTableSync();
	}

	@Override
	final OracleColumnCompareSync newColumnSync() {
		return new OracleColumnCompareSync();
	}

	@Override
	final OracleIndexSync newIndexSync() throws SQLException {
		return new OracleIndexSync();
	}

	@Override
	final OracleHierarchySync newHierarchySync() {
		return new OracleHierarchySync();
	}

	@Override
	final OracleTable newCompareTable() {
		return new OracleTable();
	}

	private final class OracleTableSync extends TableSync {

		OracleTableSync() throws SQLException {
			super(NameCaseMode.CASE_SENSITIVE);
		}

		@Override
		final void initNamespace() throws SQLException {
			fillUsingSelect(this.adapter, this.namespace, SELECT_TABLE_NAME);
		}

		private static final String SELECT_TABLE_NAME = "select table_name from user_tables";

		@Override
		final void dbCreateTable(DBTableDefineImpl define) throws SQLException {
			SqlBuilder sql = new SqlBuilder(this.lang);
			sql.appendCreate().appendTable().appendId(define.namedb()).lp();
			sql.nNewline().pi();
			for (TableFieldDefineImpl field : define.owner.fields) {
				if (field.dbTable == define || field.isRECID()) {
					sql.appendId(field.namedb()).nSpace();
					sql.appendType(field.getType()).nSpace();
					if (field.getDefault() != null) {
						sql.appendDefault();
						sql.append(defaultDefinition(field, defaultDeclare));
						sql.nSpace();
					}
					if (field.isKeepValid()) {
						sql.appendNot().appendNull();
					}
					sql.nComma().nNewline();
				}
			}
			outlineRecidConstraint(sql, define);
			sql.nNewline().ri().rp();
			if (define.isPrimary() && define.owner.isPartitioned()) {
				throw Utils.notImplemented();
				// HCL
				// if (dbTable.partType != TablePartitonType.NONE
				// && dbTable.partCols.size() > 0) {
				// final int c = dbTable.partCols.size();
				// sql.nNewline().appendPartition().appendByRange().lp();
				// for (int i = 0; i < c; i++) {
				// sql.appendId(dbTable.partCols.get(i));
				// sql.nComma().nSpace();
				// }
				// sql.uComma().rp();
				// sql.nSpace().lp().nNewline().pi();
				// sql.appendPartition().appendValues().appendLessThan().lp();
				// for (int i = 0; i < c; i++) {
				// sql.append("maxvalue").nComma().nSpace();
				// }
				// sql.uComma().rp();
				// sql.nNewline().ri().rp().append("enable row movement");
				// }
			}
			this.statement.execute(sql);
		}

	}

	private final class OracleColumnCompareSync extends ColumnCompareSync {

		@Override
		final TypeCompatiblity typeCompatible(TableFieldDefineImpl field,
				OracleColumn column) {
			return field.getType().detect(
					this.lang.getTypeCompatibleDetector(), column);
		}

		@Override
		final boolean defaultChanged(TableFieldDefineImpl field,
				OracleColumn column) {
			final ConstExpr c = field.getDefault();
			final boolean leftNull = c == null || c == NullExpr.NULL;
			final boolean rightNull = column.defaultVal == null;
			if (leftNull != rightNull
					|| (!leftNull && !rightNull && !column.defaultVal
							.equals(defaultDefinition(field, defaultDeclare)))) {
				return true;
			}
			return false;
		}

		@Override
		final void execute() throws SQLException {
			this.sync.indexSync.notifyNsBeforeDropColumn(this.compare,
					this.drop);
			this.dbDropColumns();
			this.compare.removeColumnsCascadeIndex(this.drop);
			this.renameUnusedDbColumnThenAddDefineColumn();
			this.dbAddnModifyColumn();
		}

		@Override
		final void dbRenameColumnAndSetNotNullToNullable(OracleColumn column,
				String rename) throws SQLException {
			SqlBuilder renameSql = new SqlBuilder(this.lang);
			renameSql.append("alter table ").appendId(this.compare.name);
			renameSql.append(" rename column ").appendId(column.name)
					.append(" to ").appendId(rename);
			this.statement.execute(renameSql);
			if (column.notNull) {
				SqlBuilder setNullable = new SqlBuilder(this.lang);
				setNullable.append("alter table ").appendId(this.compare.name);
				setNullable.append(" modify (").appendId(column.name)
						.append(" null)");
				this.statement.execute(setNullable);
			}
		}

		private final void dbDropColumns() throws SQLException {
			if (this.drop.size() > 0) {
				SqlBuilder sql = new SqlBuilder(this.lang);
				sql.append("alter table ").appendId(this.compare.name);
				sql.nNewline().pi().append("drop (");
				for (int i = 0, c = this.drop.size(); i < c; i++) {
					sql.appendId(this.drop.get(i).name).nComma().nSpace();
				}
				sql.uComma().uSpace().rp().ri();
				this.statement.execute(sql);
			}
		}

		private final void dbAddnModifyColumn() throws SQLException {
			if (this.add.size() == 0 && this.modify.size() == 0) {
				return;
			}
			SqlBuilder sql = new SqlBuilder(this.lang);
			sql.append("alter table ").appendId(this.compare.name);
			if (this.add.size() > 0) {
				sql.append(" add (");
				for (TableFieldDefineImpl field : this.add) {
					columnDefinition(sql, field);
					sql.nComma();
				}
				sql.uComma().rp();
			}
			if (this.modify.size() > 0) {
				sql.append(" modify (");
				for (ColumnState e : this.modify.values()) {
					columnModification(sql, e);
					sql.nComma();
				}
				sql.uComma().rp();
			}
			this.statement.execute(sql);
		}

	}

	private final class OracleIndexSync extends IndexSync {

		private OracleIndexSync() throws SQLException {
			super();
		}

		@Override
		final void initNamespace() throws SQLException {
			this.namespace = NameCaseMode.CASE_SENSITIVE.newInstance();
			fillUsingSelect(this.adapter, this.namespace, SELECT_INDEX_NAME);
		}

		private Namespace namespace;

		private static final String SELECT_INDEX_NAME = "select index_name from user_indexes";

		@Override
		final void ensureValid(IndexDefineImpl index, OracleTable dbTable) {
			if (index.ensureValidWithinNamespace(this.lang, this.namespace)) {
				this.sync.modified = true;
			}
		}

		@Override
		final void dbDropIndex(OracleIndex index) throws SQLException {
			SqlBuilder sql = new SqlBuilder(this.lang);
			sql.appendDrop().appendIndex().appendId(index.name);
			this.statement.execute(sql);
		}

		@Override
		final void notifyNsAfterCreateIndex(String index) {
			this.namespace.add(index);
		}

		@Override
		final void notifyNsAfterDropIndex(String index) {
			this.namespace.remove(index);
		}

		private static final String select_table_index = "select index_name from user_indexes where table_name = ?";

		@Override
		final void notifyNsBeforeDropTable(String tableName)
				throws SQLException {
			PreparedStatement ps = this.adapter
					.prepareStatement(select_table_index);
			try {
				ps.setString(1, tableName);
				ResultSet rs = ps.executeQuery();
				try {
					while (rs.next()) {
						this.namespace.remove(rs.getString(1));
					}
				} finally {
					rs.close();
				}
			} finally {
				this.adapter.freeStatement(ps);
			}
		}

		private static final String select_index_column = "select index_name, column_name from user_ind_columns where table_name = ?";

		@Override
		final void notifyNsBeforeDropColumn(OracleTable table,
				ArrayList<OracleColumn> columns) throws SQLException {
			PreparedStatement ps = this.adapter
					.prepareStatement(select_index_column);
			try {
				ps.setString(1, table.name);
				ResultSet rs = ps.executeQuery();
				try {
					while (rs.next()) {
						if (columns.contains(rs.getString(2))) {
							this.namespace.remove(rs.getString(1));
						}
					}
				} finally {

				}
			} finally {
				this.adapter.freeStatement(ps);
			}
		}
	}

	private final class OracleHierarchySync extends HierarchySync {

		@Override
		final HierarchyState detectState(HierarchyDefineImpl hierarchy)
				throws SQLException {
			if (hierarchy.tableName() == null) {
				return CREATE_NEW;
			} else if (!this.sync.tableSync.namespace.contains(hierarchy
					.tableName())) {
				return CREATE_NEW;
			}
			PreparedStatement ps = this.adapter
					.prepareStatement(OracleTable.SELECT_TABLE_COLUMNS);
			try {
				ps.setString(1, hierarchy.tableName());
				ResultSet rs = ps.executeQuery();
				try {
					int length = 0;
					if (rs.next()) {
						if (!rs.getString(1).equals(
								HierarchyDefineImpl.COLUMN_NAME_RECID)) {
							return CREATE_NEW;
						} else if (!rs.getString(2).equals("RAW")) {
							return CREATE_NEW;
						} else if (rs.getInt(3) != 16) {
							return CREATE_NEW;
						}
					} else {
						// unreachable
						return CREATE_NEW;
					}
					if (rs.next()) {
						if (!rs.getString(1).equals(
								HierarchyDefineImpl.COLUMN_NAME_PATH)) {
							return CREATE_NEW;
						} else if (!rs.getString(2).equals("RAW")) {
							return CREATE_NEW;
						}
						length = rs.getInt(3);
					} else {
						return CREATE_NEW;
					}
					if (rs.next()) {
						if (!rs.getString(1).equals(
								HierarchyDefineImpl.COLUMN_NAME_STATUS)) {
							return CREATE_NEW;
						} else if (!rs.getString(2).equals("NUMBER")) {
							return CREATE_NEW;
						}
					} else {
						return CREATE_NEW;
					}
					if (rs.next()) {
						return CREATE_NEW;
					}
					if (length < hierarchy.getPathLength()) {
						return EXTEND_PATH;
					}
					return DO_NOTHING;
				} finally {
					rs.close();
				}
			} finally {
				this.adapter.freeStatement(ps);
			}
		}

		@Override
		final void createHierarchyTable(HierarchyDefineImpl hierarchy)
				throws SQLException {
			SqlBuilder sql = new SqlBuilder(this.lang);
			sql.appendCreate().appendTable().appendId(hierarchy);
			sql.nSpace().lp().nNewline().pi();
			sql.appendId(HierarchyDefineImpl.COLUMN_NAME_RECID).nSpace()
					.appendType(TypeFactory.GUID).nSpace();
			sql.appendConstraint().appendId(hierarchy.pkIndex())
					.appendPrimaryKey().nComma().nNewline();
			sql.appendId(HierarchyDefineImpl.COLUMN_NAME_PATH)
					.nSpace()
					.appendType(
							TypeFactory.VARBINARY(hierarchy.getPathLength()))
					.nComma().nNewline();
			sql.appendId(HierarchyDefineImpl.COLUMN_NAME_STATUS).nSpace()
					.appendType(TypeFactory.INT).appendDefault().append("1")
					.nNewline();
			sql.ri().rp();
			this.statement.execute(sql);

		}

		@Override
		final void extendPath(HierarchyDefineImpl hierarchy)
				throws SQLException {
			SqlBuilder sql = new SqlBuilder(this.lang);
			sql.append("alter table ").appendId(hierarchy);
			sql.append(" modify (");
			sql.appendId(HierarchyDefineImpl.COLUMN_NAME_PATH).nSpace();
			sql.appendType(TypeFactory.VARBINARY(hierarchy.getPathLength()))
					.rp();
			this.statement.execute(sql);
		}

	}

	private static final void columnDefinition(SqlBuilder sql,
			TableFieldDefineImpl field) {
		sql.appendId(field.namedb()).append(' ');
		sql.appendType(field.getType());
		if (field.getDefault() != null) {
			sql.append(" default ");
			sql.append(defaultDefinition(field, defaultDeclare));
		}
		if (field.isKeepValid()) {
			sql.append(" not null");
		}
	}

	private static final void columnModification(SqlBuilder sql,
			ColumnState state) {
		sql.appendId(state.column.name);
		if (state.get(ColumnCompareSync.MOD_TYPE)) {
			sql.append(' ').appendType(state.field.getType());
		}
		if (state.get(ColumnCompareSync.MOD_DEFAULT)) {
			sql.append(" default ");
			final ConstExpr df = state.field.getDefault();
			if (df != null) {
				sql.append(defaultDefinition(state.field, defaultDeclare));
			} else {
				sql.append("null");
			}
		}
		if (state.get(ColumnCompareSync.MOD_NULLABLE)) {
			if (state.field.isKeepValid()) {
				sql.append(" not null");
			} else {
				sql.append(" null");
			}
		}
	}

	private static final ConstFormatter defaultDeclare = new ConstFormatter() {

		@Override
		public String inDate(ConstExpr c) throws Throwable {
			return "timestamp\'"
					+ DateParser.format(c.getDate(),
							DateParser.FORMAT_DATE_TIME_MS) + "\'";
		}

		@Override
		String bytes(byte[] value) {
			return "hextoraw('" + Convert.bytesToHex(value, false, false)
					+ "\')";
		}
	};

}

final class OracleTable extends DbTable<OracleTable, OracleColumn, OracleIndex> {

	/**
	 * 查询表的列定义
	 * 
	 * <p>
	 * 参数
	 * <ol>
	 * <li>表名
	 * </ol>
	 * 
	 * <p>
	 * 输出:
	 * <ol>
	 * <li>column_name 列名
	 * <li>data_type 数据类型
	 * <li>data_length 数据长度
	 * <li>data_precision 数据精度
	 * <li>data_scale 小数位
	 * <li>nullable 可否为空
	 * <li>data_default 默认值
	 * </ol>
	 */
	static final String SELECT_TABLE_COLUMNS = "select column_name, data_type, data_length, data_precision, data_scale, nullable, data_default from user_tab_cols where table_name = ? and virtual_column = 'NO' order by column_id";

	/**
	 * 查询索引列信息
	 * 
	 * <p>
	 * JDBC接口中的DatabaseMetaData的getColumns()方法,对于降序的索引列,不能正确返回列名.
	 * 所以直接查询oracle字典. 对于降序索引,从refer列中读取列名.
	 * 
	 * <p>
	 * 参数
	 * <ol>
	 * <li>模式名称即用户名称(schema)
	 * <li>索引所在表
	 * </ol>
	 * 
	 * <p>
	 * 输出：
	 * <ol>
	 * <li>index_name 索引名
	 * <li>is_unique 是否唯一
	 * <li>column_name 索引字段名称(索引项升序时才有效)
	 * <li>is_desc 是否降序索引
	 * <li>desc_column_refer 降序索引时的字段名称
	 * </ol>
	 */
	private static final String SELECT_INDEX_COLUMNS = "select i.index_name, decode(i.uniqueness,'UNIQUE',1,0) as is_unique, ic.column_name, decode(ic.descend,'DESC',1, 0 ) as is_desc, tc.data_default as desc_column_refer from user_indexes i inner join user_ind_columns ic on i.table_name = ic.table_name and i.index_name = ic.index_name inner join user_tab_cols tc on i.table_name = tc.table_name and ic.column_name = tc.column_name where i.table_owner = ?  and i.table_name = ? order by ic.column_position";

	/**
	 * 查询表的主键约束名称
	 * 
	 * <p>
	 * 参数
	 * <ol>
	 * <li>表名
	 * </ol>
	 * 
	 * <p>
	 * 输出:
	 * <ol>
	 * <li>主键约束名称
	 * </ol>
	 */
	private static final String SELECT_PK_CON = "select constraint_name from user_constraints where table_name = ? and constraint_type = 'P'";

	@Override
	final void loadColumn(DBAdapterImpl adapter) throws SQLException {
		PreparedStatement ps = adapter.prepareStatement(SELECT_TABLE_COLUMNS);
		try {
			ps.setString(1, this.name);
			ResultSet rs = ps.executeQuery();
			try {
				while (rs.next()) {
					String column = rs.getString(1);
					this.addColumn(column).load(rs);
				}
			} finally {
				rs.close();
			}
		} finally {
			adapter.freeStatement(ps);
		}
		if (this.columns.size() == 0) {
			throw tableNotExists(this.name);
		}
	}

	@Override
	final void loadIndex(DBAdapterImpl adapter) throws SQLException {
		PreparedStatement ps = adapter.prepareStatement(SELECT_INDEX_COLUMNS);
		try {
			ps.setString(1, adapter.getDefaultSchema());
			ps.setString(2, this.name);
			ResultSet rs = ps.executeQuery();
			try {
				while (rs.next()) {
					final String indexName = rs.getString(1);
					OracleIndex index = this.findIndex(indexName);
					final boolean desc = rs.getBoolean(4);
					String columnName;
					if (desc) {
						String refer = rs.getString(5);
						columnName = refer.substring(1, refer.length() - 1);
					} else {
						columnName = rs.getString(3);
					}
					if (index == null) {
						index = this.addIndex(indexName, rs.getBoolean(2));
					}
					final OracleColumn column = this.getColumn(columnName);
					index.add(column, desc);
					// this.columnWasIndexed(column, index);
				}
			} finally {
				rs.close();
			}
		} finally {
			ps.close();
		}
		this.primary = null;
		ps = adapter.prepareStatement(SELECT_PK_CON);
		try {
			ps.setString(1, this.name);
			ResultSet rs = ps.executeQuery();
			try {
				if (rs.next()) {
					this.primary = this.getIndex(rs.getString(1));
				}
			} finally {
				rs.close();
			}
		} finally {
			ps.close();
		}
	}

	@Override
	final OracleColumn newColumnOnly(String name) {
		return new OracleColumn(this, name);
	}

	@Override
	final OracleIndex newIndexOnly(String name, boolean unique) {
		return new OracleIndex(this, name, unique);
	}

	@Override
	final void checkEmptyStatus(DBAdapterImpl adapter) throws SQLException {
		this.tableEmpty = !exists(adapter, "select 1 from " + this.name
				+ " where rownum <= 1");
	}

}

final class OracleColumn extends
		DbColumn<OracleTable, OracleColumn, OracleIndex> {

	OracleColumn(OracleTable table, String name) {
		super(table, name);
	}

	OracleDataType type;

	final void load(ResultSet rs) throws SQLException {
		this.type = OracleDataType.typeOf(rs.getString(2));
		if (this.type == OracleDataType.NCHAR
				|| this.type == OracleDataType.NVARCHAR2) {
			this.length = rs.getInt(3) / 2;
		} else {
			this.length = rs.getInt(3);
		}
		this.precision = rs.getInt(4);
		this.scale = rs.getInt(5);
		this.notNull = rs.getString(6).equals("N");
		String defaultVal = rs.getString(7);
		if (defaultVal != null) {
			this.defaultVal = defaultVal.trim();
		}
	}

	@Override
	final TypeAlterability typeAlterable(DataType type) {
		return this.type.alterable(this, type);
	}
}

enum OracleDataType {

	NUMBER {
		@Override
		TypeAlterability alterable(OracleColumn column, DataType type) {
			if (type == BooleanType.TYPE) {
				if (column.precision == 1 && column.scale == 0) {
					return Always;
				}
			} else if (type == ShortType.TYPE) {
				if (column.precision != 0 && column.precision <= 5
						&& column.scale == 0) {
					return Always;
				}
			} else if (type == IntType.TYPE) {
				if (column.precision != 0 && column.precision <= 10
						&& column.scale == 0) {
					return Always;
				}
			} else if (type == LongType.TYPE) {
				if (column.precision != 0 && column.precision <= 19
						&& column.scale == 0) {
					return Always;
				}
			} else if (type instanceof NumericDBType) {
				NumericDBType nt = (NumericDBType) type;
				if (nt.precision - nt.scale >= column.precision - column.scale
						&& nt.scale >= column.scale) {
					return Always;
				}
			} else if (type.isLOB()) {
				return Never;
			}
			return ColumnNull;
		}
	},
	FLOAT {
		@Override
		TypeAlterability alterable(OracleColumn column, DataType type) {
			if (type.isLOB()) {
				return Never;
			} else if (column.precision == 63) {
				if (type == FloatType.TYPE || type == DoubleType.TYPE) {
					return Always;
				}
			} else if (column.precision == 126) {
				if (type == DoubleType.TYPE) {
					return Always;
				}
			}
			return ColumnNull;
		}
	},
	BINARY_FLOAT {
		@Override
		TypeAlterability alterable(OracleColumn column, DataType type) {
			if (type.isLOB()) {
				return Never;
			}
			return ColumnNull;
		}
	},
	BINARY_DOUBLE {
		@Override
		TypeAlterability alterable(OracleColumn column, DataType type) {
			if (type.isLOB()) {
				return Never;
			}
			return ColumnNull;
		}
	},
	CHAR {
		@Override
		TypeAlterability alterable(OracleColumn column, DataType type) {
			if (type instanceof CharDBType || type instanceof VarCharDBType) {
				CharsType ct = (CharsType) type;
				if (ct.length >= column.length) {
					return Always;
				} else {
					return ColumnNull;
				}
			} else if (type instanceof NCharDBType
					|| type instanceof NVarCharDBType) {
				CharsType ct = (CharsType) type;
				if (ct.length >= column.length) {
					return Always;
				} else {
					return ExceedExist;
				}
			} else if (type.isLOB()) {
				return Never;
			}
			return ColumnNull;
		}
	},
	VARCHAR2 {
		@Override
		TypeAlterability alterable(OracleColumn column, DataType type) {
			if (type instanceof CharDBType || type instanceof VarCharDBType) {
				CharsType ct = (CharsType) type;
				if (ct.length >= column.length) {
					return Always;
				} else {
					return ExceedExist;
				}
			} else if (type.isLOB()) {
				return Never;
			}
			return ColumnNull;
		}
	},
	CLOB {
		@Override
		TypeAlterability alterable(OracleColumn column, DataType type) {
			return Never;
		}
	},
	NCHAR {
		@Override
		TypeAlterability alterable(OracleColumn column, DataType type) {
			if (type instanceof NCharDBType || type instanceof NVarCharDBType) {
				CharsType ct = (CharsType) type;
				if (ct.length >= column.length) {
					return Always;
				} else {
					return ExceedExist;
				}
			} else if (type.isLOB()) {
				return Never;
			}
			return ColumnNull;
		}
	},
	NVARCHAR2 {
		@Override
		TypeAlterability alterable(OracleColumn column, DataType type) {
			if (type instanceof NCharDBType || type instanceof NVarCharDBType) {
				CharsType ct = (CharsType) type;
				if (ct.length >= column.length) {
					return Always;
				} else {
					return ExceedExist;
				}
			} else if (type.isLOB()) {
				return Never;
			}
			return ColumnNull;
		}
	},
	NCLOB {
		@Override
		TypeAlterability alterable(OracleColumn column, DataType type) {
			return Never;
		}
	},
	RAW {
		@Override
		TypeAlterability alterable(OracleColumn column, DataType type) {
			if (type instanceof BinDBType) {
				BinDBType bt = (BinDBType) type;
				if (bt.length >= column.length) {
					return Always;
				} else {
					return ExceedExist;
				}
			} else if (type == GUIDType.TYPE && column.length <= 16) {
				return Always;
			} else if (type.isLOB()) {
				return Never;
			}
			return ColumnNull;
		}
	},
	BLOB {
		@Override
		TypeAlterability alterable(OracleColumn column, DataType type) {
			return Never;
		}
	},
	DATE {
		@Override
		TypeAlterability alterable(OracleColumn column, DataType type) {
			return forDateCategory(type);
		}
	},
	TIMESTAMP {
		@Override
		TypeAlterability alterable(OracleColumn column, DataType type) {
			return forDateCategory(type);
		}
	},
	TIMESTAMP_WITH_TIME_ZONE {
		@Override
		TypeAlterability alterable(OracleColumn column, DataType type) {
			return forDateCategory(type);
		}
	},
	TIMESTAMP_WITH_LOCAL_TIME_ZONE {
		@Override
		TypeAlterability alterable(OracleColumn column, DataType type) {
			return forDateCategory(type);
		}
	},
	INTERVAL_YEAR_TO_MONTH {
		@Override
		TypeAlterability alterable(OracleColumn column, DataType type) {
			throw new UnsupportedOperationException();
		}
	},
	INTERVAL_DAY_TO_SECOND {
		@Override
		TypeAlterability alterable(OracleColumn column, DataType type) {
			throw new UnsupportedOperationException();
		}
	},
	ROWID {
		@Override
		TypeAlterability alterable(OracleColumn column, DataType type) {
			throw new UnsupportedOperationException();
		}
	},
	UROWID {
		@Override
		TypeAlterability alterable(OracleColumn column, DataType type) {
			throw new UnsupportedOperationException();
		}
	};

	private static final TypeAlterability forDateCategory(DataType type) {
		if (type == DateType.TYPE) {
			return Always;
		} else if (type.isLOB()) {
			return Never;
		}
		return ColumnNull;
	}

	abstract TypeAlterability alterable(OracleColumn column, DataType type);

	static final OracleDataType typeOf(String type) {
		if (type.startsWith("TIMESTAMP")) {
			if (type.endsWith("WITH TIME ZONE")) {
				return OracleDataType.TIMESTAMP_WITH_TIME_ZONE;
			} else if (type.endsWith("WITH LOCAL TIME ZONE")) {
				return OracleDataType.TIMESTAMP_WITH_LOCAL_TIME_ZONE;
			}
			return OracleDataType.TIMESTAMP;
		} else if (type.startsWith("INTERVAL YEAR")) {
			return OracleDataType.INTERVAL_YEAR_TO_MONTH;
		} else if (type.startsWith("INTERVAL DAY")) {
			return OracleDataType.INTERVAL_DAY_TO_SECOND;
		} else {
			return OracleDataType.valueOf(type);
		}
	}

}

final class OracleIndex extends DbIndex<OracleTable, OracleColumn, OracleIndex> {

	OracleIndex(OracleTable table, String name, boolean unique) {
		super(table, name, unique);
	}

}
