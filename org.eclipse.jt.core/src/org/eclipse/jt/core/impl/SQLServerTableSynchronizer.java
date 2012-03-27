package org.eclipse.jt.core.impl;

import static org.eclipse.jt.core.impl.TypeAlterability.Always;
import static org.eclipse.jt.core.impl.TypeAlterability.ExceedExist;
import static org.eclipse.jt.core.impl.TypeAlterability.Never;
import static org.eclipse.jt.core.impl.TypeCompatiblity.Exactly;
import static org.eclipse.jt.core.impl.TypeCompatiblity.NotSuggest;
import static org.eclipse.jt.core.impl.TypeCompatiblity.Overflow;
import static org.eclipse.jt.core.impl.TypeCompatiblity.Unable;

import java.io.IOException;
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
final class SQLServerTableSynchronizer
		extends
		TableSynchronizerImpl<SQLServerLang, SQLServerTable, SQLServerColumn, SQLServerIndex> {

	SQLServerTableSynchronizer(DBAdapterImpl adapter, SQLServerLang lang)
			throws SQLException {
		super(adapter, lang);
	}

	@Override
	final TableSync newTableSync() throws SQLException {
		return new SQLServerTableSync();
	}

	@Override
	final SQLServerColumnCompareSync newColumnSync() {
		return new SQLServerColumnCompareSync();
	}

	@Override
	final SQLServerIndexSync newIndexSync() throws SQLException {
		return new SQLServerIndexSync();
	}

	@Override
	final SQLServerHierarchySync newHierarchySync() {
		return new SQLServerHierarchySync();
	}

	@Override
	final SQLServerTable newCompareTable() {
		return new SQLServerTable();
	}

	private final class SQLServerTableSync extends TableSync {

		SQLServerTableSync() throws SQLException {
			super(NameCaseMode.CASE_INSENSITIVE_DISPLAY_SPECIFIC);
		}

		@Override
		final void initNamespace() throws SQLException {
			fillUsingSelect(this.adapter, this.namespace, SELECT_TABLE_NAME);
		}

		private static final String SELECT_TABLE_NAME = "select [name] from sys.tables";

		@Override
		final void dbCreateTable(DBTableDefineImpl define) throws SQLException {
			SqlBuilder sql = new SqlBuilder(this.lang);
			sql.append("create table ").appendId(define.namedb()).append(" (");
			sql.nNewline().pi();
			for (TableFieldDefineImpl field : define.owner.fields) {
				if (field.dbTable == define || field.isRECID()) {
					columnDefinition(sql, field);
					sql.nComma().nNewline();
				}
			}
			sql.appendConstraint().appendId(define.getPkeyName());
			sql.append(" primary key nonclustered ");
			sql.lp().appendId(define.owner.f_recid.namedb());
			sql.rp().nNewline();
			sql.ri().rp().nNewline();
			this.statement.execute(sql);
			// HCL partition
		}

	}

	private static final void columnDefinition(SqlBuilder sql,
			TableFieldDefineImpl field) {
		sql.appendId(field.namedb()).append(' ');
		sql.appendType(field.getType());
		if (field.isKeepValid()) {
			sql.append(" not null");
		}
		if (field.getDefault() != null) {
			sql.append(" default ");
			sql.append(defaultDefinition(field, defaultDeclare));
		}
	}

	private final class SQLServerColumnCompareSync extends ColumnCompareSync {

		static final int MOD_COLLATION = 1 << 10;

		@Override
		final void execute() throws SQLException {
			this.compare.fillWithIndexContainColumn(this.cache, this.drop);
			for (SQLServerColumn column : this.unuse.values()) {
				this.compare.fillWithIndexContainColumn(this.cache, column);
			}
			for (ColumnState state : this.modify.values()) {
				this.compare.fillWithIndexContainColumn(this.cache,
						state.column);
			}
			this.sync.indexSync.dbDropIndexes(this.cache);
			this.compare.removeIndex(this.cache);
			this.dbDropDefaultConstraints(this.drop);
			this.dbDropColumns();
			this.compare.removeColumnsCascadeIndex(this.drop);
			this.renameUnusedDbColumnThenAddDefineColumn();
			this.dbAddColumns();
			this.dbModifyDefaultConstraint();
			this.dbAlterColumns();
		}

		@Override
		final TypeCompatiblity typeCompatible(TableFieldDefineImpl field,
				SQLServerColumn column) {
			return field.getType().detect(compatible, column);
		}

		@Override
		final boolean defaultChanged(TableFieldDefineImpl field,
				SQLServerColumn column) {
			final ConstExpr c = field.getDefault();
			final boolean leftNull = c == null;
			final boolean rightNull = column.defaultVal == null;
			if (leftNull != rightNull
					|| (!leftNull && !rightNull && !column.defaultVal
							.equals(defaultDefinition(field, defaultCompare)))) {
				return true;
			}
			return false;
		}

		@Override
		protected final void handleMatched(TableFieldDefineImpl field,
				SQLServerColumn column) {
			super.handleMatched(field, column);
			// if
			// (!column.collation.equals(Arguments.SQLSERVER_DEFAULT_COLLATION))
			// {
			// this.modify(field, column).set(MOD_COLLATION);
			// }
		}

		@Override
		final void dbRenameColumnAndSetNotNullToNullable(
				SQLServerColumn column, String rename) throws SQLException {
			this.statement.execute("exec sp_rename '" + this.compare.name + '.'
					+ column + "', '" + rename + "', 'column'");
			if (column.notNull) {
				SqlBuilder sql = new SqlBuilder(this.lang);
				sql.append("alter table ").appendId(column.table.name);
				sql.append(" alter column ").append(column.name).append(' ');
				column.formatType(sql);
				sql.append(" null");
				this.statement.execute(sql);
			}
		}

		private final void dbModifyDefaultConstraint() throws SQLException {
			for (ColumnState state : this.modify.values()) {
				if (state.get(MOD_DEFAULT)) {
					if (state.column.defaultVal != null) {
						this.dbDropDefaultConstraint(state.column.defaultConstraint);
					}
					if (state.field.getDefault() != null) {
						this.dbAddDefaultConstraint(state.field);
					}
				}
			}
		}

		private final void dbDropDefaultConstraint(String constraint)
				throws SQLException {
			SqlBuilder sql = new SqlBuilder(this.lang);
			sql = new SqlBuilder(this.lang);
			sql.append("alter table ").appendId(this.compare.name);
			sql.nNewline().pi();
			sql.append("drop").nSpace();
			sql.append("constraint").nSpace();
			sql.appendId(constraint);
			this.statement.execute(sql);
		}

		private final void dbAddDefaultConstraint(TableFieldDefineImpl field)
				throws SQLException {
			SqlBuilder sql = new SqlBuilder(this.lang);
			sql.append("alter table ").appendId(this.compare.name).nNewline()
					.ri();
			sql.append("add").nSpace();
			sql.append("default").nSpace();
			sql.append(defaultDefinition(field, defaultDeclare)).nSpace();
			sql.append("for").nSpace().appendId(field.namedb());
			this.statement.execute(sql);
		}

		private final void dbDropDefaultConstraints(
				ArrayList<SQLServerColumn> columns) throws SQLException {
			if (columns != null && columns.size() > 0) {
				SqlBuilder sql = null;
				for (int i = 0, c = columns.size(); i < c; i++) {
					SQLServerColumn column = columns.get(i);
					if (column.defaultConstraint != null) {
						if (sql == null) {
							sql = new SqlBuilder(this.lang);
							sql.append("alter table ").appendId(
									this.compare.name);
							sql.nNewline().pi();
							sql.append("drop").nSpace();
						}
						sql.append("constraint").nSpace();
						sql.appendId(columns.get(i).defaultConstraint);
						sql.nComma();
					}
				}
				if (sql != null) {
					sql.uComma();
					this.statement.execute(sql);
				}
			}
		}

		private final void dbAddColumns() throws SQLException {
			if (this.add.size() > 0) {
				SqlBuilder sql = new SqlBuilder(this.lang);
				sql.append("alter table ").appendId(this.compare.name);
				sql.nNewline().pi();
				sql.append("add").nSpace();
				for (int i = 0, c = this.add.size(); i < c; i++) {
					TableFieldDefineImpl field = this.add.get(i);
					sql.appendId(field.namedb()).nSpace();
					sql.appendType(field.getType()).nSpace();
					if (field.isKeepValid()) {
						sql.append("not null");
						sql.nSpace();
					}
					if (field.getDefault() != null) {
						sql.appendDefault();
						sql.append(defaultDefinition(field, defaultDeclare));
						sql.nSpace();
						if (field.isKeepValid()) {
							sql.append("with values");
						}
						sql.nSpace();
					}
					sql.nComma().nNewline();
				}
				sql.uComma();
				this.statement.execute(sql);
				this.add.clear();
			}
		}

		private final void dbAlterColumns() throws SQLException {
			if (this.modify.size() == 0) {
				return;
			}
			SqlBuilder sql = null;
			for (ColumnState state : this.modify.values()) {
				TableFieldDefineImpl field = state.field;
				final boolean type = state.get(MOD_TYPE);
				final boolean nullable = state.get(MOD_NULLABLE);
				final boolean collation = state.get(MOD_COLLATION);
				if (type || nullable || collation) {
					if (sql == null) {
						sql = new SqlBuilder(this.lang);
						sql.append("alter table ").appendId(
								this.define.namedb());
						sql.nNewline().pi();
					}
					sql.append("alter column ");
					sql.appendId(field.namedb());
					sql.nSpace();
					sql.appendType(field.getType());
					if (nullable) {
						sql.append(field.isKeepValid() ? " not null" : " null");
					}
					if (collation) {
						// TODO
					}
					sql.nNewline().ri();
				}
			}
			if (sql != null) {
				this.statement.execute(sql);
			}
		}

		private final void dbDropColumns() throws SQLException {
			for (int i = 0, c = this.drop.size(); i < c; i++) {
				SqlBuilder sql = new SqlBuilder(this.lang);
				sql.appendAlter().appendTable().appendId(this.compare.name);
				sql.nNewline().pi();
				sql.appendDrop().appendColumn().appendId(this.drop.get(i).name);
				this.statement.execute(sql);
			}
		}

	}

	private final class SQLServerIndexSync extends IndexSync {

		private SQLServerIndexSync() throws SQLException {
			super();
		}

		@Override
		final void initNamespace() throws SQLException {
		}

		@Override
		final void ensureValid(final IndexDefineImpl index,
				final SQLServerTable dbTable) {
			this.ensureValieWithinTable(index, dbTable);
		}

		@Override
		final void dbDropIndex(SQLServerIndex index) throws SQLException {
			SqlBuilder sql = new SqlBuilder(this.lang);
			sql.append("drop index ");
			sql.appendId(index.table.name);
			sql.append('.');
			sql.appendId(index.name);
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
		final void notifyNsBeforeDropColumn(SQLServerTable table,
				ArrayList<SQLServerColumn> columns) throws SQLException {
		}
	}

	private final class SQLServerHierarchySync extends HierarchySync {

		@Override
		final HierarchyState detectState(HierarchyDefineImpl hierarchy)
				throws SQLException {
			// TODO
			// if (hierarchy.tableName() == null) {
			// return CREATE_NEW;
			// } else if (!this.tables.contains(hierarchy.tableName())) {
			// return CREATE_NEW;
			// }
			// PreparedStatement ps = this.adapter
			// .prepareStatement(SELECT_TABLE_COLUMNS);
			// try {
			// ps.setString(1, hierarchy.tableName());
			// ResultSet rs = ps.executeQuery();
			// try {
			// int length = 0;
			// if (rs.next()) {
			// if (!rs.getString(1).equals(
			// HierarchyDefineImpl.COLUMN_NAME_RECID)) {
			// return CREATE_NEW;
			// } else if (rs.getInt(2) != BINARY) {
			// return CREATE_NEW;
			// } else if (rs.getInt(3) != 16) {
			// return CREATE_NEW;
			// }
			// } else {
			// // unreachable
			// return CREATE_NEW;
			// }
			// if (rs.next()) {
			// if (!rs.getString(1).equals(
			// HierarchyDefineImpl.COLUMN_NAME_PATH)) {
			// return CREATE_NEW;
			// } else if (rs.getInt(2) != VARBINARY) {
			// return CREATE_NEW;
			// }
			// length = rs.getInt(3);
			// } else {
			// return CREATE_NEW;
			// }
			// if (rs.next()) {
			// if (!rs.getString(1).equals(
			// HierarchyDefineImpl.COLUMN_NAME_STATUS)) {
			// return CREATE_NEW;
			// } else if (rs.getInt(2) != INT) {
			// return CREATE_NEW;
			// }
			// } else {
			// return CREATE_NEW;
			// }
			// if (rs.next()) {
			// return CREATE_NEW;
			// }
			// if (length < hierarchy.getPathLength()) {
			// return EXTEND_PATH;
			// }
			// return DO_NOTHING;
			// } finally {
			// rs.close();
			// }
			// } finally {
			// ps.close();
			// }
			return null;
		}

		@Override
		final void createHierarchyTable(HierarchyDefineImpl hierarchy)
				throws SQLException {
			SqlBuilder sql = new SqlBuilder(this.lang);
			sql.append("create table ").appendId(hierarchy.tableName())
					.append("(");
			sql.nNewline().pi();
			sql.appendId(HierarchyDefineImpl.COLUMN_NAME_RECID).nSpace()
					.appendType(GUIDType.TYPE).nSpace()
					.append("primary key nonclustered").nComma().nNewline();
			sql.appendId(HierarchyDefineImpl.COLUMN_NAME_PATH).nSpace()
					.appendType(TypeFactory.VARBINARY(1)).nComma().nNewline();
			sql.appendId(HierarchyDefineImpl.COLUMN_NAME_STATUS).nSpace()
					.appendType(TypeFactory.INT).appendDefault().append("1")
					.nNewline();
			sql.ri().append(");").nNewline();
			this.statement.execute(sql);
		}

		@Override
		final void extendPath(HierarchyDefineImpl hierarchy)
				throws SQLException {
			SqlBuilder sql = new SqlBuilder(this.lang);
			sql.append("alter table ").appendId(hierarchy.tableName())
					.append(" alter column ");
			sql.appendId(HierarchyDefineImpl.COLUMN_NAME_PATH).nSpace();
			sql.appendType(TypeFactory.VARBINARY(hierarchy.getPathLength()));
			this.statement.execute(sql);
		}

	}

	private static final TypeDetector<TypeCompatiblity, SQLServerColumn> compatible = new TypeDetectorBase<TypeCompatiblity, SQLServerColumn>() {

		@Override
		public TypeCompatiblity inBoolean(SQLServerColumn column)
				throws Throwable {
			if (column.type == SQLServerType.BIT) {
				return Exactly;
			} else if (column.type == SQLServerType.TINYINT
					|| column.type == SQLServerType.SMALLINT
					|| column.type == SQLServerType.INT
					|| column.type == SQLServerType.BIGINT) {
				return Overflow;
			} else if ((column.type == SQLServerType.NUMERIC || column.type == SQLServerType.DECIMAL)
					&& column.scale == 0) {
				return NotSuggest;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inShort(SQLServerColumn column)
				throws Throwable {
			if (column.type == SQLServerType.SMALLINT) {
				return Exactly;
			} else if (column.type == SQLServerType.INT
					|| column.type == SQLServerType.BIGINT) {
				return Overflow;
			}
			if ((column.type == SQLServerType.NUMERIC || column.type == SQLServerType.DECIMAL)
					&& column.precision >= 5 && column.scale == 0) {
				return NotSuggest;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inInt(SQLServerColumn column) throws Throwable {
			if (column.type == SQLServerType.INT) {
				return Exactly;
			} else if (column.type == SQLServerType.BIGINT) {
				return Overflow;
			} else if ((column.type == SQLServerType.NUMERIC || column.type == SQLServerType.DECIMAL)
					&& column.precision >= 10 && column.scale == 0) {
				return NotSuggest;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inLong(SQLServerColumn column) throws Throwable {
			if (column.type == SQLServerType.BIGINT) {
				return Exactly;
			} else if ((column.type == SQLServerType.NUMERIC || column.type == SQLServerType.DECIMAL)
					&& column.precision - column.scale >= 19
					&& column.scale == 0) {
				return NotSuggest;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inDate(SQLServerColumn column) throws Throwable {
			if (column.type == SQLServerType.DATETIME) {
				return Exactly;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inFloat(SQLServerColumn column)
				throws Throwable {
			if (column.type == SQLServerType.REAL) {
				return Exactly;
			} else if (column.type == SQLServerType.FLOAT) {
				return Overflow;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inDouble(SQLServerColumn column)
				throws Throwable {
			if (column.type == SQLServerType.FLOAT) {
				return Exactly;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inNumeric(SQLServerColumn column,
				int precision, int scale) throws Throwable {
			if (column.type == SQLServerType.NUMERIC
					|| column.type == SQLServerType.DECIMAL) {
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
		public TypeCompatiblity inChar(SQLServerColumn column,
				SequenceDataType type) throws Throwable {
			final int length = type.getMaxLength();
			if (column.type == SQLServerType.CHAR) {
				if (column.length == length) {
					return Exactly;
				} else if (column.length > length) {
					return NotSuggest;
				}
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inVarChar(SQLServerColumn column,
				SequenceDataType type) throws Throwable {
			final int length = type.getMaxLength();
			if (column.type == SQLServerType.VARCHAR) {
				if (column.length == length) {
					return Exactly;
				} else if (column.length > length) {
					return Overflow;
				}
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inText(SQLServerColumn column) throws Throwable {
			if (column.type == SQLServerType.TEXT
					|| (column.type == SQLServerType.VARCHAR && column.length == -1)) {
				return Exactly;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inNChar(SQLServerColumn column,
				SequenceDataType type) throws Throwable {
			final int length = type.getMaxLength();
			if (column.type == SQLServerType.NCHAR) {
				if (column.length == length) {
					return Exactly;
				} else if (column.length > length) {
					return NotSuggest;
				}
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inNVarChar(SQLServerColumn column,
				SequenceDataType type) throws Throwable {
			final int length = type.getMaxLength();
			if (column.type == SQLServerType.NVARCHAR) {
				if (column.length == length) {
					return Exactly;
				} else if (column.length > length) {
					return Overflow;
				}
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inNText(SQLServerColumn column)
				throws Throwable {
			if (column.type == SQLServerType.NTEXT
					|| (column.type == SQLServerType.NVARCHAR && column.length == -1)) {
				return Exactly;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inBinary(SQLServerColumn column,
				SequenceDataType type) throws Throwable {
			final int length = type.getMaxLength();
			if (column.type == SQLServerType.BINARY) {
				if (column.length == length) {
					return Exactly;
				} else if (column.length > length) {
					return NotSuggest;
				}
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inVarBinary(SQLServerColumn column,
				SequenceDataType type) throws Throwable {
			final int length = type.getMaxLength();
			if (column.type == SQLServerType.VARBINARY) {
				if (column.length == length) {
					return Exactly;
				} else if (column.length > length) {
					return Overflow;
				}
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inBlob(SQLServerColumn column) throws Throwable {
			if (column.type == SQLServerType.IMAGE
					|| (column.type == SQLServerType.VARBINARY && column.length == -1)) {
				return Exactly;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inGUID(SQLServerColumn column) throws Throwable {
			if (column.type == SQLServerType.BINARY && column.length == 16) {
				return Exactly;
			} else if (column.type == SQLServerType.VARBINARY
					&& column.length >= 16) {
				return NotSuggest;
			}
			return Unable;
		}

	};

	private static final ConstFormatter defaultDeclare = new ConstFormatter() {

		@Override
		public String inDate(ConstExpr c) throws Throwable {
			return "\'"
					+ DateParser.format(c.getDate(),
							DateParser.FORMAT_DATE_TIME_MS) + "\'";
		}

		@Override
		String bytes(byte[] value) {
			return SQLServerTableSynchronizer.format(value);
		}
	};

	private static final ConstFormatter defaultCompare = new ConstFormatter() {

		@Override
		public String inBoolean(ConstExpr c) throws Throwable {
			return "((" + super.inBoolean(c) + "))";
		}

		@Override
		public String inByte(ConstExpr c) throws Throwable {
			return "((" + super.inByte(c) + "))";
		}

		@Override
		public String inShort(ConstExpr c) throws Throwable {
			return "((" + super.inShort(c) + "))";
		}

		@Override
		public String inInt(ConstExpr c) throws Throwable {
			return "((" + super.inInt(c) + "))";
		}

		@Override
		public String inLong(ConstExpr c) throws Throwable {
			return "((" + super.inLong(c) + "))";
		}

		@Override
		public String inFloat(ConstExpr c) throws Throwable {
			return "((" + super.inFloat(c) + "))";
		}

		@Override
		public String inDouble(ConstExpr c) throws Throwable {
			return "((" + super.inDouble(c) + "))";
		}

		@Override
		public String inString(ConstExpr c, SequenceDataType type)
				throws Throwable {
			return "(" + super.inString(c, type) + ")";
		}

		@Override
		public String inBytes(ConstExpr c, SequenceDataType type)
				throws Throwable {
			return "(" + this.bytes(c.getBytes()) + ")";
		}

		@Override
		public String inGUID(ConstExpr c) throws Throwable {
			return "(" + super.inGUID(c) + ")";
		}

		@Override
		public String inDate(ConstExpr c) throws Throwable {
			return "(\'"
					+ DateParser.format(c.getDate(),
							DateParser.FORMAT_DATE_TIME_MS) + "\')";
		}

		@Override
		String bytes(byte[] value) {
			return SQLServerTableSynchronizer.format(value);
		}

	};

	private static final String format(byte[] value) {
		return "0x" + Convert.bytesToHex(value, false, false);
	}

}

final class SQLServerTable extends
		DbTable<SQLServerTable, SQLServerColumn, SQLServerIndex> {

	@Override
	final void loadColumn(DBAdapterImpl adapter) throws SQLException {
		this.loadUsingJdbc(adapter);
		if (this.columns.size() == 0) {
			throw tableNotExists(this.name);
		}
	}

	@Override
	final SQLServerColumn newColumnOnly(String name) {
		return new SQLServerColumn(this, name);
	}

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
	 * <li>name 列名
	 * <li>type_name 类型的名称
	 * <li>max_length 数据长度
	 * <li>precision 数据精度
	 * <li>scale 小数位
	 * <li>not_null 是否不为空
	 * <li>definition 默认值
	 * <li>default_name 默认值约束的名称
	 * </ol>
	 */
	private static final String SELECT_TABLE_COLUMNS = "select c.name, t.name, c.max_length, c.precision, c.scale, case c.is_nullable when 0 then 1 else 0 end, d.definition, d.name from sys.columns c inner join sys.types t on c.user_type_id = t.user_type_id inner join sys.tables o on c.object_id = o.object_id left join sys.default_constraints d on c.default_object_id = d.object_id where o.name = ?";

	private static final String type(ResultSet rs) throws SQLException {
		return rs.getString(2);
	}

	private static final int length(ResultSet rs) throws SQLException {
		return rs.getInt(3);
	}

	private static final int precision(ResultSet rs) throws SQLException {
		return rs.getInt(4);
	}

	private static final int scale(ResultSet rs) throws SQLException {
		return rs.getInt(5);
	}

	final void loadUsingSysview(DBAdapterImpl adapter) throws SQLException {
		PreparedStatement ps = adapter.prepareStatement(SELECT_TABLE_COLUMNS);
		try {
			ps.setString(1, this.name);
			ResultSet rs = ps.executeQuery();
			try {
				while (rs.next()) {
					SQLServerColumn column = this.addColumn(rs.getString(1));
					column.type = SQLServerType.typeOf(type(rs));
					column.length = length(rs);
					column.precision = precision(rs);
					column.scale = scale(rs);
					column.notNull = rs.getBoolean(6);
					String defaultDefinition = rs.getString(7);
					if (!rs.wasNull() && defaultDefinition != null) {
						column.defaultVal = defaultDefinition.substring(1,
								defaultDefinition.length() - 1);
						column.defaultConstraint = rs.getString(8);
					}
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

	final void loadUsingJdbc(DBAdapterImpl adapter) throws SQLException {
		this.columns.clear();
		this.columnMap.clear();
		ResultSet rs = adapter.getMetaData().getColumns(adapter.getCatalog(),
				null, this.name, null);
		try {
			while (rs.next()) {
				final String name = rs.getString(4);
				SQLServerColumn column = this.addColumn(name);
				final String type = rs.getString(6);
				column.type = SQLServerType.typeOf(type);
				final int length = rs.getInt(7);
				column.length = column.precision = length;
				column.scale = rs.getInt(9);
				String is_nullable = rs.getString(18).trim();
				column.notNull = is_nullable.equals("NO");
				String defaultDefinition = rs.getString(13);
				if (!rs.wasNull() && defaultDefinition != null) {
					column.defaultVal = defaultDefinition;
					column.defaultConstraint = this.defaultConstraintNameOf(
							adapter, column.name);
				}
			}
		} finally {
			rs.close();
		}
	}

	private static final String SELECT_DEFAULT_CONSTRAINT_NAME = "select object_name(c.constid) from sysobjects o join sysconstraints c on o.id = c.id where c.status & 5 = 5 and o.name = ? and col_name(o.id, c.colid) = ?";

	private final String defaultConstraintNameOf(DBAdapterImpl adapter,
			String column) throws SQLException {
		PreparedStatement ps = adapter
				.prepareStatement(SELECT_DEFAULT_CONSTRAINT_NAME);
		try {
			ps.setString(1, this.name);
			ps.setString(2, column);
			ResultSet rs = ps.executeQuery();
			try {
				if (rs.next()) {
					return rs.getString(1);
				} else {
					throw new IllegalStateException();
				}
			} finally {
				rs.close();
			}
		} finally {
			adapter.freeStatement(ps);
		}
	}

	/**
	 * 从sqlserver2005及以上版本的数据库中读取指定表的索引结构语句
	 * 
	 * <ol>
	 * <li>INDEX_NAME 索引名称
	 * <li>IS_UNIQUE 是否唯一
	 * <li>INDEX_COLUMN 索引列
	 * <li>IS_DESC 是否降序
	 * <li>IS_PRIMARYKEY 是否主键索引
	 * </ol>
	 */
	private static final String SELECT_INDEX_COLUMNS = "select INDEX_NAME = i.name, IS_UNIQUE = i.is_unique, INDEX_COLUMN = index_col(t.name, i.index_id, ic.key_ordinal), IS_DESC = ic.is_descending_key, IS_PRIMARY_KEY = is_primary_key from sys.indexes i inner join sys.tables t on t.object_id = i.object_id inner join sys.index_columns ic on t.object_id = ic.object_id and i.index_id = ic.index_id where t.name = ? order by ic.index_id, ic.key_ordinal";

	@Override
	final void loadIndex(DBAdapterImpl adapter) throws SQLException {
		PreparedStatement ps = adapter.prepareStatement(SELECT_INDEX_COLUMNS);
		try {
			ps.setString(1, this.name);
			ResultSet rs = ps.executeQuery();
			try {
				while (rs.next()) {
					final String indexName = rs.getString(1);
					final boolean desc = rs.getBoolean(4);
					final String columnName = rs.getString(3);
					SQLServerIndex index = this.findIndex(indexName);
					if (index == null) {
						final boolean unique = rs.getBoolean(2);
						index = this.addIndex(indexName, unique);
						if (rs.getBoolean(5)) {
							this.primary = index;
						}
					}
					SQLServerColumn column = this.getColumn(columnName);
					index.add(column, desc);
					// this.columnWasIndexed(column, index);
				}
			} finally {
				rs.close();
			}
		} finally {
			adapter.freeStatement(ps);
		}
	}

	@Override
	final SQLServerIndex newIndexOnly(String name, boolean unique) {
		return new SQLServerIndex(this, name, unique);
	}

	@Override
	final void checkEmptyStatus(DBAdapterImpl adapter) throws SQLException {
		this.tableEmpty = !exists(adapter, "select top 1 1 from "
				+ this.name);
	}

}

enum SQLServerType {

	BIT {

		@Override
		TypeAlterability alterable(SQLServerColumn column, DataType type) {
			if (type == BooleanType.TYPE || type == ShortType.TYPE
					|| type == IntType.TYPE || type == LongType.TYPE
					|| type == FloatType.TYPE || type == DoubleType.TYPE
					|| type instanceof NumericDBType) {
				return Always;
			}
			return Never;
		}

		@Override
		final void formatType(SQLServerColumn column, Appendable s)
				throws IOException {
			s.append("[bit]");
		}

	},

	TINYINT {

		@Override
		TypeAlterability alterable(SQLServerColumn column, DataType type) {
			if (type == ShortType.TYPE || type == IntType.TYPE
					|| type == LongType.TYPE || type == FloatType.TYPE
					|| type == DoubleType.TYPE) {
				return Always;
			} else if (type instanceof NumericDBType) {
				NumericDBType nt = (NumericDBType) type;
				if (nt.precision - nt.scale >= 3) {
					return Always;
				}
			}
			return Never;
		}

		@Override
		final void formatType(SQLServerColumn column, Appendable s)
				throws IOException {
			s.append("[tinyint]");
		}
	},

	SMALLINT {

		@Override
		TypeAlterability alterable(SQLServerColumn column, DataType type) {
			if (type == ShortType.TYPE || type == IntType.TYPE
					|| type == LongType.TYPE || type == FloatType.TYPE
					|| type == DoubleType.TYPE) {
				return Always;
			} else if (type instanceof NumericDBType) {
				NumericDBType nt = (NumericDBType) type;
				if (nt.precision - nt.scale >= 5) {
					return Always;
				}
			}
			return Never;
		}

		@Override
		final void formatType(SQLServerColumn column, Appendable s)
				throws IOException {
			s.append("[smallint]");
		}
	},

	INT {

		@Override
		TypeAlterability alterable(SQLServerColumn column, DataType type) {
			if (type == ShortType.TYPE) {
				return ExceedExist;
			} else if (type == IntType.TYPE || type == LongType.TYPE
					|| type == FloatType.TYPE || type == DoubleType.TYPE) {
				return Always;
			} else if (type instanceof NumericDBType) {
				NumericDBType nt = (NumericDBType) type;
				if (nt.precision - nt.scale >= 10) {
					return Always;
				}
			}
			return Never;
		}

		@Override
		final void formatType(SQLServerColumn column, Appendable s)
				throws IOException {
			s.append("[int]");
		}
	},

	BIGINT {

		@Override
		TypeAlterability alterable(SQLServerColumn column, DataType type) {
			if (type == ShortType.TYPE || type == IntType.TYPE) {
				return ExceedExist;
			} else if (type == LongType.TYPE || type == FloatType.TYPE
					|| type == DoubleType.TYPE) {
				return Always;
			} else if (type instanceof NumericDBType) {
				NumericDBType nt = (NumericDBType) type;
				if (nt.precision - nt.scale >= 19) {
					return Always;
				}
			}
			return Never;
		}

		@Override
		final void formatType(SQLServerColumn column, Appendable s)
				throws IOException {
			s.append("[bigint]");
		}
	},

	REAL {

		@Override
		TypeAlterability alterable(SQLServerColumn column, DataType type) {
			if (type == ShortType.TYPE || type == IntType.TYPE
					|| type == LongType.TYPE) {
				return ExceedExist;
			} else if (type == FloatType.TYPE || type == DoubleType.TYPE) {
				return Always;
			}
			return Never;
		}

		@Override
		final void formatType(SQLServerColumn column, Appendable s)
				throws IOException {
			s.append("[real]");
		}
	},

	FLOAT {

		@Override
		TypeAlterability alterable(SQLServerColumn column, DataType type) {
			if (type == ShortType.TYPE || type == IntType.TYPE
					|| type == LongType.TYPE) {
				return ExceedExist;
			} else if (type == FloatType.TYPE || type == DoubleType.TYPE) {
				return Always;
			}
			return Never;
		}

		@Override
		final void formatType(SQLServerColumn column, Appendable s)
				throws IOException {
			s.append("[float]");
		}
	},

	NUMERIC {

		@Override
		TypeAlterability alterable(SQLServerColumn column, DataType type) {
			if (type == ShortType.TYPE || type == IntType.TYPE
					|| type == LongType.TYPE) {
				return ExceedExist;
			} else if (type == FloatType.TYPE || type == DoubleType.TYPE) {
				return Always;
			} else if (type instanceof NumericDBType) {
				NumericDBType nt = (NumericDBType) type;
				if (nt.precision - nt.scale >= column.precision - column.scale
						&& nt.scale >= column.scale) {
					return Always;
				}
			}
			return Never;
		}

		@Override
		final void formatType(SQLServerColumn column, Appendable s)
				throws IOException {
			s.append("[numeric(").append(Integer.toString(column.precision));
			s.append('.').append(Integer.toString(column.scale)).append(")]");
		}
	},

	DECIMAL {

		@Override
		TypeAlterability alterable(SQLServerColumn column, DataType type) {
			return NUMERIC.alterable(column, type);
		}

		@Override
		final void formatType(SQLServerColumn column, Appendable s)
				throws IOException {
			s.append("[decimal(").append(Integer.toString(column.precision));
			s.append('.').append(Integer.toString(column.scale)).append(")]");
		}
	},

	SMALLMONEY {

		@Override
		TypeAlterability alterable(SQLServerColumn column, DataType type) {
			throw new UnsupportedOperationException();
		}

		@Override
		final void formatType(SQLServerColumn column, Appendable s)
				throws IOException {
			s.append("[smallmoney]");
		}
	},

	MONEY {

		@Override
		TypeAlterability alterable(SQLServerColumn column, DataType type) {
			throw new UnsupportedOperationException();
		}

		@Override
		final void formatType(SQLServerColumn column, Appendable s)
				throws IOException {
			s.append("[money]");
		}
	},

	CHAR {

		@Override
		TypeAlterability alterable(SQLServerColumn column, DataType type) {
			if (type instanceof CharsType) {
				CharsType ct = (CharsType) type;
				if (ct.length >= column.length) {
					return Always;
				} else {
					return ExceedExist;
				}
			} else if (type == TextDBType.TYPE || type == NTextDBType.TYPE) {
				return Always;
			}
			return Never;
		}

		@Override
		final void formatType(SQLServerColumn column, Appendable s)
				throws IOException {
			s.append("[char(").append(Integer.toString(column.length))
					.append(")]");
		}
	},

	VARCHAR {

		@Override
		TypeAlterability alterable(SQLServerColumn column, DataType type) {
			if (type instanceof CharsType) {
				CharsType ct = (CharsType) type;
				if (ct.length >= column.length) {
					return Always;
				} else {
					return ExceedExist;
				}
			} else if (type == TextDBType.TYPE || type == NTextDBType.TYPE) {
				return Always;
			}
			return Never;
		}

		@Override
		final void formatType(SQLServerColumn column, Appendable s)
				throws IOException {
			s.append("[varcar(").append(Integer.toString(column.length))
					.append(")]");
		}
	},

	TEXT {

		@Override
		TypeAlterability alterable(SQLServerColumn column, DataType type) {
			if (type instanceof CharsType) {
				return ExceedExist;
			} else if (type == TextDBType.TYPE || type == NTextDBType.TYPE) {
				return Always;
			}
			return Never;
		}

		@Override
		final void formatType(SQLServerColumn column, Appendable s)
				throws IOException {
			s.append("[varchar(max)]");
		}
	},

	NCHAR {

		@Override
		TypeAlterability alterable(SQLServerColumn column, DataType type) {
			if (type instanceof CharsType) {
				CharsType ct = (CharsType) type;
				if (ct.length >= 2 * column.length) {
					return Always;
				} else {
					return ExceedExist;
				}
			} else if (type == TextDBType.TYPE || type == NTextDBType.TYPE) {
				return Always;
			}
			return Never;
		}

		@Override
		final void formatType(SQLServerColumn column, Appendable s)
				throws IOException {
			s.append("[nchar(").append(Integer.toString(column.length))
					.append(")]");
		}
	},

	NVARCHAR {

		@Override
		TypeAlterability alterable(SQLServerColumn column, DataType type) {
			if (type instanceof CharsType) {
				CharsType ct = (CharsType) type;
				if (ct.length >= 2 * column.length) {
					return Always;
				} else {
					return ExceedExist;
				}
			} else if (type == TextDBType.TYPE || type == NTextDBType.TYPE) {
				return Always;
			}
			return Never;
		}

		@Override
		final void formatType(SQLServerColumn column, Appendable s)
				throws IOException {
			s.append("[nvarchar(").append(Integer.toString(column.length))
					.append(")]");
		}
	},

	NTEXT {

		@Override
		TypeAlterability alterable(SQLServerColumn column, DataType type) {
			if (type instanceof CharsType) {
				return ExceedExist;
			} else if (type == TextDBType.TYPE || type == NTextDBType.TYPE) {
				return Always;
			}
			return Never;
		}

		@Override
		final void formatType(SQLServerColumn column, Appendable s)
				throws IOException {
			s.append("[next(max)]");
		}
	},

	BINARY {

		@Override
		TypeAlterability alterable(SQLServerColumn column, DataType type) {
			if (type instanceof BinDBType) {
				BinDBType bt = (BinDBType) type;
				if (bt.length >= column.length) {
					return Always;
				} else {
					return ExceedExist;
				}
			} else if (type == BlobDBType.TYPE) {
				return Always;
			}
			return Never;
		}

		@Override
		final void formatType(SQLServerColumn column, Appendable s)
				throws IOException {
			s.append("[binary(").append(Integer.toString(column.length))
					.append(")]");
		}
	},

	VARBINARY {

		@Override
		TypeAlterability alterable(SQLServerColumn column, DataType type) {
			if (type instanceof BinDBType) {
				BinDBType bt = (BinDBType) type;
				if (bt.length >= column.length) {
					return Always;
				} else {
					return ExceedExist;
				}
			} else if (type == BlobDBType.TYPE) {
				return Always;
			}
			return Never;
		}

		@Override
		final void formatType(SQLServerColumn column, Appendable s)
				throws IOException {
			s.append("[varbinary(").append(Integer.toString(column.length))
					.append(")]");
		}
	},

	IMAGE {

		@Override
		TypeAlterability alterable(SQLServerColumn column, DataType type) {
			if (type instanceof BinDBType) {
				return ExceedExist;
			} else if (type == BlobDBType.TYPE) {
				return Always;
			}
			return Never;
		}

		@Override
		final void formatType(SQLServerColumn column, Appendable s)
				throws IOException {
			s.append("[varbinary(max)]");
		}
	},

	SMALLDATETIME {

		@Override
		TypeAlterability alterable(SQLServerColumn column, DataType type) {
			if (type == DateType.TYPE) {
				return Always;
			}
			return Never;
		}

		@Override
		final void formatType(SQLServerColumn column, Appendable s)
				throws IOException {
			s.append("[smalldatetime]");
		}
	},

	DATETIME {

		@Override
		TypeAlterability alterable(SQLServerColumn column, DataType type) {
			if (type == DateType.TYPE) {
				return Always;
			}
			return Never;
		}

		@Override
		final void formatType(SQLServerColumn column, Appendable s)
				throws IOException {
			s.append("[datetime]");
		}
	},

	TIMESTAMP {

		@Override
		TypeAlterability alterable(SQLServerColumn column, DataType type) {
			throw new UnsupportedOperationException();
		}

		@Override
		final void formatType(SQLServerColumn column, Appendable s)
				throws IOException {
			s.append("[timestamp]");
		}
	},
	UNIQUEIDENTIFIER {

		@Override
		TypeAlterability alterable(SQLServerColumn column, DataType type) {
			throw new UnsupportedOperationException();
		}

		@Override
		final void formatType(SQLServerColumn column, Appendable s)
				throws IOException {
			s.append("[uniqueidentifier]");
		}
	},
	SQL_VARIANT {

		@Override
		TypeAlterability alterable(SQLServerColumn column, DataType type) {
			throw new UnsupportedOperationException();
		}

		@Override
		final void formatType(SQLServerColumn column, Appendable s)
				throws IOException {
			throw new UnsupportedOperationException();
		}
	},
	XML {

		@Override
		TypeAlterability alterable(SQLServerColumn column, DataType type) {
			throw new UnsupportedOperationException();
		}

		@Override
		final void formatType(SQLServerColumn column, Appendable s)
				throws IOException {
			throw new UnsupportedOperationException();
		}
	},
	SYSNAME {

		@Override
		TypeAlterability alterable(SQLServerColumn column, DataType type) {
			throw new UnsupportedOperationException();
		}

		@Override
		final void formatType(SQLServerColumn column, Appendable s)
				throws IOException {
			throw new UnsupportedOperationException();
		}
	};
	static final SQLServerType typeOf(String type) {
		return valueOf(type.toUpperCase());
	}

	abstract TypeAlterability alterable(SQLServerColumn column, DataType type);

	abstract void formatType(SQLServerColumn column, Appendable s)
			throws IOException;
}

final class SQLServerColumn extends
		DbColumn<SQLServerTable, SQLServerColumn, SQLServerIndex> {

	SQLServerColumn(SQLServerTable table, String name) {
		super(table, name);
	}

	SQLServerType type;
	String defaultConstraint;
	String collation;

	@Override
	final TypeAlterability typeAlterable(DataType type) {
		return this.type.alterable(this, type);
	}

	final void formatType(Appendable s) {
		try {
			this.type.formatType(this, s);
		} catch (IOException e) {
			throw Utils.tryThrowException(e);
		}
	}

}

final class SQLServerIndex extends
		DbIndex<SQLServerTable, SQLServerColumn, SQLServerIndex> {

	SQLServerIndex(SQLServerTable table, String name, boolean unique) {
		super(table, name, unique);
	}

}