package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.table.IndexItemDeclare;

/**
 * 索引项定义
 * 
 * @author Jeff Tang
 * 
 */
class IndexItemImpl extends DefineBaseImpl implements IndexItemDeclare {

	public final void setDesc(boolean desc) {
		this.checkModifiable();
		this.desc = desc;
	}

	public final TableFieldDefineImpl getField() {
		return this.field;
	}

	public final boolean isDesc() {
		return this.desc;
	}

	final IndexDefineImpl index;

	TableFieldDefineImpl field;

	boolean desc;

	IndexItemImpl(IndexDefineImpl index, TableFieldDefineImpl field,
			boolean desc) {
		if (index.owner != field.owner) {
			throw new IllegalArgumentException("不在相同逻辑表.");
		}
		if (index.dbTable != field.dbTable) {
			throw new IllegalArgumentException("不在相同的物理表.");
		}
		this.index = index;
		this.field = field;
		this.desc = desc;
	}

	static final IndexItemImpl newForMerge(IndexDefineImpl index) {
		return new IndexItemImpl(index);
	}

	private IndexItemImpl(IndexDefineImpl index) {
		this.index = index;
	}

	final IndexItemImpl clone(IndexDefineImpl index) {
		return new IndexItemImpl(index, this);
	}

	/**
	 * 克隆的构造方法
	 * 
	 * @param index
	 * @param sample
	 */
	private IndexItemImpl(IndexDefineImpl index, IndexItemImpl sample) {
		super(sample);
		this.index = index;
		this.field = index.owner.fields.get(sample.field.name);
		this.desc = sample.desc;
	}

	static final IndexItemImpl newForMerge(IndexDefineImpl index,
			TableFieldDefineImpl field) {
		return new IndexItemImpl(index, field);
	}

	IndexItemImpl(IndexDefineImpl index, TableFieldDefineImpl field) {
		this.index = index;
		this.field = field;
	}

	@Override
	public final String getXMLTagName() {
		return xml_name;
	}

	static final String xml_name = "index-item";

}
