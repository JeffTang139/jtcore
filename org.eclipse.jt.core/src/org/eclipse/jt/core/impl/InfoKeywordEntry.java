package org.eclipse.jt.core.impl;

/**
 * 关键字
 * 
 * @author Jeff Tang
 * 
 */
final class InfoKeywordEntry {
	private Enum<?> one;
	private Enum<?>[] others;

	InfoKeywordEntry(InfoKeywordEntry clone) {
		this.one = clone.one;
		if (clone.others != null) {
			this.others = clone.others.clone();
		}
	}

	InfoKeywordEntry(Enum<?> one, Enum<?>[] others) {
		this.setKeywords(one, others);
	}

	private final Enum<?> removeOther(int index) {
		Enum<?> r = this.others[index];
		int ol = this.others.length;
		if (ol == 1) {
			this.others = null;
		} else {
			Enum<?>[] newOthers = new Enum[ol - 1];
			for (int j = 0; j < index; j++) {
				newOthers[j] = this.others[j];
			}
			for (int j = index + 1; j < ol; j++) {
				newOthers[j - 1] = this.others[j];
			}
			this.others = newOthers;
		}
		return r;
	}

	/**
	 * 删除关键字，如果删除后该项为空则返回true
	 */
	final boolean removeKeyword(Enum<?> keyword) {
		if (keyword == this.one) {
			if (this.others == null) {
				return true;
			} else {
				this.one = this.removeOther(0);
			}
		} else if (this.others != null) {
			for (int i = 0; i < this.others.length; i++) {
				if (this.others[i] == keyword) {
					this.removeOther(i);
					break;
				}
			}
		}
		return false;
	}

	final void addKeyword(Enum<?> keyword) {
		if (keyword == this.one) {
			return;
		} else if (this.others != null) {
			int ol = this.others.length;
			for (int i = 0; i < ol; i++) {
				if (this.others[i] == keyword) {
					return;
				}
			}
			Enum<?>[] newOthers = new Enum[ol - 1];
			for (int i = 0; i < ol; i++) {
				newOthers[i] = this.others[i];
			}
			newOthers[ol] = keyword;
			this.others = newOthers;
		} else {
			this.others = new Enum[] { keyword };
		}
	}

	static Class<?> enumClassOf(Enum<?> keyword) {
		Class<?> clazz = keyword.getClass();
		Class<?> sClass = clazz.getSuperclass();
		if (sClass == Enum.class) {
			return clazz;
		} else {
			return sClass;
		}
	}

	final void setKeywords(Enum<?> one, Enum<?>[] others) {
		this.one = one;
		this.others = others != null && others.length > 0 ? others : null;
	}

	final boolean match(Class<?> type) {
		return enumClassOf(this.one) == type;
	}
}
