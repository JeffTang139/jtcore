package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.type.DataType;
import org.eclipse.jt.core.type.TypeFactory;

/**
 * 长整型字面量节点
 * 
 * @author Jeff Tang
 * 
 */
class NLiteralLong extends NLiteral {
	public long value;

	public NLiteralLong(TLong value) {
		super(value);
		this.value = value.value;
	}

	@Override
	public DataType getType() {
		return TypeFactory.LONG;
	}

	public <T> void accept(T visitorContext, SQLVisitor<T> visitor) {
		visitor.visitLiteralLong(visitorContext, this);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof NLiteralLong) {
			return ((NLiteralLong) obj).value == this.value;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return (int) this.value;
	}

	@Override
	public String toString() {
		return RenderVisitor.render(this);
	}
}
