package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.spi.sql.SQLValueFormatException;
import org.eclipse.jt.core.type.DataType;
import org.eclipse.jt.core.type.DateParser;
import org.eclipse.jt.core.type.TypeFactory;
import org.eclipse.jt.core.type.ValueConvertException;


/**
 * 日期型字面量节点
 * 
 * @author Jeff Tang
 * 
 */
class NLiteralDate extends NLiteral {
	public static final NLiteralDate EMPTY = new NLiteralDate(new TString(
			"1900-01-01", 0, 0, 0));
	public final long value;

	public NLiteralDate(TString value) {
		super(value);
		this.value = parseDate(value);
	}

	@Override
	public DataType getType() {
		return TypeFactory.DATE;
	}

	public <T> void accept(T visitorContext, SQLVisitor<T> visitor) {
		visitor.visitLiteralDate(visitorContext, this);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof NLiteralDate) {
			return ((NLiteralDate) obj).value == this.value;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return (int) this.value;
	}

	private static long parseDate(TString date) {
		try {
			return DateParser.parse(date.value);
		} catch (IllegalArgumentException ex) {
			throw new SQLValueFormatException(date.line, date.col, "日期格式不正确 '"
					+ date.value + "'");
		} catch (ValueConvertException ex) {
			throw new SQLValueFormatException(date.line, date.col, "日期格式不正确 '"
					+ date.value + "'");
		}
	}

	@Override
	public String toString() {
		return RenderVisitor.render(this);
	}
}
