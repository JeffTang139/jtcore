package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.type.DataType;
import org.eclipse.jt.core.type.TypeFactory;

/**
 * 字符串字面量节点
 * 
 * @author Jeff Tang
 * 
 */
class NLiteralString extends NLiteral {
	public final String value;

	public NLiteralString(TString value) {
		super(value);
		this.value = value.value;
	}

	@Override
	public DataType getType() {
		return TypeFactory.STRING;
	}

	public <T> void accept(T visitorContext, SQLVisitor<T> visitor) {
		visitor.visitLiteralString(visitorContext, this);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof NLiteralString) {
			String s = ((NLiteralString) obj).value;
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
		return RenderVisitor.render(this);
	}
}
