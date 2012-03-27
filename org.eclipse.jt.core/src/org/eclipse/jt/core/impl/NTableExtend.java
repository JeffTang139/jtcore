package org.eclipse.jt.core.impl;


/**
 * 扩展表节点
 * 
 * @author Jeff Tang
 * 
 */
class NTableExtend implements TextLocalizable {
	private final int startLine;
	private final int startCol;
	private final int endLine;
	private final int endCol;

	public final TString name;
	public final NTableField[] fields;

	public NTableExtend(Token start, TString name, NTableField[] fields) {
		this.name = name;
		this.fields = fields;
		this.startLine = start.line;
		this.startCol = start.col;
		NTableField f = fields[fields.length - 1];
		this.endLine = f.endLine();
		this.endCol = f.endCol();
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
}
