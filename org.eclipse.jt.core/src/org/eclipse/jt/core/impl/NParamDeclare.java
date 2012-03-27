package org.eclipse.jt.core.impl;

/**
 * ²ÎÊýÉùÃ÷
 * 
 * @author Jeff Tang
 * 
 */
class NParamDeclare implements TextLocalizable, SQLVisitable {
	public enum InOut {
		IN, OUT, INOUT
	}

	public static final NParamDeclare EMPTY = new NParamDeclare(InOut.IN,
			new TString("@", 0, 0, 0), NDataType.UNKNOWN, false, null);

	public final InOut modifier;
	public final TString name;
	public final NDataType type;
	public final boolean notNull;
	public final NLiteral defaultValue;
	final String argumentName;

	public NParamDeclare(InOut modifier, TString name, NDataType type,
			boolean notNull, NLiteral defaultValue) {
		this.modifier = modifier;
		this.name = name;
		this.type = type;
		this.notNull = notNull;
		this.defaultValue = defaultValue;
		this.argumentName = name.value.substring(1);
	}

	public int startLine() {
		return this.name.startLine();
	}

	public int startCol() {
		return this.name.startCol();
	}

	public int endLine() {
		return this.name.endLine();
	}

	public int endCol() {
		return this.name.endCol();
	}

	public <T> void accept(T visitorContext, SQLVisitor<T> visitor) {
		visitor.visitParamDeclare(visitorContext, this);
	}

	@Override
	public String toString() {
		return RenderVisitor.render(this);
	}
}
