package org.eclipse.jt.core.impl;

/**
 * 参数引用表达式节点
 * 
 * @author Jeff Tang
 * 
 */
class NVarRefExpr implements NValueExpr {
	public final TString name;
	public final NVarRefExpr owner;
	final String argumentName;

	public NVarRefExpr(NVarRefExpr owner, TString name) {
		this.owner = owner;
		this.name = name;
		// 删除@符号
		this.argumentName = name.value.substring(1);
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

	public <T> void accept(T visitorContext, SQLVisitor<T> visitor) {
		visitor.visitVarRefExpr(visitorContext, this);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof NVarRefExpr) {
			return ((NVarRefExpr) obj).argumentName.equals(this.argumentName);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.argumentName.hashCode();
	}

	@Override
	public String toString() {
		return RenderVisitor.render(this);
	}
}
