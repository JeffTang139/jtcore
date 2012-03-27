package org.eclipse.jt.core.impl;

/**
 * ³¤ÕûÐÍ·ûºÅ
 * 
 * @author Jeff Tang
 * 
 */
class TLong extends Token {
	public final long value;

	public TLong(long value, int line, int col, int length) {
		super(line, col, length);
		this.value = value;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof TLong) {
			return ((TLong) obj).value == this.value;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return (int) this.value;
	}
}
