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
	 * ���ӹؼ���
	 * 
	 * @param keyword
	 *            �ؼ���
	 */
	public final void addKeyword(Enum<?> keyword) {
		Class<?> clazz = InfoKeywordEntry.enumClassOf(keyword);
		int index = this.keywordEntryIndexOf(clazz);
		if (index < 0) {
			this.addKeywordEntry(keyword, null);
		} else if (clazz.getAnnotation(Multiple.class) != null) {
			this.keywordEntrys[index].addKeyword(keyword);
		} else {
			throw new UnsupportedOperationException("�ؼ���δ�������ɿ������ʹ�ã�");
		}
	}

	/**
	 * ����ĳ��ؼ���
	 * 
	 * @param <TKeyword>
	 *            �ؼ�������
	 * @param one
	 *            ��һ���ؼ���
	 * @param others
	 *            ����Ĺؼ���
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
	 * ����ĳ��ؼ���
	 * 
	 * @param <TKeyword>
	 *            �ؼ�������
	 * @param one
	 *            ��һ���ؼ���
	 * @param others
	 *            ����Ĺؼ���
	 */
	public final <TKeyword extends Enum<TKeyword>> void setKeywords(TKeyword one) {
		this.setKeywords(one, (TKeyword[]) null);
	}

	/**
	 * �Ƴ�ĳ��ؼ���
	 * 
	 * @param keywordType
	 *            �ؼ�����
	 */
	public final void removeKeywords(Class<? extends Enum<?>> keywordType) {
		int index = this.keywordEntryIndexOf(keywordType);
		if (index >= 0) {
			this.removeKeywordEntry(index);
		}
	}

	/**
	 * �Ƴ��ؼ���
	 * 
	 * @return ����֮ǰ�ùؼ����Ƿ����
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