package org.eclipse.jt.core.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.eclipse.jt.core.Filter;
import org.eclipse.jt.core.exception.TableSynchronizationException;
import org.eclipse.jt.core.type.DataType;
import org.eclipse.jt.core.type.SequenceDataType;
import org.eclipse.jt.core.type.TypeDetectorBase;


/**
 * 表同步器
 * 
 * @author Jeff Tang
 */
abstract class TableSynchronizerImpl<TLang extends DBLang, TTable extends DbTable<TTable, TColumn, TIndex>, TColumn extends DbColumn<TTable, TColumn, TIndex>, TIndex extends DbIndex<TTable, TColumn, TIndex>>
		implements TableSynchronizer {

	public final boolean sync(TableDefineImpl table) throws SQLException {
		this.modified = false;
		for (DBTableDefineImpl define : table.dbTables) {
			if (this.tableSync.namespace.contains(define.namedb())) {
				this.tableSync.checkExistedTableName(table, define);
				this.compare.reset(this.adapter, define.namedb());
				this.columnCompareSync.reset(define);
				this.columnCompareSync.compareDefined();
				this.columnCompareSync.execute();
				this.indexSync.compareBothAndSync(define, this.compare);
			} else {
				this.tableSync.createTableNotifyNs(define);
			}
		}
		return this.modified;
	}

	public final boolean post(TableDefineImpl post, TableDefineImpl runtime)
			throws SQLException {
		this.modified = false;
		for (DBTableDefineImpl define : post.dbTables) {
			if (this.tableSync.namespace.contains(define.namedb())) {
				this.tableSync.checkExistedTableName(post, define);
				this.compare.reset(this.adapter, define.namedb());
				this.columnCompareSync.reset(define);
				this.columnCompareSync.compareUndefined();
				this.columnCompareSync.compareDefined();
				this.columnCompareSync.execute();
				this.indexSync.compareBothAndSync(define, this.compare);
			} else {
				this.tableSync.createTableNotifyNs(define);
			}
		}
		this.tableSync.dropUndefinedTableAndHierarchy(post, runtime);
		return this.modified;
	}

	public final void drop(TableDefineImpl table) throws SQLException {
		for (HierarchyDefineImpl hierarchy : table.hierarchies) {
			this.hierarchySync.dropHierarchyNotifyNs(hierarchy);
		}
		for (DBTableDefineImpl define : table.dbTables) {
			this.tableSync.dropTableNotifyNs(define.namedb());
		}
	}

	public final void unuse() {
		if (this.statement != null) {
			this.statement.unuse();
		}
	}

	final DBAdapterImpl adapter;
	final DdlStatement statement;
	final TLang lang;

	final TableSync tableSync;
	final ColumnCompareSync columnCompareSync;
	final IndexSync indexSync;
	final HierarchySync hierarchySync;

	final TTable compare;

	boolean modified;

	TableSynchronizerImpl(DBAdapterImpl adapter, TLang lang)
			throws SQLException {
		this.adapter = adapter;
		this.statement = new DdlStatement(adapter);
		this.lang = lang;
		adapter.updateTrans(true);
		this.compare = this.newCompareTable();
		this.tableSync = this.newTableSync();
		this.columnCompareSync = this.newColumnSync();
		this.indexSync = this.newIndexSync();
		this.hierarchySync = this.newHierarchySync();
	}

	abstract TTable newCompareTable();

	abstract TableSync newTableSync() throws SQLException;

	abstract ColumnCompareSync newColumnSync();

	abstract IndexSync newIndexSync() throws SQLException;

	abstract HierarchySync newHierarchySync();

	abstract class DefineSync {

		final TableSynchronizerImpl<TLang, TTable, TColumn, TIndex> sync = TableSynchronizerImpl.this;
		final DBAdapterImpl adapter = TableSynchronizerImpl.this.adapter;
		final TLang lang = TableSynchronizerImpl.this.lang;
		final DdlStatement statement = TableSynchronizerImpl.this.statement;
	}

	abstract class TableSync extends DefineSync {

		final Namespace namespace;

		TableSync(NameCaseMode mode) throws SQLException {
			this.namespace = mode.newInstance();
			this.initNamespace();
		}

		abstract void initNamespace() throws SQLException;

		final void createTableNotifyNs(DBTableDefineImpl define)
				throws SQLException {
			if (define.ensureValid(this.lang)) {
				this.sync.modified = true;
			}
			this.dbCreateTable(define);
			this.namespace.add(define.namedb());
			this.sync.indexSync.notifyNsAfterCreateIndex(define.getPkeyName());
			this.sync.indexSync.create(define);
		}

		final void dropUndefinedTableAndHierarchy(TableDefineImpl post,
				TableDefineImpl runtime) throws SQLException {
			for (DBTableDefineImpl t : runtime.dbTables) {
				final String name = t.namedb();
				if (post.dbTables.find(name) == null) {
					this.dropTableNotifyNs(name);
				}
			}
			for (HierarchyDefineImpl hierarchy : runtime.hierarchies) {
				if (post.hierarchies.find(hierarchy.name) == null) {
					this.sync.hierarchySync.dropHierarchyNotifyNs(hierarchy);
				}
			}
		}

		abstract void dbCreateTable(DBTableDefineImpl define)
				throws SQLException;

		final void dropTableNotifyNs(String table) throws SQLException {
			this.sync.indexSync.notifyNsBeforeDropTable(table);
			this.namespace.remove(table);
			this.dbDropTable(table);
		}

		final void dbDropTable(String table) throws SQLException {
			SqlBuilder sql = new SqlBuilder(this.lang);
			sql.append("drop table ").appendId(table);
			this.statement.execute(sql);
		}

		final void checkExistedTableName(TableDefineImpl table,
				DBTableDefineImpl define) {
			if (this.lang.filterKeyword(define.namedb())) {
				System.err.println("在逻辑表[" + table.name + "],物理表名称["
						+ define.namedb() + "]为关键字,非常不建议使用.");
			}
		}
	}

	final class ColumnState {

		final TableFieldDefineImpl field;
		final TColumn column;
		int state;

		final void set(int mod) {
			this.state |= mod;
		}

		final boolean get(int mod) {
			return (this.state & mod) != 0;
		}

		ColumnState(TableFieldDefineImpl field, TColumn column) {
			this.field = field;
			this.column = column;
		}
	}

	abstract class ColumnCompareSync extends DefineSync implements
			Filter<String> {

		ColumnCompareSync() {
			this.compare = this.sync.compare;
		}

		final void reset(DBTableDefineImpl define) {
			this.define = define;
			this.drop.clear();
			this.unuse.clear();
			this.add.clear();
			this.modify.clear();
			this.cache.clear();
		}

		final void compareDefined() {
			next: for (TableFieldDefineImpl field : this.define.owner.fields) {
				for (; !field.isRECID() && !field.isRECVER()
						&& field.dbTable == this.define;) {
					TColumn column = this.compare.findColumn(field.namedb());
					if (column == null) {
						if (SystemVariables.DEBUG_SYNC) {
							log(field, "在数据库表中不存在");
						}
						if (field.ensureValid(this.lang)) {
							if (SystemVariables.DEBUG_SYNC) {
								log(field, "名称为数据库关键字而被修改");
							}
							this.sync.modified = true;
							continue;
						} else {
							if (SystemVariables.DEBUG_SYNC) {
								log(field, "加入到新增列队列");
							}
							this.add.add(field);
							continue next;
						}
					} else {
						TypeCompatiblity compatible = this.typeCompatible(
								field, column);
						TypeAlterability alterable = column.typeAlterable(field
								.getType());
						switch (compatible) {
							case NotSuggest:
								if (this.currentConditionSupportAlterType(
										alterable, this.compare)) {
									if (SystemVariables.DEBUG_SYNC) {
										log(field, "数据库对应列类型为不建议,加入类型修改队列");
									}
									this.modify(field, column).set(MOD_TYPE);
								}
							case Exactly:
							case Overflow:
								this.handleMatched(field, column);
								continue next;
							case Unable: {
								if (this.currentConditionSupportAlterType(
										alterable, this.compare)) {
									if (SystemVariables.DEBUG_SYNC) {
										log(field, "数据库对应列类型不兼容,加入类型修改队列");
									}
									this.modify(field, column).set(MOD_TYPE);
									this.handleMatched(field, column);
								} else if (this.compare.tableEmpty) {
									if (SystemVariables.DEBUG_SYNC) {
										log(field,
												"数据库对应列类型不兼容且不能转换,因为表为空,字段将被删除后重建");
									}
									this.drop.add(column);
									this.add.add(field);
								} else {
									if (SystemVariables.DEBUG_SYNC) {
										log(field,
												"数据库对应列类型不兼容且不能转换,因为表不为空,原数据库列将被废弃,字段加入新增列队列");
									}
									this.unuse.put(field, column);
								}
							}
						}
						continue next;
					}
				}
			}
		}

		final void compareUndefined() {
			for (TColumn column : this.compare.columns) {
				if (column.isRecid() || column.isRecver()) {
					continue;
				}
				TableFieldDefineImpl field = this.define.fields
						.get(column.name);
				if (field == null) {
					TableFieldDefineImpl across = this.define.owner
							.findFieldUsingNamedb(column.name);
					if (across == null || this.compare.tableEmpty) {
						this.drop.add(column);
					} else {
						throw new UnsupportedOperationException("不支持跨物理表增加同名字段");
					}
				} else {
					TypeCompatiblity compatible = this.typeCompatible(field,
							column);
					TypeAlterability alterable = column.typeAlterable(field
							.getType());
					switch (compatible) {
						case Exactly:
						case Overflow:
						case NotSuggest:
							continue;
						case Unable: {
							if (this.currentConditionSupportAlterType(
									alterable, this.compare)) {
								continue;
							} else if (this.compare.tableEmpty) {
								this.drop.add(column);
							} else {
								throw new UnsupportedOperationException(
										"字段类别变化");
							}
						}
					}

				}
			}
		}

		DBTableDefineImpl define;
		TTable compare;

		/**
		 * 删除列,必须在add列之前处理
		 */
		final ArrayList<TColumn> drop = new ArrayList<TColumn>();
		/**
		 * 废弃列,定义字段名称存在,但类型不匹配
		 * 
		 * <p>
		 * 数据库支持重命名列,则将废弃列重命名,被设置可为空,新增定义字段.否则,修改定义字段的namedb,新增字段,同时设置存在列可为空.
		 */
		final LinkedHashMap<TableFieldDefineImpl, TColumn> unuse = new LinkedHashMap<TableFieldDefineImpl, TColumn>();
		/**
		 * 新增列
		 */
		final ArrayList<TableFieldDefineImpl> add = new ArrayList<TableFieldDefineImpl>();
		/**
		 * 修改列
		 */
		final LinkedHashMap<String, ColumnState> modify = new LinkedHashMap<String, ColumnState>();
		final ArrayList<TIndex> cache = new ArrayList<TIndex>();

		static final int MOD_TYPE = 1 << 0;
		static final int MOD_DEFAULT = 1 << 1;
		static final int MOD_NULLABLE = 1 << 2;

		abstract void execute() throws SQLException;

		abstract TypeCompatiblity typeCompatible(TableFieldDefineImpl field,
				TColumn column);

		abstract boolean defaultChanged(TableFieldDefineImpl field,
				TColumn column);

		final ColumnState modify(TableFieldDefineImpl field, TColumn column) {
			if (!field.namedb().equals(column.name)) {
				throw new IllegalStateException();
			}
			ColumnState state = this.modify.get(column.name);
			if (state == null) {
				state = new ColumnState(field, column);
				this.modify.put(column.name, state);
			}
			return state;
		}

		/**
		 * 当前条件是否满足可修改性,以修改列类型
		 */
		final boolean currentConditionSupportAlterType(
				TypeAlterability alterable, TTable compare) {
			return alterable == TypeAlterability.Always
					|| (alterable == TypeAlterability.ColumnNull && compare.tableEmpty);
		}

		protected void handleMatched(TableFieldDefineImpl field, TColumn column) {
			if (field.isKeepValid() != column.notNull) {
				if (field.isKeepValid()) {
					if ((field.getDefault() == null || field.getDefault() == NullExpr.NULL)
							&& !this.compare.tableEmpty) {
						System.err.println("在逻辑表[" + field.owner.name
								+ "],物理表[" + field.dbTable.namedb()
								+ "],尝试修改字段[" + field.name + "."
								+ field.namedb() + "]不为空,但没有提供默认值定义或表不为空.");
					} else {
						if (SystemVariables.DEBUG_SYNC) {
							log(field, "为空属性修改,加入到修改列队列");
						}
						this.modify(field, column).set(MOD_NULLABLE);
					}
				}
			}
			if (this.defaultChanged(field, column)) {
				this.modify(field, column).set(MOD_DEFAULT);
				if (SystemVariables.DEBUG_SYNC) {
					log(field, "默认值属性修改,加入到修改列队类");
				}
			}
		}

		static final String UNUSED_COLUMN_PREFIX = "UNUSED_";

		public final boolean accept(String item) {
			return this.compare.columnMap.containsKey(item)
					|| this.define.fields.containsKey(item);
		}

		final void renameUnusedDbColumnThenAddDefineColumn()
				throws SQLException {
			for (Entry<TableFieldDefineImpl, TColumn> e : this.unuse.entrySet()) {
				final TableFieldDefineImpl field = e.getKey();
				final TColumn column = e.getValue();
				String rename = UNUSED_COLUMN_PREFIX.concat(column.name);
				final int maxlen = this.lang.getMaxColumnNameLength();
				if (rename.length() > maxlen
						|| this.compare.columnMap.containsKey(rename)
						|| this.define.fields.containsKey(rename)) {
					rename = Utils.buildIdentityName(rename, maxlen, this);
				}
				this.dbRenameColumnAndSetNotNullToNullable(column, rename);
				this.compare.columnMap.remove(column.name);
				column.name = rename;
				this.compare.columnMap.put(rename, column);
				this.add.add(field);
			}
		}

		abstract void dbRenameColumnAndSetNotNullToNullable(TColumn column,
				String rename) throws SQLException;

	}

	abstract class IndexSync extends DefineSync {

		IndexSync() throws SQLException {
			this.initNamespace();
		}

		abstract void initNamespace() throws SQLException;

		final void create(DBTableDefineImpl define) throws SQLException {
			IndexDefineImpl lk = define.owner.logicalKey;
			if (define.isPrimary() && lk != null) {
				this.createIndex(lk);
			}
			for (IndexDefineImpl index : define.owner.indexes) {
				if (index.dbTable == define) {
					this.createIndex(index);
				}
			}
		}

		final void compareBothAndSync(DBTableDefineImpl define, TTable compare)
				throws SQLException {
			for (IndexDefineImpl left : define.owner.indexes) {
				if (left.dbTable == define) {
					this.compareAndSync(left, compare);
				}
			}
			IndexDefineImpl lk = define.owner.logicalKey;
			if (define.isPrimary() && lk != null) {
				this.compareAndSync(lk, compare);
			}
			next: for (TIndex right : compare.indexes) {
				if (right.isPrimaryKey()) {
					continue;
				}
				if (define.isPrimary() && lk != null
						&& right.name.equals(lk.namedb())) {
					continue;
				}
				if (right.name.startsWith(SystemVariables.NONE_DNA_INDEX)) {
					continue;
				}
				for (int i = 0, c = define.owner.indexes.size(); i < c; i++) {
					IndexDefineImpl left = define.owner.indexes.get(i);
					if (left.namedb().equals(right.name)) {
						continue next;
					}
				}
				this.dbDropIndex(right);
				this.notifyNsAfterDropIndex(right.name);
			}
		}

		private final void compareAndSync(IndexDefineImpl left, TTable compare)
				throws SQLException {
			TIndex right = compare.findIndex(left.namedb());
			if (right != null) {
				if (!right.structEquals(left)) {
					TIndex find = compare.findStructEqualIndex(left);
					if (find != null) {
						left.setNamedb(find.name);
						this.sync.modified = true;
					} else {
						this.ensureValid(left, compare);
						this.dbCreateIndex(left);
						this.notifyNsAfterCreateIndex(left.namedb());
						compare.addIndexLike(left);
					}
				}
			} else {
				TIndex find = compare.findStructEqualIndex(left);
				if (find != null) {
					if (!this.dbTryRenameIndex(compare, find, left.namedb())) {
						left.setNamedb(find.name);
						this.sync.modified = true;
					}
				} else {
					this.ensureValid(left, compare);
					this.dbCreateIndex(left);
					this.notifyNsAfterCreateIndex(left.namedb());
					compare.addIndexLike(left);
				}
			}
		}

		final void createIndex(IndexDefineImpl index) throws SQLException {
			this.ensureValid(index, null);
			this.dbCreateIndex(index);
			this.notifyNsAfterCreateIndex(index.namedb());
		}

		abstract void ensureValid(IndexDefineImpl index, TTable dbTable);

		final void ensureValieWithinTable(final IndexDefineImpl index,
				final TTable dbTable) {
			final int maxlen = this.lang.getMaxIndexNameLength();
			if (index.namedb().length() > maxlen
					|| this.lang.filterKeyword(index.namedb())
					|| (dbTable != null && dbTable.indexMap.containsKey(index
							.namedb()))) {
				String rename = Utils.buildIdentityName(index.namedb(), maxlen,
						new Filter<String>() {
							public boolean accept(String item) {
								return IndexSync.this.lang.filterKeyword(index
										.namedb())
										|| (dbTable != null && dbTable.indexMap
												.containsKey(index.namedb()));
							}
						});
				index.setNamedb(rename);
			}
		}

		final void dbCreateIndex(IndexDefineImpl index) throws SQLException {
			SqlBuilder sql = new SqlBuilder(this.lang);
			sql.append("create ");
			if (index.isUnique()) {
				sql.append("unique ");
			}
			sql.append("index ").appendId(index.namedb());
			sql.append(" on ").appendId(index.dbTable.namedb()).nSpace().lp();
			for (IndexItemImpl item : index.items) {
				sql.appendId(item.field.namedb());
				if (item.isDesc()) {
					sql.appendDesc();
				}
				sql.nComma();
			}
			sql.uComma().rp();
			this.statement.execute(sql);
		}

		final void dbDropIndexes(ArrayList<TIndex> indexes) throws SQLException {
			for (int i = 0, c = indexes.size(); i < c; i++) {
				this.dbDropIndex(indexes.get(i));
			}
		}

		abstract void dbDropIndex(TIndex index) throws SQLException;

		protected boolean dbTryRenameIndex(TTable table, TIndex index,
				String rename) {
			return false;
		}

		abstract void notifyNsAfterCreateIndex(String index);

		abstract void notifyNsAfterDropIndex(String index);

		abstract void notifyNsBeforeDropTable(String tableName)
				throws SQLException;

		abstract void notifyNsBeforeDropColumn(TTable table,
				ArrayList<TColumn> columns) throws SQLException;

	}

	abstract class HierarchySync extends DefineSync {

		/**
		 * <ul>
		 * <li>不存在指定名称表,则创建级次表,不填充任何数据.
		 * <li>存在指定名称表,且结构为级次表结构,则匹配使用,并保证长度.
		 * <li>存在指定名称表,且结构不为级次表结构,则重构表名,创建新表.
		 * </ul>
		 * 
		 * @param table
		 * @throws SQLException
		 */
		final void sync(TableDefineImpl table) throws SQLException {
			for (HierarchyDefineImpl hierarchy : table.hierarchies) {
				HierarchyState state = this.detectState(hierarchy);
				switch (state) {
					case CREATE_NEW: {
						// TODO
						// hierarchy.ensureValid(sync.lang, sync.tables,
						// sync.indexes);
						// sync.createHierarchy(hierarchy);
						// sync.tables.add(hierarchy.tableName());
						// sync.indexes.add(hierarchy.pkIndex());
						// hierarchy.ensurePathIndexValid(sync.lang,
						// sync.indexes);
						// sync.createHierarchyPathIndex(hierarchy);
						// sync.indexes.add(hierarchy.pathIndex());
						break;
					}
					case EXTEND_PATH: {
						this.extendPath(hierarchy);
						break;
					}
					case DO_NOTHING:
				}
			}
		}

		abstract HierarchyState detectState(HierarchyDefineImpl hierarchy)
				throws SQLException;

		abstract void createHierarchyTable(HierarchyDefineImpl hierarchy)
				throws SQLException;

		final void createPathIndex(HierarchyDefineImpl hierarchy)
				throws SQLException {
			SqlBuilder sql = new SqlBuilder(this.lang);
			sql.append("create unique index ");
			sql.appendId(hierarchy.pathIndex());
			sql.append(" on ").appendId(hierarchy).lp();
			sql.appendId(HierarchyDefineImpl.COLUMN_NAME_PATH);
			sql.nComma().nSpace();
			sql.appendId(HierarchyDefineImpl.COLUMN_NAME_RECID);
			sql.rp();
			this.statement.execute(sql);
		}

		abstract void extendPath(HierarchyDefineImpl hierarchy)
				throws SQLException;

		final void dropHierarchyNotifyNs(HierarchyDefineImpl hierarchy)
				throws SQLException {
			this.sync.tableSync.dbDropTable(hierarchy.tableName);
			this.sync.tableSync.namespace.remove(hierarchy.tableName);
			this.sync.indexSync.notifyNsAfterDropIndex(hierarchy.pkIndex);
			this.sync.indexSync.notifyNsAfterDropIndex(hierarchy.pathIndex);
		}
	}

	enum HierarchyState {

		CREATE_NEW, EXTEND_PATH, DO_NOTHING;
	}

	static final IndexDefineImpl findStructEqual(IndexDefineImpl index,
			ArrayList<IndexDefineImpl> others) {
		if (others == null) {
			return null;
		}
		for (int i = 0, c = others.size(); i < c; i++) {
			IndexDefineImpl other = others.get(i);
			if (index.structEquals(other)) {
				return other;
			}
		}
		return null;
	}

	static final void fillUsingSelect(DBAdapterImpl adapter, Namespace ns,
			String sql) throws SQLException {
		PreparedStatement ps = adapter.prepareStatement(sql);
		try {
			ResultSet rs = ps.executeQuery();
			try {
				while (rs.next()) {
					ns.add(rs.getString(1));
				}
			} finally {
				rs.close();
			}
		} finally {
			adapter.freeStatement(ps);
		}
	}

	static final void fillUsingStatement(Namespace ns, PreparedStatement ps)
			throws SQLException {
		ResultSet rs = ps.executeQuery();
		try {
			while (rs.next()) {
				ns.add(rs.getString(1));
			}
		} finally {
			rs.close();
		}
	}

	static final void outlineRecidConstraint(SqlBuilder sql,
			DBTableDefineImpl dbTable) {
		outlinePkConstraintDefinition(sql, dbTable.getPkeyName(),
				dbTable.owner.f_recid.namedb());
	}

	static final void outlinePkConstraintDefinition(SqlBuilder sql,
			String name, String column, String... others) {
		sql.appendConstraint();
		sql.appendId(name);
		sql.appendPrimaryKey();
		sql.lp().appendId(column);
		if (others != null) {
			for (String other : others) {
				sql.nComma().appendId(other);
			}
		}
		sql.rp();
	}

	static final String defaultDefinition(TableFieldDefineImpl field,
			ConstFormatter fmt) {
		final ConstExpr d = field.getDefault();
		if (d == null) {
			return null;
		}
		return d.getType().detect(fmt, d);
	}

	static final TableSynchronizationException addNotNullColumnWithoutDefaultValueToNotEmptyTable(
			TableFieldDefineImpl field) {
		return new TableSynchronizationException(field.owner, "新增非空字段["
				+ field.name + "]未定义默认值.");
	}

	static final <TTable extends DbTable<TTable, TColumn, TIndex>, TColumn extends DbColumn<TTable, TColumn, TIndex>, TIndex extends DbIndex<TTable, TColumn, TIndex>> boolean existStructEqual(
			TTable table, IndexDefineImpl t) {
		for (TIndex index : table.indexes) {
			if (index.structEquals(t)) {
				return true;
			}
		}
		return false;
	}

	static abstract class ConstFormatter extends
			TypeDetectorBase<String, ConstExpr> {

		@Override
		public String inBoolean(ConstExpr c) throws Throwable {
			return c.getBoolean() ? "1" : "0";
		}

		@Override
		public String inByte(ConstExpr c) throws Throwable {
			return Byte.toString(c.getByte());
		}

		@Override
		public String inShort(ConstExpr c) throws Throwable {
			return Short.toString(c.getShort());
		}

		@Override
		public String inInt(ConstExpr c) throws Throwable {
			return Integer.toString(c.getInt());
		}

		@Override
		public String inLong(ConstExpr c) throws Throwable {
			return Long.toString(c.getLong());
		}

		@Override
		public String inFloat(ConstExpr c) throws Throwable {
			return Float.toString(c.getFloat());
		}

		@Override
		public String inDouble(ConstExpr c) throws Throwable {
			return Double.toString(c.getDouble());
		}

		@Override
		public String inString(ConstExpr c, SequenceDataType type)
				throws Throwable {
			return "\'" + escape(c.getString()) + "\'";
		}

		@Override
		public String inBytes(ConstExpr c, SequenceDataType type)
				throws Throwable {
			return this.bytes(c.getBytes());
		}

		@Override
		public String inGUID(ConstExpr c) throws Throwable {
			return this.bytes(c.getBytes());
		}

		abstract String bytes(byte[] value);

		public String format(NullExpr c) {
			return "null";
		}

		static final String escape(String s) {
			if (s == null) {
				return "null";
			}
			final int l = s.length();
			if (l == 0) {
				return "";
			}
			StringBuilder sb = null;
			int start = 0;
			for (int i = s.indexOf('\'', 0); i > 0; i = s.indexOf('\'', i + 1)) {
				if (sb == null) {
					sb = new StringBuilder(l * 5 / 4);
				}
				sb.append(s, start, i).append('\'');
				start = i + 1;
			}
			if (sb == null) {
				return s;
			} else {
				return sb.toString();
			}
		}

	}

	private static final void log(String msg) {
		System.out.println("[tablesync]:" + msg);
	}

	private static final void log(TableFieldDefineImpl field, String msg) {
		log("对比表结构在[" + field.owner.name + "." + field.dbTable.namedb()
				+ "],字段定义[" + field.name + "." + field.namedb() + "]" + msg);
	}
}

