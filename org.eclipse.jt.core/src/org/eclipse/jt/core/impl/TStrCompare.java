package org.eclipse.jt.core.impl;

/**
 * ×Ö·û´®±È½Ï¹Ø¼ü×Ö
 * 
 * @author Jeff Tang
 * 
 */
class TStrCompare extends Token {
	public final NStrCompareExpr.Keywords value;

	public TStrCompare(NStrCompareExpr.Keywords value, int line, int col,
			int length) {
		super(line, col, length);
		this.value = value;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof TStrCompare) {
			return ((TStrCompare) obj).value == this.value;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.value.hashCode();
	}
}
