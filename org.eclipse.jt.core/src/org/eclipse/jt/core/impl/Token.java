package org.eclipse.jt.core.impl;

/**
 * 符号值 Token的派生类：TInt,TLong,TString,TBoolean,TDouble,TFunction
 * 名称中的类型不表示符号的类型，而表示符号返回值的类型。
 * 
 * @author Jeff Tang
 * 
 */
class Token implements TextLocalizable {
	public static final Token EMPTY = new Token(0, 0, 0);
	
	public final int line;
	public final int col;
	public final int length;

	public Token(int line, int col, int length) {
		this.line = line;
		this.col = col;
		this.length = length;
	}

	public int startLine() {
		return this.line;
	}

	public int startCol() {
		return this.col;
	}

	public int endLine() {
		return this.line;
	}

	public int endCol() {
		return this.col + this.length;
	}
}
