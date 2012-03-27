package org.eclipse.jt.core.impl;

/**
 * ²¼¶ûÐÍ·ûºÅ
 * 
 * @author Jeff Tang
 * 
 */
class TBoolean extends Token {
	public final boolean value;

	public TBoolean(boolean value, int line, int col, int length) {
		super(line, col, length);
		this.value = value;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof TBoolean) {
			return ((TBoolean) obj).value == this.value;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.value ? Boolean.TRUE.hashCode() : Boolean.FALSE.hashCode();
	}
}
