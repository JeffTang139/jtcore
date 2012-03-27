package org.eclipse.jt.core.impl;

/**
 * ¼¶´Î¹Ø¼ü×Ö
 * 
 * @author Jeff Tang
 * 
 */
class THierarchy extends Token {
	public final NHierarchyExpr.Keywords value;

	public THierarchy(NHierarchyExpr.Keywords value, int line, int col,
			int length) {
		super(line, col, length);
		this.value = value;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof THierarchy) {
			return ((THierarchy) obj).value == this.value;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.value.hashCode();
	}
}
