package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.info.Multiple;

abstract class InfoBase extends NamedDefineImpl {

	private InfoKeywordEntry[] keywordEntrys;
	private int keywordEntryCount;

	private final int keywordEntryIndexOf(Class<?> clazz) {
		for (int i = 0; i < this.keywordEntryCount; i++) {
			if (this.keywordEntrys[i].match(clazz)) {
				return i;
			}
		}
		return -1;
	}

	private final <TKeyword extends Enum<?>> void addKeywordEntry(TKeyword one,
	        TKeyword[] others) {
		int cap = this.keywordEntrys == null ? 0 : this.keywordEntrys.length;
		if (cap <= this.keywordEntryCount) {
			InfoKeywordEntry[] newKeywords = new InfoKeywordEntry[cap + 3];
			if (this.keywordEntryCount > 0) {
				System.arraycopy(this.keywordEntrys, 0, newKeywords, 0,
				        this.keywordEntryCount);
			}
			this.keywordEntrys = newKeywords;
		}
		this.keywordEntrys[this.keywordEntryCount++] = new InfoKeywordEntry(
		        one, others);
	}

	private final void removeKeywordEntry(int index) {
		if (--this.keywordEntryCount > index) {
			System.arraycopy(this.keywordEntrys, index + 1, this.keywordEntrys,
			        index, this.keywordEntryCount - index);
		}
		this.keywordEntrys[this.keywordEntryCount] = null;
	}

	/**
	 * 添加关键字
	 * 
	 * @param keyword
	 *            关键字
	 */
	public final void addKeyword(Enum<?> keyword) {
		Class<?> clazz = InfoKeywordEntry.enumClassOf(keyword);
		int index = this.keywordEntryIndexOf(clazz);
		if (index < 0) {
			this.addKeywordEntry(keyword, null);
		} else if (clazz.getAnnotation(Multiple.class) != null) {
			this.keywordEntrys[index].addKeyword(keyword);
		} else {
			throw new UnsupportedOperationException("关键字未被声明成可以组合使用！");
		}
	}

	/**
	 * 设置某类关键字
	 * 
	 * @param <TKeyword>
	 *            关键字类型
	 * @param one
	 *            第一个关键字
	 * @param others
	 *            其余的关键字
	 */
	public final <TKeyword extends Enum<TKeyword>> void setKeywords(
	        TKeyword one, TKeyword... others) {
		Class<?> clazz = InfoKeywordEntry.enumClassOf(one);
		int index = this.keywordEntryIndexOf(clazz);
		if (index >= 0) {
			this.keywordEntrys[index].setKeywords(one, others);
		} else {
			this.addKeywordEntry(one, others);
		}
	}

	/**
	 * 设置某类关键字
	 * 
	 * @param <TKeyword>
	 *            关键字类型
	 * @param one
	 *            第一个关键字
	 * @param others
	 *            其余的关键字
	 */
	public final <TKeyword extends Enum<TKeyword>> void setKeywords(TKeyword one) {
		this.setKeywords(one, (TKeyword[]) null);
	}

	/**
	 * 移除某类关键字
	 * 
	 * @param keywordType
	 *            关键字类
	 */
	public final void removeKeywords(Class<? extends Enum<?>> keywordType) {
		int index = this.keywordEntryIndexOf(keywordType);
		if (index >= 0) {
			this.removeKeywordEntry(index);
		}
	}

	/**
	 * 移除关键字
	 * 
	 * @return 返回之前该关键字是否存在
	 */
	public boolean removeKeyword(Enum<?> keyword) {
		Class<?> clazz = InfoKeywordEntry.enumClassOf(keyword);
		int index = this.keywordEntryIndexOf(clazz);
		if (index >= 0 && this.keywordEntrys[index].removeKeyword(keyword)) {
			this.removeKeywordEntry(index);
		}
		return index >= 0;
	}

	public InfoBase(String name, InfoBase inherited) {
		super(name);
		if (inherited != null && inherited.keywordEntryCount > 0) {
			this.keywordEntrys = new InfoKeywordEntry[inherited.keywordEntryCount];
			for (int i = 0; i < inherited.keywordEntryCount; i++) {
				this.keywordEntrys[i] = new InfoKeywordEntry(
				        inherited.keywordEntrys[i]);
			}
		}
	}
}