abstract class DbTable<TTable extends DbTable<TTable, TColumn, TIndex>, TColumn extends DbColumn<TTable, TColumn, TIndex>, TIndex extends DbIndex<TTable, TColumn, TIndex>> {

	@Override
	public final String toString() {
		return this.name;
	}

	String name;

	final void reset(DBAdapterImpl adapter, String name) throws SQLException {
		this.name = name;
		this.columnMap.clear();
		this.columns.clear();
		this.indexMap.clear();
		this.indexes.clear();
		this.primary = null;
		this.loadColumn(adapter);
		this.loadIndex(adapter);
		this.checkEmptyStatus(adapter);
	}

	final StringKeyMap<TColumn> columnMap = new StringKeyMap<TColumn>();
	final ArrayList<TColumn> columns = new ArrayList<TColumn>();

	abstract void loadColumn(DBAdapterImpl adapter) throws SQLException;

	static final IllegalStateException tableNotExists(String table) {
		return new IllegalStateException();
	}

	final TColumn findColumn(String name) {
		return this.columnMap.get(name);
	}

	final TColumn getColumn(String name) {
		return this.columnMap.get(name, true);
	}

	abstract TColumn newColumnOnly(String name);

	final TColumn addColumn(String name) {
		TColumn c = this.newColumnOnly(name);
		this.columnMap.put(name, c, true);
		this.columns.add(c);
		return c;
	}

