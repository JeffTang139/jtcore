package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.spi.sql.SQLValueFormatException;
import org.eclipse.jt.core.type.DataType;
import org.eclipse.jt.core.type.GUID;
import org.eclipse.jt.core.type.TypeFactory;
import org.eclipse.jt.core.type.ValueConvertException;


/**
 * GUID型字面量节点
 * 
 * @author Jeff Tang
 * 
 */
class NLiteralGUID extends NLiteral {
	public static final NLiteralGUID EMPTY = new NLiteralGUID(new TString(
			GUID.emptyID.toString(), 0, 0, 0));
	public final GUID value;

	public NLiteralGUID(TString value) {
		super(value);
		this.value = parseGUID(value);
	}

	@Override
	public DataType getType() {
		return TypeFactory.GUID;
	}

	public <T> void accept(T visitorContext, SQLVisitor<T> visitor) {
		visitor.visitLiteralGUID(visitorContext, this);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof NLiteralGUID) {
			return ((NLiteralGUID) obj).value.equals(this.value);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.value.hashCode();
	}

	private static GUID parseGUID(TString guid) {
		try {
			return GUID.valueOf(guid.value);
		} catch (ValueConvertException vce) {
			throw new SQLValueFormatException(guid.line, guid.col,
					"GUID格式不正确 '" + guid.value + "'");
		}
	}

	@Override
	public String toString() {
		return RenderVisitor.render(this);
	}
}
