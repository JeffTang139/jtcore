package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.Filter;
import org.eclipse.jt.core.def.table.IndexDeclare;
import org.eclipse.jt.core.def.table.TableFieldDefine;
import org.eclipse.jt.core.exception.NullArgumentException;

/**
 * 索引定义实现类
 * 
 * @author Jeff Tang
 * 
 */
final class IndexDefineImpl extends NamedDefineImpl implements IndexDeclare {

	public final TableDefineImpl getOwner() {
		return this.owner;
	}

	public final MetaBaseContainerImpl<? extends IndexItemImpl> getItems() {
		return this.items;
	}

	public final boolean isUnique() {
		return this.unique;
	}

	public final void setUnique(boolean unique) {
		this.checkModifiable();
		this.unique = unique;
	}

	public final IndexItemImpl addItem(TableFieldDefine field) {
		if (field == null) {
			throw new NullArgumentException("索引字段");
		}
		return this.addItem((TableFieldDefineImpl) field, false);
	}

	public final IndexItemImpl addItem(TableFieldDefine field, boolean desc) {
		if (field == null) {
			throw new NullArgumentException("索引字段");
		}
		return this.addItem((TableFieldDefineImpl) field, desc);
	}

	/**
	 * 所属逻辑表定义
	 */
	final TableDefineImpl owner;

	/**
	 * 所属的物理表
	 */
	final DBTableDefineImpl dbTable;

	/**
	 * 索引字段列表
	 */
	final MetaBaseContainerImpl<IndexItemImpl> items = new MetaBaseContainerImpl<IndexItemImpl>();

	/**
	 * 是否唯一索引
	 */
	private boolean unique;

	/**
	 * 数据库中名称
	 */
	private String namedb;

	/**
	 * 构造逻辑表使用的索引定义
	 * 
	 * @param owner
	 * @param dbTable
	 * @param name
	 * @param unique
	 */
	IndexDefineImpl(TableDefineImpl owner, DBTableDefineImpl dbTable,
			String name, boolean unique) {
		super(name);
		if (owner == null) {
			throw new NullPointerException();
		}
		if (dbTable.owner != owner) {
			throw new IllegalArgumentException();
		}
		this.owner = owner;
		this.dbTable = dbTable;
		this.unique = unique;
		this.namedb = name.toUpperCase();
	}

	final String namedb() {
		return this.namedb;
	}

	final void setNamedb(String namedb) {
		if (namedb == null || namedb.length() == 0) {
			throw new NullPointerException();
		}
		this.namedb = namedb.toUpperCase();
	}

	final IndexItemImpl findItem(TableFieldDefineImpl field) {
		for (int i = 0, c = this.items.size(); i < c; i++) {
			IndexItemImpl item = this.items.get(i);
			if (item.getField() == field) {
				return item;
			}
		}
		return null;
	}

	final IndexItemImpl addItem(TableFieldDefineImpl field, boolean desc) {
		this.checkModifiable();
		if (this.findItem(field) != null) {
			throw new IllegalArgumentException("字段[" + field.name + "]在索引["
					+ this.getName() + "]中已存在.");
		}
		IndexItemImpl item = new IndexItemImpl(this, field, desc);
		this.items.add(item);
		return item;
	}

	final boolean structEquals(IndexDefineImpl index) {
		if (this == index) {
			return true;
		}
		if (this.unique != index.unique
				|| this.items.size() != index.items.size()) {
			return false;
		} else {
			for (int i = 0, c = this.items.size(); i < c; i++) {
				IndexItemImpl left = this.items.get(i);
				IndexItemImpl right = index.items.get(i);
				if (left.desc != right.desc
						|| !left.field.namedb().equals(right.field.namedb())) {
					return false;
				}
			}
			return true;
		}
	}

	/**
	 * 索引名在模式内唯一
	 * 
	 * @param lang
	 * @param namespace
	 * @return
	 */
	final boolean ensureValidWithinNamespace(final DBLang lang,
			final Namespace namespace) {
		final String namedb = this.namedb;
		final int maxlen = lang.getMaxIndexNameLength();
		if (namedb.length() > maxlen || namespace.contains(namedb)
				|| lang.filterKeyword(namedb)) {
			final String rebuild = Utils.buildIdentityName(namedb, maxlen,
					new Filter<String>() {
						public boolean accept(String item) {
							return namespace.contains(item)
									|| lang.filterKeyword(item);
						}
					});
			this.namedb = rebuild;
			return true;
		}
		return false;
	}

	@Override
	public final String getXMLTagName() {
		return xml_name;
	}

	static final String xml_name = "index";

	static final IndexDefineImpl newForMerge(TableDefineImpl table,
			DBTableDefineImpl dbTable, String name) {
		return new IndexDefineImpl(table, dbTable, name);
	}

	private IndexDefineImpl(TableDefineImpl owner, DBTableDefineImpl dbTable,
			String name) {
		super(name);
		this.owner = owner;
		this.dbTable = dbTable;
		this.namedb = name.toUpperCase();
	}

	final IndexDefineImpl clone(TableDefineImpl owner) {
		return new IndexDefineImpl(owner, this);
	}

	private IndexDefineImpl(TableDefineImpl owner, IndexDefineImpl sample) {
		super(sample);
		this.owner = owner;
		this.namedb = sample.namedb;
		this.dbTable = owner.dbTables.get(sample.dbTable.name);
		this.unique = sample.unique;
		for (int i = 0, c = sample.items.size(); i < c; i++) {
			this.items.add(sample.items.get(i).clone(this));
		}
	}

	@Override
	final void assignFrom(Object sample) {
		super.assignFrom(sample);
		IndexDefineImpl index = (IndexDefineImpl) sample;
		this.unique = index.unique;
		for (int i = 0, c = index.items.size(); i < c; i++) {
			IndexItemImpl from = index.items.get(i);
			TableFieldDefineImpl f = this.owner.fields.get(from.field.name);
			IndexItemImpl to = this.findItem(f);
			if (to == null) {
				to = from.clone(this);
				this.items.add(i, to);
			} else {
				this.items.ensureElementAt(to, i);
				to.assignFrom(from);
			}
		}
		this.items.trunc(index.items.size());
	}

}
