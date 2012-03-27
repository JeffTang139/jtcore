package org.eclipse.jt.core.impl;

/**
 * ¸¡µãÐÍ·ûºÅ
 * 
 * @author Jeff Tang
 * 
 */
class TDouble extends Token {
	public final double value;

	public TDouble(double value, int line, int col, int length) {
		super(line, col, length);
		this.value = value;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof TDouble) {
			return Double.compare(((TDouble) obj).value, this.value) == 0;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return (int) this.value;
	}
}
