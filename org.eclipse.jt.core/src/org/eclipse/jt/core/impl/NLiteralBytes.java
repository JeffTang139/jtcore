package org.eclipse.jt.core.impl;

import java.util.Arrays;

import org.eclipse.jt.core.spi.sql.SQLValueFormatException;
import org.eclipse.jt.core.type.DataType;
import org.eclipse.jt.core.type.TypeFactory;
import org.eclipse.jt.core.type.ValueConvertException;


/**
 * BYTES字面量节点
 * 
 * @author Jeff Tang
 * 
 */
class NLiteralBytes extends NLiteral {
	public static final NLiteralBytes EMPTY = new NLiteralBytes(TString.EMPTY);

	public final byte[] value;

	public NLiteralBytes(TString value) {
		super(value);
		this.value = parseBytes(value);
	}

	@Override
	public DataType getType() {
		return TypeFactory.BYTES;
	}

	public <T> void accept(T visitorContext, SQLVisitor<T> visitor) {
		visitor.visitLiteralBytes(visitorContext, this);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof NLiteralBytes) {
			return Arrays.equals(((NLiteralBytes) obj).value, this.value);
		}
		return false;
	}

	@Override
	public int hashCode() {
		int l = this.value.length;
		if (l > 0) {
			return (this.value[l - 1] << 16) ^ (this.value[0] << 8)
					^ this.value.length;
		}
		return 0;
	}

	private static byte[] parseBytes(TString bytes) {
		String s = bytes.value;
		int len = s.length();
		if ((len & 1) != 0) {
			throw new SQLValueFormatException(bytes.line, bytes.col,
					"BYTES字符串长度为奇数 '" + s + "'");
		}
		try {
			byte[] buf = new byte[len >> 1];
			int i = 0, j = 0;
			while (i < len) {
				buf[j++] = (byte) ((parseChar(s, i++) << 4) + parseChar(s, i++));
			}
			return buf;
		} catch (ValueConvertException vce) {
			throw new SQLValueFormatException(bytes.line, bytes.col,
					"BYTES格式不正确 '" + s + "'");
		} catch (StringIndexOutOfBoundsException sbe) {
			throw new SQLValueFormatException(bytes.line, bytes.col,
					"BYTES格式不正确 '" + s + "'");
		}
	}

	private final static int h2b_A_10 = 'A' - 10;
	private final static int h2b_a_10 = 'a' - 10;

	private static int parseChar(String s, int offset)
			throws ValueConvertException, StringIndexOutOfBoundsException {
		char c = s.charAt(offset);
		if (c < '0') {
		} else if (c <= '9') {
			return c - '0';
		} else if (c < 'A') {
		} else if (c <= 'F') {
			return c - h2b_A_10;
		} else if (c < 'a') {
		} else if (c <= 'f') {
			return c - h2b_a_10;
		}
		throw new ValueConvertException("在偏移量" + offset + "处出现无效的十六进制字符'" + c
				+ "'");
	}
	
	@Override
	public String toString() {
		return RenderVisitor.render(this);
	}
}
