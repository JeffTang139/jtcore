package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.type.DataType;
import org.eclipse.jt.core.type.TypeFactory;

/**
 * 整型字面量节点
 * 
 * @author Jeff Tang
 * 
 */
class NLiteralInt extends NLiteral {
	public int value;

	public NLiteralInt(TInt value) {
		super(value);
		this.value = value.value;
	}

	@Override
	public DataType getType() {
		return TypeFactory.INT;
	}

	public <T> void accept(T visitorContext, SQLVisitor<T> visitor) {
		visitor.visitLiteralInt(visitorContext, this);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof NLiteralInt) {
			return ((NLiteralInt) obj).value == this.value;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.value;
	}

	@Override
	public String toString() {
		return RenderVisitor.render(this);
	}
}
