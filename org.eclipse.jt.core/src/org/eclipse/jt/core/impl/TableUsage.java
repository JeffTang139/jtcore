package org.eclipse.jt.core.impl;

import java.util.Iterator;

/**
 * 数据库对象的使用状况
 * 
 * @author Jeff Tang
 * 
 */
final class TableUsage {

	final TableDefineImpl target;

	final IntBits dbTableUsage = new IntBits();

	final IntBits hierarchyUsage = new IntBits();

	TableUsage(TableRef tableRef) {
		this.target = tableRef.getTarget();
	}

	/**
	 * 清空所有对数据库对象的使用状况记录
	 */
	public final void clear() {
		this.dbTableUsage.clear();
		this.hierarchyUsage.clear();
	}

	/**
	 * 标记使用物理表
	 */
	public final void use(DBTableDefineImpl dbTable) {
		if (dbTable.owner != this.target) {
			throw new IllegalArgumentException("物理表所在逻辑表与当前表引用目标不同.");
		}
		this.dbTableUsage.set(dbTable.index());
	}

	public final void useTables(IntBits bits) {
		this.dbTableUsage.or(bits);
	}

	/**
	 * 第一个使用的物理表
	 */
	public final DBTableDefineImpl firstTable() {
		int index = this.dbTableUsage.nextSetBit(0);
		if (index >= 0) {
			return this.target.dbTables.get(index);
		}
		return null;
	}

	/**
	 * 使用的物理表个数
	 */
	public final int tableCount() {
		return this.dbTableUsage.cardinality();
	}

	/**
	 * 是否使用的物理表
	 */
	public final boolean hasUsed(DBTableDefineImpl dbTable) {
		return this.dbTableUsage.get(dbTable.index());
	}

	/**
	 * 返回标记使用的物理表的迭代器
	 * 
	 * @return
	 */
	public final Iterable<DBTableDefineImpl> tables() {
		return new Iterable<DBTableDefineImpl>() {
			public Iterator<DBTableDefineImpl> iterator() {
				return new Iterator<DBTableDefineImpl>() {

					private DBTableDefineImpl next = this.findNext(0);

					private DBTableDefineImpl findNext(int from) {
						int index = TableUsage.this.dbTableUsage
								.nextSetBit(from);
						if (index >= 0) {
							return TableUsage.this.target.dbTables.get(index);
						}
						return null;
					}

					public boolean hasNext() {
						return this.next != null;
					}

					public DBTableDefineImpl next() {
						DBTableDefineImpl current = this.next;
						int index = TableUsage.this.target.dbTables
								.indexOf(current);
						this.next = this.findNext(index + 1);
						return current;
					}

					public void remove() {
						throw new UnsupportedOperationException();
					}
				};
			}
		};
	}

	/**
	 * 标记使用级次定义
	 */
	public final void use(HierarchyDefineImpl hierarchy) {
		if (hierarchy.owner != this.target) {
			throw new IllegalArgumentException("物理表所在逻辑表与当前表引用目标不同.");
		}
		this.hierarchyUsage.set(hierarchy.index());
	}

	/**
	 * 第一个使用的级次定义
	 */
	public final HierarchyDefineImpl firstHierarchy() {
		int index = this.hierarchyUsage.nextSetBit(0);
		if (index >= 0) {
			return this.target.hierarchies.get(index);
		}
		return null;
	}

	/**
	 * 使用的级次定义的个数
	 */
	public final int hierarchyCount() {
		return this.hierarchyUsage.cardinality();
	}

	/**
	 * 是否使用了级次定义
	 */
	public final boolean hasUsed(HierarchyDefineImpl hierarchy) {
		return this.hierarchyUsage.get(hierarchy.index());
	}

	public final Iterable<HierarchyDefineImpl> hierarchies() {
		return new Iterable<HierarchyDefineImpl>() {
			public Iterator<HierarchyDefineImpl> iterator() {
				return new Iterator<HierarchyDefineImpl>() {

					private HierarchyDefineImpl next = this.findNext(0);

					private HierarchyDefineImpl findNext(int from) {
						int index = TableUsage.this.hierarchyUsage
								.nextSetBit(from);
						if (index >= 0) {
							return TableUsage.this.target.hierarchies
									.get(index);
						}
						return null;
					}

					public void remove() {
						throw new UnsupportedOperationException();
					}

					public HierarchyDefineImpl next() {
						HierarchyDefineImpl current = this.next;
						int index = current.owner.hierarchies.indexOf(current);
						this.next = this.findNext(index + 1);
						return current;
					}

					public boolean hasNext() {
						return this.next != null;
					}
				};
			}
		};
	}

}
