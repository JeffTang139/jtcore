package org.eclipse.jt.core.impl;

import static org.eclipse.jt.core.impl.TableSynchronizerImpl.HierarchyState.CREATE_NEW;
import static org.eclipse.jt.core.impl.TableSynchronizerImpl.HierarchyState.DO_NOTHING;
import static org.eclipse.jt.core.impl.TableSynchronizerImpl.HierarchyState.EXTEND_PATH;
import static org.eclipse.jt.core.impl.TypeAlterability.Always;
import static org.eclipse.jt.core.impl.TypeAlterability.ColumnNull;
import static org.eclipse.jt.core.impl.TypeAlterability.ExceedExist;
import static org.eclipse.jt.core.impl.TypeCompatiblity.Exactly;
import static org.eclipse.jt.core.impl.TypeCompatiblity.NotSuggest;
import static org.eclipse.jt.core.impl.TypeCompatiblity.Overflow;
import static org.eclipse.jt.core.impl.TypeCompatiblity.Unable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.eclipse.jt.core.type.Convert;
import org.eclipse.jt.core.type.DataType;
import org.eclipse.jt.core.type.DateParser;
import org.eclipse.jt.core.type.SequenceDataType;
import org.eclipse.jt.core.type.TypeDetector;
import org.eclipse.jt.core.type.TypeDetectorBase;
import org.eclipse.jt.core.type.TypeFactory;


/**
 * @author Jeff Tang
 * 
 */
