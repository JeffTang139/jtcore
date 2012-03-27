package org.eclipse.jt.core.impl;

class TSetFunction extends Token {
	public final NAggregateExpr.Func value;

	public TSetFunction(NAggregateExpr.Func func, int line, int col, int length) {
		super(line, col, length);
		this.value = func;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof TSetFunction) {
			return ((TSetFunction) obj).value == this.value;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.value.hashCode();
	}
}
