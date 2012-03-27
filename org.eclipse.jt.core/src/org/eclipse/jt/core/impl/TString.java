package org.eclipse.jt.core.impl;

/**
 * ×Ö·û´®ÐÍ·ûºÅ
 * 
 * @author Jeff Tang
 * 
 */
class TString extends Token {
	public static final TString EMPTY = new TString("", 0, 0, 0);

	public final String value;

	public TString(String value, int line, int col, int length) {
		super(line, col, length);
		this.value = value;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof TString) {
			String s = ((TString) obj).value;
			if (s == null) {
				return this.value == null;
			}
			return s.equals(this.value);
		}
		return false;
	}

	@Override
	public int hashCode() {
		if (this.value == null) {
			return 0;
		}
		return this.value.hashCode();
	}
	
	@Override
	public String toString() {
		return this.value;
	}
}