	final void removeColumnCascadeIndex(TColumn column) {
		this.columnMap.remove(column.name, true);
		this.columns.remove(column);
		for (int i = this.indexes.size() - 1; i >= 0; i--) {
			TIndex index = this.indexes.get(i);
			if (index.indexedColumn(column)) {
				this.indexMap.remove(index.name, true);
				this.indexes.remove(index);
			}
		}
	}

	final void removeColumnsCascadeIndex(ArrayList<TColumn> columns) {
		for (int i = 0, c = columns.size(); i < c; i++) {
			this.removeColumnCascadeIndex(columns.get(i));
		}
	}

	final StringKeyMap<TIndex> indexMap = new StringKeyMap<TIndex>();
	final ArrayList<TIndex> indexes = new ArrayList<TIndex>();
	TIndex primary;

	abstract void loadIndex(DBAdapterImpl adapter) throws SQLException;

	final TIndex findIndex(String name) {
		return this.indexMap.get(name);
	}

	final TIndex findStructEqualIndex(IndexDefineImpl index) {
		for (int i = 0, c = this.indexes.size(); i < c; i++) {
			TIndex compare = this.indexes.get(i);
			if (compare.structEquals(index)) {
				return compare;
			}
		}
		return null;
	}

	final TIndex getIndex(String name) {
		return this.indexMap.get(name, true);
	}

