package org.eclipse.jt.core.impl;

/**
 * 输出列引用
 * 
 * @author Jeff Tang
 * 
 */
class NNameRef implements TextLocalizable {
	public static final NNameRef EMPTY = new NNameRef(TString.EMPTY);

	public final TString name;

	public NNameRef(TString name) {
		this.name = name;
	}

	public int startLine() {
		return this.name.line;
	}

	public int startCol() {
		return this.name.col;
	}

	public int endLine() {
		return this.name.line;
	}

	public int endCol() {
		return this.name.col + this.name.length;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof NNameRef) {
			return this.name.equals(((NNameRef) obj).name);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.name.hashCode();
	}
}
