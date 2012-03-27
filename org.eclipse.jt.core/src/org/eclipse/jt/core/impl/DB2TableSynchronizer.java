package org.eclipse.jt.core.impl;

import static org.eclipse.jt.core.impl.TypeAlterability.Always;
import static org.eclipse.jt.core.impl.TypeAlterability.Never;
import static org.eclipse.jt.core.impl.TypeCompatiblity.Exactly;
import static org.eclipse.jt.core.impl.TypeCompatiblity.NotSuggest;
import static org.eclipse.jt.core.impl.TypeCompatiblity.Overflow;
import static org.eclipse.jt.core.impl.TypeCompatiblity.Unable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;

import org.eclipse.jt.core.type.Convert;
import org.eclipse.jt.core.type.DataType;
import org.eclipse.jt.core.type.DateParser;
import org.eclipse.jt.core.type.SequenceDataType;
import org.eclipse.jt.core.type.TypeDetector;
import org.eclipse.jt.core.type.TypeDetectorBase;


/**
 * @author Jeff Tang
 * 
 */
final class DB2TableSynchronizer extends
		TableSynchronizerImpl<DB2Lang, DB2Table, DB2Column, DB2Index> {

	DB2TableSynchronizer(DBAdapterImpl adapter, DB2Lang lang)
			throws SQLException {
		super(adapter, lang);
	}

	@Override
	final DB2TableSync newTableSync() throws SQLException {
		return new DB2TableSync();
	}

	@Override
	final DB2ColumnCompareSync newColumnSync() {
		return new DB2ColumnCompareSync();
	}

	@Override
	final DB2IndexSync newIndexSync() throws SQLException {
		return new DB2IndexSync();
	}

	@Override
	DB2HierarchySync newHierarchySync() {
		return new DB2HierarchySync();
	}

	@Override
	protected final DB2Table newCompareTable() {
		return new DB2Table();
	}

	private final class DB2TableSync extends TableSync {

		DB2TableSync() throws SQLException {
			super(NameCaseMode.CASE_SENSITIVE);
		}

		@Override
		final void initNamespace() throws SQLException {
			PreparedStatement ps = this.adapter
					.prepareStatement(SELECT_TABLE_NAME);
			try {
				ps.setString(1, this.adapter.getDefaultSchema());
				fillUsingStatement(this.namespace, ps);
			} finally {
				this.adapter.freeStatement(ps);
			}
		}

		private static final String SELECT_TABLE_NAME = "select name from sysibm.systables where type = 'T' and creator = ?";

		@Override
		final void dbCreateTable(DBTableDefineImpl define) throws SQLException {
			SqlBuilder sql = new SqlBuilder(this.lang);
			sql.appendCreate().appendTable().appendId(define.namedb()).lp();
			sql.nNewline().pi();
			for (int i = 0, c = define.owner.fields.size(); i < c; i++) {
				TableFieldDefineImpl field = define.owner.fields.get(i);
				if (field.dbTable == define || field.isRECID()) {
					columnDefinition(sql, field);
					sql.nComma().nNewline();
				}
			}
			outlineRecidConstraint(sql, define);
			sql.nNewline().ri().rp();
			// HCL partition
			this.statement.execute(sql);
		}

	}

	private final class DB2ColumnCompareSync extends ColumnCompareSync {

		@Override
		final void execute() throws SQLException {
			this.sync.indexSync.notifyNsBeforeDropColumn(this.compare,
					this.drop);
			this.dbDropColumns();
			this.compare.removeColumnsCascadeIndex(this.drop);
			// TODO
			this.dbAddnAlterColumn();
		}

		@Override
		final TypeCompatiblity typeCompatible(TableFieldDefineImpl field,
				DB2Column column) {
			return field.getType().detect(compatible, column);
		}

		@Override
		final boolean defaultChanged(TableFieldDefineImpl field,
				DB2Column column) {
			final boolean leftNull = field.getDefault() == null
					|| field.getDefault() == NullExpr.NULL;
			final boolean rightNull = column.defaultVal == null;
			if (leftNull != rightNull
					|| (!leftNull && !rightNull && !column.defaultVal
							.equals(defaultDefinition(field, defaultDeclare)))) {
				return true;
			}
			return false;
		}

		@Override
		final void dbRenameColumnAndSetNotNullToNullable(DB2Column column,
				String rename) throws SQLException {
			throw new UnsupportedOperationException();
		}

		private final void dbAddnAlterColumn() throws SQLException {
			if (this.add.size() == 0 && this.modify.size() == 0) {
				return;
			}
			SqlBuilder sql = new SqlBuilder(this.lang);
			sql.append("alter table ").appendId(this.compare.name);
			sql.nNewline().pi();
			if (this.add.size() > 0) {
				for (TableFieldDefineImpl add : this.add) {
					sql.append("add column ");
					columnDefinition(sql, add);
					sql.nNewline();
				}
			}
			if (this.modify.size() > 0) {
				for (ColumnState state : this.modify.values()) {
					sql.append("alter column ");
					columnAlteration(sql, state);
					sql.nNewline();
				}
			}
			this.statement.execute(sql);
		}

		private final void dbDropColumns() throws SQLException {
			if (this.drop.size() == 0) {
				return;
			}
			SqlBuilder sql = new SqlBuilder(this.lang);
			sql.append("alter table ").appendId(this.compare.name);
			sql.nNewline().pi();
			for (DB2Column column : this.drop) {
				sql.append("drop column ").appendId(column.name);
				sql.nNewline();
			}
			this.statement.execute(sql);
			SqlBuilder reorg = new SqlBuilder(this.lang);
			reorg.append("reorg table ").appendId(this.compare.name);
			this.statement.execute(reorg);
		}

	}

	private final class DB2IndexSync extends IndexSync {

		private DB2IndexSync() throws SQLException {
			super();
		}

		private Namespace namespace;

		@Override
		final void initNamespace() throws SQLException {
			this.namespace = NameCaseMode.CASE_SENSITIVE.newInstance();
			PreparedStatement ps = this.adapter
					.prepareStatement(SELECT_INDEX_NAME);
			try {
				ps.setString(1, this.adapter.getDefaultSchema());
				fillUsingStatement(this.namespace, ps);
			} finally {
				this.adapter.freeStatement(ps);
			}
		}

		private static final String SELECT_INDEX_NAME = "select name from sysibm.sysindexes where creator = ?";

		@Override
		final void ensureValid(IndexDefineImpl index, DB2Table dbTable) {
			if (index.ensureValidWithinNamespace(this.lang, this.namespace)) {
				this.sync.modified = true;
			}
		}

		@Override
		final void dbDropIndex(DB2Index index) throws SQLException {
			SqlBuilder sql = new SqlBuilder(this.lang);
			sql.append("drop index ").appendId(index.name);
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

		private static final String select_table_index = "select indname from syscat.indexes i where indschema = ? and tabschema = ? and tabname = ?";

		@Override
		final void notifyNsBeforeDropTable(String tableName)
				throws SQLException {
			PreparedStatement ps = this.adapter
					.prepareStatement(select_table_index);
			try {
				final String schema = this.lang
						.getDefaultSchema(this.adapter.dataSourceRef.dataSource);
				ps.setString(1, schema);
				ps.setString(2, schema);
				ps.setString(3, tableName);
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

		private static final String select_index_column = "select i.tabname, ic.colname from syscat.indexes i join syscat.indexcoluse ic on i.indschema = ic.indschema and i.indname = ic.indname where i.indschema = ? and i.tabschema = ? and i.tabname = ?";

		@Override
		final void notifyNsBeforeDropColumn(DB2Table table,
				ArrayList<DB2Column> columns) throws SQLException {
			PreparedStatement ps = this.adapter
					.prepareStatement(select_index_column);
			try {
				final String schema = this.lang
						.getDefaultSchema(this.adapter.dataSourceRef.dataSource);
				ps.setString(1, schema);
				ps.setString(2, schema);
				ps.setString(3, table.name);
				ResultSet rs = ps.executeQuery();
				try {
					while (rs.next()) {
						if (columns.contains(rs.getString(2))) {
							this.namespace.remove(rs.getString(1));
						}
					}
				} finally {
					rs.close();
				}
			} finally {
				ps.close();
			}
		}

	}

	private final class DB2HierarchySync extends HierarchySync {

		@Override
		HierarchyState detectState(HierarchyDefineImpl hierarchy)
				throws SQLException {
			// HCL Auto-generated method stub
			throw new UnsupportedOperationException();
		}

		@Override
		final void createHierarchyTable(HierarchyDefineImpl hierarchy)
				throws SQLException {
			// HCL Auto-generated method stub
			throw new UnsupportedOperationException();
		}

		@Override
		final void extendPath(HierarchyDefineImpl hierarchy)
				throws SQLException {
			// HCL Auto-generated method stub
			throw new UnsupportedOperationException();
		}

	}

	/**
	 * column_name, data_type, column_options
	 */
	private static final void columnDefinition(SqlBuilder sql,
			TableFieldDefineImpl field) {
		sql.appendId(field.namedb()).nSpace();
		sql.appendType(field.getType()).nSpace();
		final ConstExpr df = field.getDefault();
		if (df != null) {
			sql.appendDefault();
			sql.append(defaultDefinition(field, defaultDeclare));
			sql.nSpace();
		}
		if (field.isKeepValid()) {
			sql.appendNot().appendNull();
		}
	}

	private static final void columnAlteration(SqlBuilder sql, ColumnState state) {
		TableFieldDefineImpl field = state.field;
		sql.appendId(state.column.name).nSpace();
		if (state.get(ColumnCompareSync.MOD_TYPE)) {
			sql.append("set data type ").appendType(field.getType());
		}
		if (state.get(ColumnCompareSync.MOD_NULLABLE)) {
			sql.nSpace();
			if (field.isKeepValid()) {
				sql.append(" set not null");
			} else {
				sql.append(" drop not null");
			}
		}
		if (state.get(ColumnCompareSync.MOD_DEFAULT)) {
			if (field.getDefault() != null) {
				sql.append(" set default ");
				sql.append(defaultDefinition(field, defaultDeclare));
			} else {
				sql.append(" drop default");
			}
		}
	}

	private static final ConstFormatter defaultDeclare = new ConstFormatter() {

		@Override
		public String inDate(ConstExpr c) throws Throwable {
			return "\'"
					+ DateParser.format(c.getDate(),
							DateParser.FORMAT_DATE_TIME_MS) + "\'";
		}

		@Override
		String bytes(byte[] value) {
			return "x\'" + Convert.bytesToHex(value, false, false) + "\')";
		}
	};

	private static final TypeDetector<TypeCompatiblity, DB2Column> compatible = new TypeDetectorBase<TypeCompatiblity, DB2Column>() {

		@Override
		public TypeCompatiblity inBoolean(DB2Column column) throws Throwable {
			if (column.type == DB2Type.SMALLINT) {
				return Exactly;
			} else if (column.type == DB2Type.INTEGER
					|| column.type == DB2Type.BIGINT) {
				return Overflow;
			} else if (column.type == DB2Type.DECIMAL && column.scale == 0) {
				return NotSuggest;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inShort(DB2Column column) throws Throwable {
			if (column.type == DB2Type.SMALLINT) {
				return Exactly;
			} else if (column.type == DB2Type.INTEGER
					|| column.type == DB2Type.BIGINT) {
				return Overflow;
			} else if (column.type == DB2Type.DECIMAL && column.precision >= 5
					&& column.scale == 0) {
				return NotSuggest;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inInt(DB2Column column) throws Throwable {
			if (column.type == DB2Type.INTEGER) {
				return Exactly;
			} else if (column.type == DB2Type.BIGINT) {
				return Overflow;
			} else if (column.type == DB2Type.DECIMAL && column.precision >= 10
					&& column.scale == 0) {
				return NotSuggest;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inLong(DB2Column column) throws Throwable {
			if (column.type == DB2Type.BIGINT) {
				return Exactly;
			} else if (column.type == DB2Type.DECIMAL && column.precision >= 19
					&& column.scale == 0) {
				return NotSuggest;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inFloat(DB2Column column) throws Throwable {
			if (column.type == DB2Type.REAL) {
				return Exactly;
			} else if (column.type == DB2Type.DOUBLE) {
				return Overflow;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inDouble(DB2Column column) throws Throwable {
			if (column.type == DB2Type.DOUBLE) {
				return Exactly;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inNumeric(DB2Column column, int precision,
				int scale) throws Throwable {
			if (column.type == DB2Type.DECIMAL) {
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
		public TypeCompatiblity inChar(DB2Column column, SequenceDataType type)
				throws Throwable {
			if (column.forbitdata()) {
				return Unable;
			}
			if (column.type == DB2Type.VARCHAR) {
				if (column.length == type.getMaxLength()) {
					return Exactly;
				} else if (column.length > type.getMaxLength()) {
					return Overflow;
				}
			} else if (column.type == DB2Type.VARGRAPHIC) {
				return NotSuggest;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inVarChar(DB2Column column,
				SequenceDataType type) throws Throwable {
			if (column.forbitdata()) {
				return Unable;
			}
			if (column.type == DB2Type.VARCHAR) {
				if (column.length == type.getMaxLength()) {
					return Exactly;
				} else if (column.length > type.getMaxLength()) {
					return Overflow;
				}
			} else if (column.type == DB2Type.VARGRAPHIC) {
				return NotSuggest;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inText(DB2Column column) throws Throwable {
			if (column.type == DB2Type.CLOB) {
				return Exactly;
			} else if (column.type == DB2Type.DBCLOB) {
				return NotSuggest;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inNChar(DB2Column column, SequenceDataType type)
				throws Throwable {
			if (column.forbitdata()) {
				return Unable;
			}
			if (column.type == DB2Type.VARCHAR) {
				if (column.length == type.getMaxLength()) {
					return Exactly;
				} else if (column.length > type.getMaxLength()) {
					return Overflow;
				}
			} else if (column.type == DB2Type.VARGRAPHIC) {
				return NotSuggest;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inNVarChar(DB2Column column,
				SequenceDataType type) throws Throwable {
			if (column.forbitdata()) {
				return Unable;
			}
			if (column.type == DB2Type.VARCHAR) {
				if (column.length == type.getMaxLength()) {
					return Exactly;
				} else if (column.length > type.getMaxLength()) {
					return Overflow;
				}
			} else if (column.type == DB2Type.VARGRAPHIC) {
				return NotSuggest;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inNText(DB2Column column) throws Throwable {
			if (column.type == DB2Type.CLOB) {
				return Exactly;
			} else if (column.type == DB2Type.DBCLOB) {
				return NotSuggest;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inBinary(DB2Column column, SequenceDataType type)
				throws Throwable {
			if (column.type == DB2Type.VARCHAR && column.forbitdata()) {
				if (column.length == type.getMaxLength()) {
					return Exactly;
				} else if (column.length > type.getMaxLength()) {
					return Overflow;
				}
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inVarBinary(DB2Column column,
				SequenceDataType type) throws Throwable {
			if (column.type == DB2Type.VARCHAR && column.forbitdata()) {
				if (column.length == type.getMaxLength()) {
					return Exactly;
				} else if (column.length > type.getMaxLength()) {
					return Overflow;
				}
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inBlob(DB2Column column) throws Throwable {
			if (column.type == DB2Type.BLOB) {
				return Exactly;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inGUID(DB2Column column) throws Throwable {
			if (!column.forbitdata()) {
				return Unable;
			}
			if (column.type == DB2Type.CHARACTER && column.length == 16) {
				return Exactly;
			} else if (column.type == DB2Type.VARCHAR && column.length >= 16) {
				return NotSuggest;
			}
			return Unable;
		}

		@Override
		public TypeCompatiblity inDate(DB2Column column) throws Throwable {
			if (column.type == DB2Type.TIMESTAMP) {
				return Exactly;
			}
			return Unable;
		}

	};

}

final class DB2Table extends DbTable<DB2Table, DB2Column, DB2Index> {

	/**
	 * <ol>
	 * <li>column_name
	 * <li>column_type
	 * <li>column_length
	 * <li>column_scale
	 * <li>not_null
	 * <li>default_value
	 * <li>codepage
	 * </ol>
	 * 
	 * <ol>
	 * <li>owner 即jdbc_schema
	 * <li>table_name
	 * </ol>
	 */
	private static final String SELECT_TABLE_COLUMNS = "select c.colname, c.typename, c.length, c.scale, case c.nulls when 'Y' then 0 else 1 end, c.default, c.codepage from syscat.columns c where tabschema = ? and tabname = ? order by colno";

	@Override
	final void loadColumn(DBAdapterImpl adapter) throws SQLException {
		this.loadUsingSyscat(adapter);
		if (this.columns.size() == 0) {
			throw tableNotExists(this.name);
		}
	}

	final void loadUsingSyscat(DBAdapterImpl adapter) throws SQLException {
		PreparedStatement ps = adapter.prepareStatement(SELECT_TABLE_COLUMNS);
		try {
			ps.setString(1, adapter.getDefaultSchema());
			ps.setString(2, this.name);
			ResultSet rs = ps.executeQuery();
			try {
				while (rs.next()) {
					DB2Column column = this.addColumn(rs.getString(1));
					final String type = rs.getString(2).trim();
					final int length = rs.getInt(3);
					final int scale = rs.getInt(4);
					final boolean notnull = rs.getBoolean(5);
					final String defVal = rs.getString(6);
					final int codepage = rs.getInt(7);
					column.precision = column.length = length;
					// trim !!! disgusting
					// binary type unsupported
					column.type = DB2Type.valueOf(type);
					column.scale = scale;
					column.notNull = notnull;
					column.codepage = codepage;
					String defaultDefinition = defVal;
					if (!rs.wasNull() && defaultDefinition != null) {
						column.defaultVal = defaultDefinition;
					}
				}
			} finally {
				rs.close();
			}
		} finally {
			adapter.freeStatement(ps);
		}
	}

	final void loadUsingJdbc(DBAdapterImpl adapter) throws SQLException {
		ResultSet rs = adapter.getMetaData().getColumns(adapter.getCatalog(),
				adapter.getDefaultSchema(), this.name, null);
		try {
			while (rs.next()) {
				DB2Column column = this.addColumn(rs.getString(4));
				column.type = DB2Type.jdbcTypeOf(column, rs.getInt(5),
						rs.getString(6));
				column.length = column.precision = rs.getInt(7);
				column.notNull = rs.getString(18).equals("NO");
				column.defaultVal = rs.getString(13);
			}
		} finally {
			rs.close();
		}
	}

	@Override
	final DB2Column newColumnOnly(String name) {
		return new DB2Column(this, name);
	}

	/**
	 * 输出:
	 * <ol>
	 * <li>表模式
	 * <li>索引模式
	 * <li>索引名称
	 * <li>索引规则
	 * <li>索引列
	 * <li>索引列排序
	 * </ol>
	 * 
	 * 参数
	 * <ol>
	 * <li>表模式
	 * <li>索引模式
	 * <li>表名
	 * </ol>
	 */
	static final String SELECT_INDEX_COLUMNS = "select i.tbcreator, i.creator, i.name, i.uniquerule, ic.colname, ic.colorder from sysibm.sysindexes i inner join sysibm.sysindexcoluse ic on i.name = ic.indname and i.creator = ic.indschema where i.tbcreator = ? and i.creator = ? and i.tbname = ? order by i.name, ic.colseq";

	/**
	 * 输出:
	 * <ol>
	 * <li>索引名称
	 * <li>索引规则
	 * <li>索引列名
	 * <li>索引列排序 (是否降序)
	 * </ol>
	 * 
	 * 参数
	 * <ol>
	 * <li>表与索引模式(相同)
	 * <li>表名
	 * </ol>
	 */
	private static final String SELECT_INDEX_COLUMNS_COMPACT = "select i.name, i.uniquerule, ic.colname, case ic.colorder when 'D' then 1 else 0 end from sysibm.sysindexes i inner join sysibm.sysindexcoluse ic on i.name = ic.indname and i.creator = ic.indschema where i.tbcreator = i.creator and i.creator = ? and i.tbname = ? order by i.name, ic.colseq";

	@Override
	final void loadIndex(DBAdapterImpl adapter) throws SQLException {
		PreparedStatement ps = adapter
				.prepareStatement(SELECT_INDEX_COLUMNS_COMPACT);
		try {
			ps.setString(1, adapter.getDefaultSchema());
			ps.setString(2, this.name);
			ResultSet rs = ps.executeQuery();
			try {
				while (rs.next()) {
					String indexName = rs.getString(1);
					DB2Index index = this.findIndex(indexName);
					if (index == null) {
						IndexRule rule = IndexRule.valueOf(rs.getString(2));
						index = this.addIndex(indexName, rule.unqiue);
						if (rule == IndexRule.P && this.primary == null) {
							this.primary = index;
						}
					}
					final DB2Column column = this.getColumn(rs.getString(3));
					index.add(column, rs.getBoolean(4));
				}
			} finally {
				rs.close();
			}
		} finally {
			ps.close();
		}
	}

	@Override
	final DB2Index newIndexOnly(String name, boolean unique) {
		return new DB2Index(this, name, unique);
	}

	@Override
	final void checkEmptyStatus(DBAdapterImpl adapter) throws SQLException {
		this.tableEmpty = !exists(adapter, "select 1 from " + this.name
				+ " fetch first 1 row only");
	}

}

enum DB2Type {

	SMALLINT {

		@Override
		DataTypeBase typeOf(int length, int scale) {
			return ShortType.TYPE;
		}

		@Override
		TypeAlterability alterable(DB2Column column, DataType type) {
			if (type == IntType.TYPE || type == LongType.TYPE) {
				return Always;
			} else if (type instanceof NumericDBType) {
				NumericDBType n = (NumericDBType) type;
				if (n.precision - n.scale >= 5) {
					return Always;
				}
			}
			return Never;
		}
	},
	INTEGER {
		@Override
		DataTypeBase typeOf(int length, int scale) {
			return IntType.TYPE;
		}

		@Override
		TypeAlterability alterable(DB2Column column, DataType type) {
			if (type == LongType.TYPE) {
				return Always;
			} else if (type instanceof NumericDBType) {
				NumericDBType n = (NumericDBType) type;
				if (n.precision - n.scale >= 10) {
					return Always;
				}
			}
			return Never;
		}
	},
	BIGINT {
		@Override
		DataTypeBase typeOf(int length, int scale) {
			return IntType.TYPE;
		}

		@Override
		TypeAlterability alterable(DB2Column column, DataType type) {
			if (type instanceof NumericDBType) {
				NumericDBType n = (NumericDBType) type;
				if (n.precision - n.scale >= 19) {
					return Always;
				}
			}
			return Never;
		}
	},
	REAL {
		@Override
		DataTypeBase typeOf(int length, int scale) {
			return FloatType.TYPE;
		}

		@Override
		TypeAlterability alterable(DB2Column column, DataType type) {
			if (type == DoubleType.TYPE) {
				return Always;
			}
			return Never;
		}
	},
	DOUBLE {
		@Override
		DataTypeBase typeOf(int length, int scale) {
			return DoubleType.TYPE;
		}

		@Override
		TypeAlterability alterable(DB2Column column, DataType type) {
			return Never;
		}
	},
	DECIMAL {
		@Override
		DataTypeBase typeOf(int length, int scale) {
			return NumericDBType.map.get(length, length, scale);
		}

		@Override
		TypeAlterability alterable(DB2Column column, DataType type) {
			if (type instanceof NumericDBType) {
				NumericDBType n = (NumericDBType) type;
				if (n.precision - n.scale >= column.precision - column.scale
						&& n.scale >= column.scale) {
					return Always;
				}
			}
			return Never;
		}
	},
	DATE {
		@Override
		DataTypeBase typeOf(int length, int scale) {
			return DateType.TYPE;
		}

		@Override
		TypeAlterability alterable(DB2Column column, DataType type) {
			return Never;
		}
	},
	TIMESTAMP {
		@Override
		DataTypeBase typeOf(int length, int scale) {
			return DateType.TYPE;
		}

		@Override
		TypeAlterability alterable(DB2Column column, DataType type) {
			return Never;
		}
	},
	CHARACTER {
		@Override
		DataTypeBase typeOf(int length, int scale) {
			return CharDBType.map.get(length, length, scale);
		}

		@Override
		TypeAlterability alterable(DB2Column column, DataType type) {
			if ((column.forbitdata() && (type instanceof FixBinDBType || type instanceof VarBinDBType))
					|| (!column.forbitdata() && (type instanceof CharDBType || type instanceof VarCharDBType))) {
				SequenceDataType c = (SequenceDataType) type;
				if (c.getMaxLength() >= column.length) {
					return Always;
				}
			}
			return Never;
		}
	},
	VARCHAR {
		@Override
		DataTypeBase typeOf(int length, int scale) {
			return VarCharDBType.map.get(length, length, scale);
		}

		@Override
		TypeAlterability alterable(DB2Column column, DataType type) {
			return CHARACTER.alterable(column, type);
		}
	},
	CLOB {
		@Override
		DataTypeBase typeOf(int length, int scale) {
			return TextDBType.TYPE;
		}

		@Override
		TypeAlterability alterable(DB2Column column, DataType type) {
			return Never;
		}
	},
	GRAPHIC {
		@Override
		DataTypeBase typeOf(int length, int scale) {
			return NCharDBType.map.get(length, length, scale);
		}

		@Override
		TypeAlterability alterable(DB2Column column, DataType type) {
			if (type instanceof NCharDBType || type instanceof NVarCharDBType) {
				CharsType c = (CharsType) type;
				if (c.getMaxLength() >= column.length) {
					return Always;
				}
			}
			return Never;
		}
	},
	VARGRAPHIC {
		@Override
		DataTypeBase typeOf(int length, int scale) {
			return NVarCharDBType.map.get(length, length, scale);
		}

		@Override
		TypeAlterability alterable(DB2Column column, DataType type) {
			return GRAPHIC.alterable(column, type);
		}
	},
	DBCLOB {
		@Override
		DataTypeBase typeOf(int length, int scale) {
			return NTextDBType.TYPE;
		}

		@Override
		TypeAlterability alterable(DB2Column column, DataType type) {
			return Never;
		}
	},
	BLOB {
		@Override
		DataTypeBase typeOf(int length, int scale) {
			return BlobDBType.TYPE;
		}

		@Override
		TypeAlterability alterable(DB2Column column, DataType type) {
			return Never;
		}
	};
	abstract DataTypeBase typeOf(int length, int scale);

	abstract TypeAlterability alterable(DB2Column column, DataType type);

	static final DB2Type jdbcTypeOf(DB2Column column, int dataType,
			String typeName) {
		switch (dataType) {
			case Types.BINARY:
				column.codepage = 0;
				column.type = DB2Type.CHARACTER;
				break;
			case Types.BIGINT:
				column.type = DB2Type.BIGINT;
				break;
			case Types.SMALLINT:
				column.type = DB2Type.SMALLINT;
				break;
			case Types.INTEGER:
				column.type = DB2Type.INTEGER;
				break;
			case Types.REAL:
				column.type = DB2Type.REAL;
				break;
			case Types.DOUBLE:
				column.type = DB2Type.DOUBLE;
				break;
			case Types.DECIMAL:
			case Types.NUMERIC:
				column.type = DB2Type.DECIMAL;
				break;
			case Types.CHAR:
				if (typeName.equals("CHAR")) {
					column.type = DB2Type.CHARACTER;
				} else {
					column.type = DB2Type.GRAPHIC;
				}
				break;
			case Types.VARCHAR:
				if (typeName.equals("VARCHAR")) {
					column.type = DB2Type.VARCHAR;
				} else {
					column.type = DB2Type.VARGRAPHIC;
				}
				break;
		}
		return null;
	}
}

final class DB2Column extends DbColumn<DB2Table, DB2Column, DB2Index> {

	DB2Column(DB2Table table, String name) {
		super(table, name);
	}

	DB2Type type;

	int codepage;

	final boolean forbitdata() {
		return (this.type == DB2Type.CHARACTER || this.type == DB2Type.VARCHAR)
				&& this.codepage == 0;
	}

	// 可以执行alter column x set data type
	@Override
	final TypeAlterability typeAlterable(DataType type) {
		return this.type.alterable(this, type);
	}
}

final class DB2Index extends DbIndex<DB2Table, DB2Column, DB2Index> {

	DB2Index(DB2Table table, String name, boolean unique) {
		super(table, name, unique);
	}
}

enum IndexRule {

	P(true) {
	},
	U(true) {
	},
	D(false) {
	},
	C(true) {
	},
	N(true) {
	},
	R(true) {
	},
	G(true) {
	};

	final boolean unqiue;

	IndexRule(boolean unique) {
		this.unqiue = unique;
	}
}
