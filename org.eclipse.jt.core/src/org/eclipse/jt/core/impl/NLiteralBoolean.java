package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.type.DataType;
import org.eclipse.jt.core.type.TypeFactory;

/**
 * 布尔型字面量节点
 * 
 * @author Jeff Tang
 * 
 */
class NLiteralBoolean extends NLiteral {
	public final boolean value;

	public NLiteralBoolean(TBoolean value) {
		super(value);
		this.value = value.value;
	}

	@Override
	public DataType getType() {
		return TypeFactory.BOOLEAN;
	}

	public <T> void accept(T visitorContext, SQLVisitor<T> visitor) {
		visitor.visitLiteralBoolean(visitorContext, this);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof NLiteralBoolean) {
			return ((NLiteralBoolean) obj).value == this.value;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.value ? Boolean.TRUE.hashCode() : Boolean.FALSE.hashCode();
	}

	@Override
	public String toString() {
		return RenderVisitor.render(this);
	}
}
