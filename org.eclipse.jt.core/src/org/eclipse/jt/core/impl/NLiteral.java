package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.type.DataType;

/**
 * 字面量节点
 * 
 * @author Jeff Tang
 * 
 */
abstract class NLiteral implements NValueExpr {
	private final int startLine;
	private final int startCol;
	private final int endLine;
	private final int endCol;

	public NLiteral(Token value) {
		this.startLine = value.line;
		this.startCol = value.col;
		this.endLine = value.line;
		this.endCol = value.col + value.length;
	}

	public int startLine() {
		return this.startLine;
	}

	public int startCol() {
		return this.startCol;
	}

	public int endLine() {
		return this.endLine;
	}

	public int endCol() {
		return this.endCol;
	}

	public abstract DataType getType();
}
