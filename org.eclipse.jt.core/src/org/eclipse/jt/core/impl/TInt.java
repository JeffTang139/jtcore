package org.eclipse.jt.core.impl;

/**
 * ÕûÐÍ·ûºÅ
 * 
 * @author Jeff Tang
 * 
 */
class TInt extends Token {
	public static final TInt EMPTY = new TInt(0, 0, 0, 0);

	public final int value;

	public TInt(int value, int line, int col, int length) {
		super(line, col, length);
		this.value = value;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof TInt) {
			return ((TInt) obj).value == this.value;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.value;
	}
}