final class MySQLTableSynchronizer extends
		TableSynchronizerImpl<MySQLLang, MySQLTable, MySQLColumn, MySQLIndex> {

	MySQLTableSynchronizer(DBAdapterImpl adapter, MySQLLang lang)
			throws SQLException {
		super(adapter, lang);
	}

	@Override
	final MySQLTableSync newTableSync() throws SQLException {
		return new MySQLTableSync();
	}

	@Override
	final MySQLColumnCompareSync newColumnSync() {
		return new MySQLColumnCompareSync();
	}

	@Override
	final MySQLIndexSync newIndexSync() throws SQLException {
		return new MySQLIndexSync();
	}

	@Override
	final MySQLHierarchySync newHierarchySync() {
		return new MySQLHierarchySync();
	}

	@Override
	final MySQLTable newCompareTable() {
		return new MySQLTable();
	}

	private final class MySQLTableSync extends TableSync {

		MySQLTableSync() throws SQLException {
			super(NameCaseMode.CASE_INSENSITIVE_DISPLAY_LOWER);
		}

		private static final String SELECT_TABLE_NAME = "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = ?";

		@Override
		final void initNamespace() throws SQLException {
			PreparedStatement ps = this.adapter
					.prepareStatement(SELECT_TABLE_NAME);
			try {
				ps.setString(1, this.adapter.getCatalog());
				fillUsingStatement(this.namespace, ps);
			} finally {
				this.adapter.freeStatement(ps);
			}
		}

		@Override
		final void dbCreateTable(DBTableDefineImpl define) throws SQLException {
			SqlBuilder sql = new SqlBuilder(this.lang);
			sql.appendCreate().appendTable().appendId(define.name).lp();
			sql.nNewline().pi();
			for (TableFieldDefineImpl field : define.owner.fields) {
				if (field.isRECID()) {
					sql.appendId(field.namedb()).nSpace();
					sql.appendType(field.getType()).nSpace();
					sql.appendNot().appendNull().nSpace();
					sql.appendPrimaryKey();
				} else if (field.dbTable == define) {
					appendColumnDefinition(sql, field, false, false);
				}
				sql.nComma().nNewline();
			}
			sql.uComma().ri().rp();
			this.statement.execute(sql);
		}

	}

	private final class MySQLColumnCompareSync extends ColumnCompareSync {

		@Override
		final void execute() throws SQLException {
			this.dbDropColumns();
			this.compare.removeColumnsCascadeIndex(this.drop);
			this.renameUnusedDbColumnThenAddDefineColumn();
			this.dbAddnAlterColumns();
		}

		@Override
		final TypeCompatiblity typeCompatible(TableFieldDefineImpl field,
				MySQLColumn column) {
			return field.getType().detect(compatible, column);
		}

		@Override
		final boolean defaultChanged(TableFieldDefineImpl field,
				MySQLColumn column) {
			final boolean leftNull = field.getDefault() == null;
			final boolean rightNull = column.defaultVal == null;
			if (leftNull != rightNull
					|| (!leftNull && !rightNull && !column.defaultVal
							.equals(defaultDefinition(field, defaultCompare)))) {
				return true;
			}
			return false;
		}

		@Override
		final void dbRenameColumnAndSetNotNullToNullable(MySQLColumn column,
				String rename) throws SQLException {
			SqlBuilder sql = new SqlBuilder(this.lang);
			sql.append("alter table ").appendId(column.table.name);
			sql.append(" change column ").appendId(column.name);
			sql.append(' ').appendId(rename).append(' ');
			column.formatType(sql);
			if (column.notNull) {
				sql.append(" null");
			}
			this.statement.execute(sql);
		}

		private final void dbDropColumns() throws SQLException {
			SqlBuilder sql = new SqlBuilder(this.lang);
			sql.appendAlter().appendId(this.compare.name).nNewline().pi();
			for (int i = 0, c = this.drop.size(); i < c; i++) {
				sql.appendDrop().appendColumn().appendId(this.drop.get(i).name)
						.nComma();
			}
			sql.uComma();
			this.statement.execute(sql);
		}

		private final void dbAddnAlterColumns() throws SQLException {
			if (this.add.size() == 0 || this.modify.size() == 0) {
				return;
			}
			SqlBuilder sql = new SqlBuilder(this.lang);
			sql.append("alter table ").appendId(this.compare.name);
			if (this.add.size() > 0) {
				sql.append(" add column (");
				for (TableFieldDefineImpl f : this.add) {
					appendColumnDefinition(sql, f, false, false);
					sql.nComma();
				}
				sql.uComma().append(')');
			}
			if (this.modify.size() > 0) {
				for (ColumnState state : this.modify.values()) {
					sql.append(" modify column ");
					appendColumnDefinition(sql, state.field, true, true);
					sql.nComma();
				}
				sql.uComma();
			}
			this.statement.execute(sql);
		}

	}

	private final class MySQLIndexSync extends IndexSync {

		private MySQLIndexSync() throws SQLException {
			super();
		}

		@Override
		final void initNamespace() throws SQLException {

		}

		@Override
		final void ensureValid(IndexDefineImpl index, MySQLTable dbTable) {
			this.ensureValieWithinTable(index, dbTable);
		}

		@Override
		final void dbDropIndex(MySQLIndex index) throws SQLException {
			SqlBuilder sql = new SqlBuilder(this.lang);
			sql.append("drop index ").appendId(index.name);
			sql.append(" on ").appendId(index.table.name);
			this.statement.execute(sql);
		}

		@Override
		final void notifyNsAfterCreateIndex(String index) {
		}

		@Override
		final void notifyNsAfterDropIndex(String index) {
		}

		@Override
		final void notifyNsBeforeDropTable(String tableName)
				throws SQLException {
		}

		@Override
		final void notifyNsBeforeDropColumn(MySQLTable table,
				ArrayList<MySQLColumn> columns) throws SQLException {
		}

	}

	private final class MySQLHierarchySync extends HierarchySync {

		@Override
		HierarchyState detectState(HierarchyDefineImpl hierarchy)
				throws SQLException {
			if (hierarchy.tableName() == null) {
				return CREATE_NEW;
			} else if (!this.sync.tableSync.namespace.contains(hierarchy
					.tableName())) {
				return CREATE_NEW;
			}
			PreparedStatement ps = this.adapter
					.prepareStatement(MySQLTable.SELECT_TABLE_COLUMNS);
			try {
				ps.setString(1, this.adapter.getCatalog());
				ps.setString(2, hierarchy.tableName());
				ResultSet rs = ps.executeQuery();
				try {
					int length = 0;
					if (rs.next()) {
						if (!rs.getString(1).equals(
								HierarchyDefineImpl.COLUMN_NAME_RECID)) {
							return CREATE_NEW;
						} else if (!rs.getString(2).equals("binary")) {
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
						} else if (!rs.getString(2).equals("binary")) {
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
						} else if (!rs.getString(2).equals("int")) {
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
					.appendType(TypeFactory.GUID).nSpace().appendPrimaryKey()
					.nComma().nNewline();
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
			sql.append("alter table ").appendId(hierarchy)
					.append(" alter column ");
			sql.appendId(HierarchyDefineImpl.COLUMN_NAME_PATH).nSpace();
			sql.appendType(TypeFactory.VARBINARY(hierarchy.getPathLength()));
			this.statement.execute(sql);

		}

	}

	private static final void appendColumnDefinition(SqlBuilder sql,
			TableFieldDefineImpl field, boolean nullableExplicit,
			boolean defaultExplicit) {
		sql.appendId(field.namedb()).nSpace();
		sql.appendType(field.getType()).nSpace();
		if (field.isKeepValid()) {
			sql.appendNot().appendNull();
			sql.nSpace();
		} else if (nullableExplicit) {
			sql.appendNull().nSpace();
		}
		if (field.getDefault() != null) {
			sql.appendDefault();
			sql.append(defaultDefinition(field, defaultDeclare));
			sql.nSpace();
		} else if (defaultExplicit) {
			sql.appendDefault().appendNull().nSpace();
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
			return "unhex('" + Convert.bytesToHex(value, false, false) + "\')";
		}
	};

	private static final ConstFormatter defaultCompare = new ConstFormatter() {

		@Override
		public String inString(ConstExpr c, SequenceDataType type)
				throws Throwable {
			return escape(c.getString());
		}

		@Override
		public String inDate(ConstExpr c) throws Throwable {
			return "timestamp\'"
					+ DateParser.format(c.getDate(),
							DateParser.FORMAT_DATE_TIME_MS) + "\'";
		}

		@Override
		String bytes(byte[] value) {
			return "unhex('" + Convert.bytesToHex(value, false, false) + "\')";
		}
	};

	private static final TypeDetector<TypeCompatiblity, MySQLColumn> compatible = new TypeDetectorBase<TypeCompatiblity, MySQLColumn>() {

		@Override
		public TypeCompatiblity inBoolean(MySQLColumn column) throws Throwable {
			if (column.type == MySQLType.TINYINT) {
				return Exactly;
			} else if (column.type == MySQLType.SMALLINT
					|| column.type == MySQLType.MEDIUMINT
					|| column.type == MySQLType.INT
					|| column.type == MySQLType.BIGINT) {
				return Overflow;
			} else if (column.type == MySQLType.DECIMAL
					&& column.precision >= 1 && column.scale == 0) {
				return NotSuggest;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inShort(MySQLColumn column) throws Throwable {
			if (column.type == MySQLType.SMALLINT) {
				return Exactly;
			} else if (column.type == MySQLType.MEDIUMINT
					|| column.type == MySQLType.INT
					|| column.type == MySQLType.BIGINT) {
				return Overflow;
			} else if (column.type == MySQLType.DECIMAL
					&& column.precision >= 5 && column.scale == 0) {
				return NotSuggest;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inInt(MySQLColumn column) throws Throwable {
			if (column.type == MySQLType.INT) {
				return Exactly;
			} else if (column.type == MySQLType.BIGINT) {
				return Overflow;
			} else if (column.type == MySQLType.DECIMAL
					&& column.precision >= 10 && column.scale == 0) {
				return NotSuggest;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inLong(MySQLColumn column) throws Throwable {
			if (column.type == MySQLType.BIGINT) {
				return Exactly;
			} else if (column.type == MySQLType.DECIMAL
					&& column.precision >= 19 && column.scale == 0) {
				return NotSuggest;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inFloat(MySQLColumn column) throws Throwable {
			if (column.type == MySQLType.FLOAT) {
				return Exactly;
			} else if (column.type == MySQLType.DOUBLE) {
				return Overflow;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inDouble(MySQLColumn column) throws Throwable {
			if (column.type == MySQLType.DOUBLE) {
				return Exactly;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inNumeric(MySQLColumn column, int precision,
				int scale) throws Throwable {
			if (column.type == MySQLType.DECIMAL) {
				if (column.precision == precision && column.scale == scale) {
					return Exactly;
				} else if (((column.precision - column.scale) >= (precision - scale))
						&& column.scale >= scale) {
					return Overflow;
				}
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inChar(MySQLColumn column, SequenceDataType type)
				throws Throwable {
			if (column.type == MySQLType.CHAR && !column.national()) {
				if (column.length == type.getMaxLength()) {
					return Exactly;
				} else if (column.length > type.getMaxLength()) {
					return NotSuggest;
				}
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inVarChar(MySQLColumn column,
				SequenceDataType type) throws Throwable {
			if (column.type == MySQLType.VARCHAR && !column.national()) {
				if (column.length == type.getMaxLength()) {
					return Exactly;
				} else if (column.length > type.getMaxLength()) {
					return Overflow;
				}
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inText(MySQLColumn column) throws Throwable {
			if (column.type == MySQLType.LONGTEXT && !column.national()) {
				return Exactly;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inNChar(MySQLColumn column,
				SequenceDataType type) throws Throwable {
			if (column.type == MySQLType.CHAR && column.national()) {
				if (column.length == type.getMaxLength()) {
					return Exactly;
				} else if (column.length > type.getMaxLength()) {
					return NotSuggest;
				}
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inNVarChar(MySQLColumn column,
				SequenceDataType type) throws Throwable {
			if (column.type == MySQLType.VARCHAR && column.national()) {
				if (column.length == type.getMaxLength()) {
					return Exactly;
				} else if (column.length > type.getMaxLength()) {
					return Overflow;
				}
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inNText(MySQLColumn column) throws Throwable {
			if (column.type == MySQLType.LONGTEXT && column.national()) {
				return Exactly;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inBinary(MySQLColumn column,
				SequenceDataType type) throws Throwable {
			final int length = type.getMaxLength();
			if (column.type == MySQLType.BINARY) {
				if (column.length == length) {
					return Exactly;
				} else if (column.length > length) {
					return NotSuggest;
				}
			} else if (column.type == MySQLType.VARBINARY
					&& column.length >= length) {
				return NotSuggest;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inVarBinary(MySQLColumn column,
				SequenceDataType type) throws Throwable {
			final int length = type.getMaxLength();
			if (column.type == MySQLType.VARBINARY) {
				if (column.length == length) {
					return Exactly;
				} else if (column.length > length) {
					return Overflow;
				}
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inBlob(MySQLColumn column) throws Throwable {
			if (column.type == MySQLType.LONGBLOB) {
				return Exactly;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inGUID(MySQLColumn column) throws Throwable {
			if (column.type == MySQLType.BINARY && column.length == 16) {
				return Exactly;
			} else if (column.type == MySQLType.VARBINARY
					&& column.length >= 16) {
				return NotSuggest;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inDate(MySQLColumn column) throws Throwable {
			if (column.type == MySQLType.TIMESTAMP) {
				return Exactly;
			} else if (column.type == MySQLType.DATETIME) {
				return NotSuggest;
			}
			return Unable;
		}

	};

}

final class MySQLTable extends DbTable<MySQLTable, MySQLColumn, MySQLIndex> {

	/**
	 * 查询表的字段定义
	 * 
	 * <ol>
	 * <li>column_name 列名
	 * <li>data_type 数据类型
	 * <li>column_type 数据类型声明
	 * <li>character_maximum_length 字符串长度
	 * <li>character_set_name 字符串字符集
	 * <li>numeric_precision 数值精度
	 * <li>numeric_scale 数值小数位
	 * <li>is_nullable
	 * <li>column_default 默认值
	 * <li>column_key 键
	 * </ol>
	 */
	static final String SELECT_TABLE_COLUMNS = "select column_name, data_type, column_type, character_maximum_length, character_set_name, numeric_precision, numeric_scale, is_nullable, column_default, column_key from information_schema.columns where table_schema = ? and table_name = ? order by ordinal_position";

	/**
	 * 查询索引列信息
	 * 
	 * <ol>
	 * <li>index_name
	 * <li>non_unique
	 * <li>column_name
	 * </ol>
	 */
	private static final String SELECT_INDEX_COLUMNS = "select index_name, non_unique, column_name from information_schema.statistics where table_schema = ? and table_name = ? order by index_name, seq_in_index";

	@Override
	final void loadColumn(DBAdapterImpl adapter) throws SQLException {
		PreparedStatement ps = adapter.prepareStatement(SELECT_TABLE_COLUMNS);
		try {
			ps.setString(1, adapter.getCatalog());
			ps.setString(2, this.name.toLowerCase());
			ResultSet rs = ps.executeQuery();
			try {
				while (rs.next()) {
					final String column = rs.getString(1);
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
	final MySQLColumn newColumnOnly(String name) {
		return new MySQLColumn(this, name);
	}

	@Override
	final void loadIndex(DBAdapterImpl adapter) throws SQLException {
		PreparedStatement ps = adapter.prepareStatement(SELECT_INDEX_COLUMNS);
		try {
			ps.setString(1, adapter.getCatalog());
			ps.setString(2, this.name.toLowerCase());
			ResultSet rs = ps.executeQuery();
			try {
				while (rs.next()) {
					final String name = rs.getString(1);
					MySQLIndex index = this.findIndex(name);
					if (index == null) {
						final boolean unique = rs.getInt(2) == 0;
						index = this.addIndex(name, unique);
					}
					final MySQLColumn column = this.getColumn(rs.getString(3));
					index.add(column, false);
				}
			} finally {
				rs.close();
			}
		} catch (Exception e) {
			throw new SQLException();
		} finally {
			adapter.freeStatement(ps);
		}
	}

	@Override
	final MySQLIndex newIndexOnly(String name, boolean unique) {
		return new MySQLIndex(this, name, unique);
	}

	@Override
	final void checkEmptyStatus(DBAdapterImpl adapter) throws SQLException {
		this.tableEmpty = !exists(adapter, "select 1 from " + this.name
				+ " limit 1");
	}

}

enum MySQLType {
	BIT {
		@Override
		TypeAlterability alterable(MySQLColumn column, DataType type) {
			return Always;
		}
	},
	TINYINT {
		@Override
		TypeAlterability alterable(MySQLColumn column, DataType type) {
			if (type == ShortType.TYPE || type == IntType.TYPE
					|| type == LongType.TYPE || type == FloatType.TYPE
					|| type == DoubleType.TYPE || type == GUIDType.TYPE) {
				return Always;
			} else if (type instanceof NumericDBType) {
				NumericDBType nt = (NumericDBType) type;
				if (nt.precision - nt.scale >= 3) {
					return Always;
				}
			} else if (type == BooleanType.TYPE) {
				return ExceedExist;
			} else if (type.isString() || type.isBytes()) {
				return ExceedExist;
			}
			return ColumnNull;
		}
	},
	SMALLINT {
		@Override
		TypeAlterability alterable(MySQLColumn column, DataType type) {
			if (type == ShortType.TYPE || type == IntType.TYPE
					|| type == LongType.TYPE || type == FloatType.TYPE
					|| type == DoubleType.TYPE) {
				return Always;
			} else if (type instanceof NumericDBType) {
				NumericDBType nt = (NumericDBType) type;
				if (nt.precision - nt.scale >= 5) {
					return Always;
				}
			} else if (type == GUIDType.TYPE) {
				return Always;
			} else if (type == BooleanType.TYPE) {
				return ExceedExist;
			} else if (type.isString() || type.isBytes()) {
				return ExceedExist;
			}
			return ColumnNull;
		}
	},
	MEDIUMINT {
		@Override
		TypeAlterability alterable(MySQLColumn column, DataType type) {
			if (type == IntType.TYPE || type == LongType.TYPE
					|| type == FloatType.TYPE || type == DoubleType.TYPE) {
				return Always;
			} else if (type instanceof NumericDBType) {
				NumericDBType nt = (NumericDBType) type;
				if (nt.precision - nt.scale >= 7) {
					return Always;
				}
			} else if (type == BooleanType.TYPE || type == ShortType.TYPE) {
				return ExceedExist;
			} else if (type.isString() || type.isBytes()) {
				return ExceedExist;
			}
			return ColumnNull;
		}
	},
	INT {
		@Override
		TypeAlterability alterable(MySQLColumn column, DataType type) {
			if (type == IntType.TYPE || type == LongType.TYPE
					|| type == FloatType.TYPE || type == DoubleType.TYPE) {
				return Always;
			} else if (type instanceof NumericDBType) {
				NumericDBType nt = (NumericDBType) type;
				if (nt.precision - nt.scale >= 10) {
					return Always;
				}
			} else if (type == BooleanType.TYPE || type == ShortType.TYPE) {
				return ExceedExist;
			} else if (type.isString() || type.isBytes()) {
				return ExceedExist;
			}
			return ColumnNull;
		}
	},
	BIGINT {
		@Override
		TypeAlterability alterable(MySQLColumn column, DataType type) {
			if (type == LongType.TYPE || type == FloatType.TYPE
					|| type == DoubleType.TYPE) {
				return Always;
			} else if (type instanceof NumericDBType) {
				NumericDBType nt = (NumericDBType) type;
				if (nt.precision - nt.scale >= 19) {
					return Always;
				}
			} else if (type == BooleanType.TYPE || type == ShortType.TYPE
					|| type == IntType.TYPE) {
				return ExceedExist;
			} else if (type.isString() || type.isBytes()) {
				return ExceedExist;
			}
			return ColumnNull;
		}
	},
	FLOAT {
		@Override
		TypeAlterability alterable(MySQLColumn column, DataType type) {
			if (type.isNumber()) {
				return ExceedExist;
			} else if (type == FloatType.TYPE || type == DoubleType.TYPE) {
				return Always;
			} else if (type.isString() || type.isBytes()) {
				return ExceedExist;
			}
			return ColumnNull;
		}
	},
	DOUBLE {
		@Override
		TypeAlterability alterable(MySQLColumn column, DataType type) {
			if (type.isNumber()) {
				return ExceedExist;
			} else if (type == FloatType.TYPE || type == DoubleType.TYPE) {
				return Always;
			} else if (type.isString() || type.isBytes()) {
				return ExceedExist;
			}
			return ColumnNull;
		}
	},
	DECIMAL {
		@Override
		TypeAlterability alterable(MySQLColumn column, DataType type) {
			if (type instanceof NumericDBType) {
				NumericDBType nt = (NumericDBType) type;
				if (nt.precision - nt.scale >= column.precision - column.scale
						&& nt.scale >= column.scale) {
					return Always;
				}
			} else if (type.isNumber()) {
				return ExceedExist;
			} else if (type == FloatType.TYPE || type == DoubleType.TYPE) {
				return Always;
			}
			return ColumnNull;
		}
	},
	YEAR {
		@Override
		TypeAlterability alterable(MySQLColumn column, DataType type) {
			if (type == DateType.TYPE) {
				return Always;
			}
			return ColumnNull;
		}
	},
	DATE {
		@Override
		TypeAlterability alterable(MySQLColumn column, DataType type) {
			if (type == DateType.TYPE) {
				return Always;
			}
			return ColumnNull;
		}
	},
	TIME {
		@Override
		TypeAlterability alterable(MySQLColumn column, DataType type) {
			if (type == DateType.TYPE) {
				return Always;
			}
			return ColumnNull;
		}
	},
	DATETIME {
		@Override
		TypeAlterability alterable(MySQLColumn column, DataType type) {
			if (type == DateType.TYPE) {
				return Always;
			}
			return ColumnNull;
		}
	},
	TIMESTAMP {
		@Override
		TypeAlterability alterable(MySQLColumn column, DataType type) {
			if (type == DateType.TYPE) {
				return Always;
			}
			return ColumnNull;
		}
	},
	CHAR {
		@Override
		TypeAlterability alterable(MySQLColumn column, DataType type) {
			if (type == TextDBType.TYPE || type == NTextDBType.TYPE) {
				return Always;
			} else if (type instanceof CharsType) {
				CharsType ct = (CharsType) type;
				if (ct.length >= column.length) {
					return Always;
				} else {
					return ExceedExist;
				}
			}
			return ColumnNull;
		}
	},
	VARCHAR {
		@Override
		TypeAlterability alterable(MySQLColumn column, DataType type) {
			if (type == TextDBType.TYPE || type == NTextDBType.TYPE) {
				return Always;
			} else if (type instanceof CharsType) {
				CharsType ct = (CharsType) type;
				if (ct.length >= column.length) {
					return Always;
				} else {
					return ExceedExist;
				}
			}
			return ColumnNull;
		}
	},
	TINYTEXT {
		@Override
		TypeAlterability alterable(MySQLColumn column, DataType type) {
			if (type == TextDBType.TYPE || type == NTextDBType.TYPE) {
				return Always;
			} else if (type instanceof CharsType) {
				CharsType ct = (CharsType) type;
				if (ct.length >= 255) {
					return Always;
				} else {
					return ExceedExist;
				}
			}
			return ColumnNull;
		}
	},
	TEXT {
		@Override
		TypeAlterability alterable(MySQLColumn column, DataType type) {
			if (type == TextDBType.TYPE || type == NTextDBType.TYPE) {
				return Always;
			} else if (type instanceof CharsType) {
				return ExceedExist;
			}
			return ColumnNull;
		}
	},
	MEDIUMTEXT {
		@Override
		TypeAlterability alterable(MySQLColumn column, DataType type) {
			if (type == TextDBType.TYPE || type == NTextDBType.TYPE) {
				return Always;
			} else if (type instanceof CharsType) {
				return ExceedExist;
			}
			return ColumnNull;
		}
	},

	LONGTEXT {
		@Override
		TypeAlterability alterable(MySQLColumn column, DataType type) {
			if (type == TextDBType.TYPE || type == NTextDBType.TYPE) {
				return Always;
			} else if (type instanceof CharsType) {
				return ExceedExist;
			}
			return ColumnNull;
		}
	},
	BINARY {
		@Override
		TypeAlterability alterable(MySQLColumn column, DataType type) {
			if (type instanceof BinDBType) {
				BinDBType bt = (BinDBType) type;
				if (bt.length >= column.length) {
					return Always;
				} else {
					return ExceedExist;
				}
			} else if (type == GUIDType.TYPE && column.length <= 16) {
				return Always;
			} else if (type == BlobDBType.TYPE) {
				return Always;
			}
			return ColumnNull;
		}
	},
	VARBINARY {
		@Override
		TypeAlterability alterable(MySQLColumn column, DataType type) {
			if (type instanceof BinDBType) {
				BinDBType bt = (BinDBType) type;
				if (bt.length >= column.length) {
					return Always;
				} else {
					return ExceedExist;
				}
			} else if (type == GUIDType.TYPE && column.length <= 16) {
				return Always;
			} else if (type == BlobDBType.TYPE) {
				return Always;
			}
			return ColumnNull;
		}
	},
	TINYBLOB {
		@Override
		TypeAlterability alterable(MySQLColumn column, DataType type) {
			if (type == GUIDType.TYPE) {
				return ExceedExist;
			} else if (type instanceof BinDBType) {
				BinDBType bt = (BinDBType) type;
				if (bt.length >= 255) {
					return Always;
				} else {
					return ExceedExist;
				}
			} else if (type == BlobDBType.TYPE) {
				return Always;
			}
			return ColumnNull;
		}
	},
	BLOB {
		@Override
		TypeAlterability alterable(MySQLColumn column, DataType type) {
			if (type instanceof BinDBType || type == GUIDType.TYPE) {
				return ExceedExist;
			} else if (type == BlobDBType.TYPE) {
				return Always;
			}
			return ColumnNull;
		}
	},
	MEDIUMBLOB {
		@Override
		TypeAlterability alterable(MySQLColumn column, DataType type) {
			if (type instanceof BinDBType || type == GUIDType.TYPE) {
				return ExceedExist;
			} else if (type == BlobDBType.TYPE) {
				return Always;
			}
			return ColumnNull;
		}
	},

	LONGBLOB {
		@Override
		TypeAlterability alterable(MySQLColumn column, DataType type) {
			if (type instanceof BinDBType || type == GUIDType.TYPE) {
				return ExceedExist;
			} else if (type == BlobDBType.TYPE) {
				return Always;
			}
			return ColumnNull;
		}
	},
	SET {
		@Override
		TypeAlterability alterable(MySQLColumn column, DataType type) {
			throw new UnsupportedOperationException();
		}
	},
	ENUM {
		@Override
		TypeAlterability alterable(MySQLColumn column, DataType type) {
			throw new UnsupportedOperationException();
		}
	};
	static final MySQLType parse(String type) {
		return valueOf(type.toUpperCase());
	}

	abstract TypeAlterability alterable(MySQLColumn column, DataType type);
}

final class MySQLColumn extends DbColumn<MySQLTable, MySQLColumn, MySQLIndex> {

	MySQLColumn(MySQLTable table, String name) {
		super(table, name);
	}

	MySQLType type;
	String charset;

	private static final boolean unsigned(ResultSet rs) throws SQLException {
		return rs.getString(2).indexOf("unsigned") > 0;
	}

	final void load(ResultSet rs) throws SQLException {
		this.type = MySQLType.parse(rs.getString(2));
		// for lob
		this.length = (int) rs.getLong(4);
		this.charset = rs.getString(5);
		this.precision = rs.getInt(6);
		this.scale = rs.getInt(7);
		if (unsigned(rs)) {
			throw new UnsupportedOperationException();
		}
		this.notNull = rs.getString(8).equals("NO");
		String defaultValue = rs.getString(9);
		if (!rs.wasNull()) {
			this.defaultVal = defaultValue;
		}
	}

	final boolean national() {
		if (this.charset.equals("utf8")) {
			return true;
		} else if (this.charset.equals("gbk")) {
			return false;
		}
		throw new UnsupportedOperationException("不支持的字符编码" + this.charset);
	}

	@Override
	final TypeAlterability typeAlterable(DataType type) {
		return this.type.alterable(this, type);
	}

	final void formatType(Appendable s) {

	}

}

final class MySQLIndex extends DbIndex<MySQLTable, MySQLColumn, MySQLIndex> {

	MySQLIndex(MySQLTable table, String name, boolean unique) {
		super(table, name, unique);
	}

	@Override
	final boolean isPrimaryKey() {
		return this.name.equalsIgnoreCase("PRIMARY");
	}

}