	final void removeIndex(TIndex index) {
		this.indexMap.remove(index.name, true);
		this.indexes.remove(index);
	}

	final void removeIndex(ArrayList<TIndex> indexes) {
		for (int i = 0, c = indexes.size(); i < c; i++) {
			TIndex index = indexes.get(i);
			this.removeIndex(index);
		}
	}

	abstract TIndex newIndexOnly(String name, boolean unique);

	final TIndex addIndex(String name, boolean unique) {
		TIndex index = this.newIndexOnly(name, unique);
		this.indexMap.put(name, index, true);
		this.indexes.add(index);
		return index;
	}

	final void addIndexLike(IndexDefineImpl index) {
		TIndex oi = this.newIndexOnly(index.namedb(), index.isUnique());
		this.indexMap.put(oi.name, oi, true);
		this.indexes.add(oi);
		for (IndexItemImpl item : index.items) {
			oi.add(this.getColumn(item.getField().namedb()), item.desc);
		}
	}

	final void fillWithIndexContainColumn(ArrayList<TIndex> fill,
			ArrayList<TColumn> columns) {
		for (int i = 0, c = columns.size(); i < c; i++) {
			this.fillWithIndexContainColumn(fill, columns.get(i));
		}
	}

	final void fillWithIndexContainColumn(ArrayList<TIndex> fill, TColumn column) {
		for (int i = 0, c = this.indexes.size(); i < c; i++) {
			final TIndex index = this.indexes.get(i);
			for (int j = 0, d = index.columns.size(); j < d; j++) {
				if (index.columns.get(j) == column && !fill.contains(index)) {
					fill.add(index);
				}
			}
		}
	}

