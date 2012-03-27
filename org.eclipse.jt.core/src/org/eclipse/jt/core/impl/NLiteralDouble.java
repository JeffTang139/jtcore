package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.type.DataType;
import org.eclipse.jt.core.type.TypeFactory;

/**
 * 浮点型字面量节点
 * 
 * @author Jeff Tang
 * 
 */
class NLiteralDouble extends NLiteral {
	public double value;

	public NLiteralDouble(TDouble value) {
		super(value);
		this.value = value.value;
	}

	@Override
	public DataType getType() {
		return TypeFactory.DOUBLE;
	}

	public <T> void accept(T visitorContext, SQLVisitor<T> visitor) {
		visitor.visitLiteralDouble(visitorContext, this);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof NLiteralDouble) {
			return Double.compare(((NLiteralDouble) obj).value, this.value) == 0;
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
