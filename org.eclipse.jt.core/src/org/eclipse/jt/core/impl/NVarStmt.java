package org.eclipse.jt.core.impl;

/**
 * ±äÁ¿ÉùÃ÷
 * 
 * @author Jeff Tang
 * 
 */
class NVarStmt implements NStatement {
	private final int startLine;
	private final int startCol;
	private final int endLine;
	private final int endCol;

	public final TString name;
	public final NDataType type;
	public final NValueExpr init;

	public NVarStmt(TextLocalizable start, TString name, NDataType type,
			NValueExpr init) {
		this.name = name;
		this.type = type;
		this.init = init;
		this.startLine = start.startLine();
		this.startCol = start.startCol();
		if (init != null) {
			this.endLine = init.endLine();
			this.endCol = init.endCol();
		} else {
			this.endLine = this.name.endLine();
			this.endCol = this.name.endCol();
		}
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

	public <T> void accept(T visitorContext, SQLVisitor<T> visitor) {
		visitor.visitVarStmt(visitorContext, this);
	}

	@Override
	public String toString() {
		return RenderVisitor.render(this);
	}
}
