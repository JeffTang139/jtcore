package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.Filter;
import org.eclipse.jt.core.def.table.HierarchyDeclare;

/**
 * 级次定义实现类
 * 
 * @author Jeff Tang
 * 
 */
final class HierarchyDefineImpl extends NamedDefineImpl implements
		HierarchyDeclare {

	public final int getMaxLevel() {
		return this.maxlevel;
	}

	public final TableDefineImpl getOwner() {
		return this.owner;
	}

	public final void setMaxLevel(int maxLevel) {
		this.checkModifiable();
		if (maxLevel < 0 || HierarchyDefineImpl.MAX_LEVEL < maxLevel) {
			throw new IllegalArgumentException("错误或不支持的最大级次值");
		}
		if (maxLevel < this.maxlevel) {
			throw new IllegalArgumentException("新的最大级次值小于当前最大级次值");
		}
		this.maxlevel = maxLevel;
	}

	/**
	 * 最大级次
	 */
	static final int MAX_LEVEL = 32;

	/**
	 * 级次表的主键列名
	 */
	static final String COLUMN_NAME_RECID = TableDefineImpl.FIELD_DBNAME_RECID;

	/**
	 * 级次表的路径列名
	 */
	static final String COLUMN_NAME_PATH = "PATH";

	/**
	 * 级次表的状态列名
	 */
	static final String COLUMN_NAME_STATUS = "STATUS";

	/**
	 * 级次表的唯一索引名前缀
	 */
	static final String PATH_INDEX_PREFIX = "HX_";

	/**
	 * 状态列第0位标识-是否为叶子节点
	 */
	static final int STATUS_MASK_IS_LEAF = 1 << 0;

	/**
	 * 所属逻辑表定义
	 */
	final TableDefineImpl owner;

	/**
	 * 序号
	 */
	final int sequence;

	/**
	 * 最大级次深度
	 */
	int maxlevel;

	/**
	 * 级次表名称,大写
	 */
	String tableName;

	/**
	 * 主键索引名称
	 */
	String pkIndex;

	/**
	 * 路径索引名称
	 */
	String pathIndex;

	HierarchyDefineImpl(TableDefineImpl owner, String name, int maxLevel) {
		super(name);
		this.owner = owner;
		this.sequence = owner.hierarchySequencer.next();
		this.maxlevel = maxLevel;
		this.tableName = owner.name.toUpperCase() + "_H" + this.sequence;
	}

	final int index() {
		return this.owner.hierarchies.indexOf(this);
	}

	static final HierarchyDefineImpl newForMerge(TableDefineImpl owner,
			String name) {
		return new HierarchyDefineImpl(owner, name);
	}

	/**
	 * newForMerge
	 */
	private HierarchyDefineImpl(TableDefineImpl owner, String name) {
		super(name);
		this.owner = owner;
		this.sequence = owner.hierarchySequencer.next();
	}

	final void ensureValid(final DBLang lang, final Namespace tables,
			final Namespace indexes) {
		if (this.tableName.length() > lang.getMaxTableNameLength()
				|| tables.contains(this.tableName)
				|| lang.filterKeyword(this.tableName)) {
			this.tableName = Utils.buildIdentityName(this.tableName,
					lang.getMaxTableNameLength(), new Filter<String>() {
						public boolean accept(String item) {
							return tables.contains(item)
									|| lang.filterKeyword(item);
						}
					});
		}
		if (this.pkIndex == null) {
			this.pkIndex = TableDefineImpl.DNA_PK_PREFIX.concat(this.tableName);
		}
		final String pkname = this.pkIndex;
		if (pkname.length() > lang.getMaxIndexNameLength()
				|| lang.filterKeyword(pkname) || indexes.contains(pkname)) {
			this.pkIndex = Utils.buildIdentityName(pkname,
					lang.getMaxIndexNameLength(), new Filter<String>() {
						public boolean accept(String item) {
							return lang.filterKeyword(pkname)
									|| indexes.contains(pkname);
						}
					});
		}
	}

	final <TContainer, TElement> void ensurePathIndexValid(final DBLang lang,
			final Namespace indexes) {
		if (this.pathIndex == null) {
			this.pathIndex = PATH_INDEX_PREFIX.concat(this.tableName());
		}
		final String pathIndex = this.pathIndex;
		if (pathIndex.length() > lang.getMaxIndexNameLength()
				|| lang.filterKeyword(pathIndex) || indexes.contains(pathIndex)) {
			this.pathIndex = Utils.buildIdentityName(pathIndex,
					lang.getMaxIndexNameLength(), new Filter<String>() {
						public boolean accept(String item) {
							return lang.filterKeyword(pathIndex)
									|| indexes.contains(pathIndex);
						}
					});
		}
	}

	final String tableName() {
		if (this.tableName == null) {
			throw new NullPointerException("表名称");
		}
		return this.tableName;
	}

	final String pkIndex() {
		if (this.pkIndex == null) {
			throw new NullPointerException("主键索引名称");
		}
		return this.pkIndex;
	}

	final String pathIndex() {
		if (this.pathIndex == null) {
			throw new NullPointerException("级次路径的索引名称为空");
		}
		return this.pathIndex;
	}

	final int getPathLength() {
		return this.maxlevel * 17;
	}

	@Override
	public final String getXMLTagName() {
		return xml_name;
	}

	static final String xml_name = "hierarchy";

	final HierarchyDefineImpl clone(TableDefineImpl owner) {
		return new HierarchyDefineImpl(owner, this);
	}

	private HierarchyDefineImpl(TableDefineImpl owner,
			HierarchyDefineImpl sample) {
		super(sample);
		this.owner = owner;
		this.sequence = sample.sequence;
		this.maxlevel = sample.maxlevel;
		this.tableName = sample.tableName;
		this.pkIndex = sample.pkIndex;
		this.pathIndex = sample.pathIndex;
	}

	private volatile HierarchyMoveSql hierarchyMoveSql;

	final HierarchyMoveSql getHierarchyMoveSql(DBLang lang) {
		if (this.hierarchyMoveSql == null) {
			synchronized (this) {
				if (this.hierarchyMoveSql == null) {
					this.hierarchyMoveSql = new HierarchyMoveSql(lang, this);
				}
			}
		}
		return this.hierarchyMoveSql;
	}

}