	static final boolean exists(DBAdapterImpl adapter, String sql)
			throws SQLException {
		PreparedStatement ps = adapter.prepareStatement(sql);
		try {
			ResultSet rs = ps.executeQuery();
			try {
				if (rs.next()) {
					return true;
				}
				return false;
			} finally {
				rs.close();
			}
		} finally {
			adapter.freeStatement(ps);
		}
	}

	abstract void checkEmptyStatus(DBAdapterImpl adapter) throws SQLException;

	boolean tableEmpty;

}

interface DbDataType {
}

abstract class DbColumn<TTable extends DbTable<TTable, TColumn, TIndex>, TColumn extends DbColumn<TTable, TColumn, TIndex>, TIndex extends DbIndex<TTable, TColumn, TIndex>> {

	final TTable table;
	String name;

	@Override
	public final String toString() {
		return this.name;
	}

	DbColumn(TTable table, String name) {
		this.table = table;
		this.name = name;
	}

	int length;
	int precision;
	int scale;
	boolean notNull;
	String defaultVal;

	abstract TypeAlterability typeAlterable(DataType type);

	final boolean isRecid() {
		return this.name.equals(TableDefineImpl.FIELD_DBNAME_RECID);
	}

	final boolean isRecver() {
		return this.name.equals(TableDefineImpl.FIELD_DBNAME_RECVER);
	}
}

abstract class DbIndex<TTable extends DbTable<TTable, TColumn, TIndex>, TColumn extends DbColumn<TTable, TColumn, TIndex>, TIndex extends DbIndex<TTable, TColumn, TIndex>> {

	@Override
	public final String toString() {
		return this.name;
	}

	final TTable table;
	String name;
	boolean unique;

	DbIndex(TTable table, String name, boolean unique) {
		this.table = table;
		this.name = name;
		this.unique = unique;
	}

	boolean isPrimaryKey() {
		return this == this.table.primary;
	}

	final ArrayList<TColumn> columns = new ArrayList<TColumn>();
	final IntBits desc = new IntBits();

	final void add(TColumn column, boolean desc) {
		this.columns.add(column);
		if (desc) {
			this.desc.set(this.columns.size() - 1);
		}
	}

	final boolean indexedColumn(TColumn column) {
		for (int i = 0, c = this.columns.size(); i < c; i++) {
			if (this.columns.get(i) == column) {
				return true;
			}
		}
		return false;
	}

	final boolean structEquals(IndexDefineImpl di) {
		if (di.isUnique() != this.unique) {
			return false;
		}
		if (di.items.size() != this.columns.size()) {
			return false;
		}
		for (int i = 0, c = di.items.size(); i < c; i++) {
			IndexItemImpl l = di.items.get(i);
			if (!l.field.namedb().equals(this.columns.get(i).name)) {
				return false;
			}
			if (l.desc != this.desc.get(i)) {
				return false;
			}
		}
		return true;
	}
}