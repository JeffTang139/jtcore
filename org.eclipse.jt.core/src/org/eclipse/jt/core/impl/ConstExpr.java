package org.eclipse.jt.core.impl;

import java.util.Date;

import org.eclipse.jt.core.def.exp.ConstExpression;
import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.misc.SXElement;
import org.eclipse.jt.core.type.Convert;
import org.eclipse.jt.core.type.DataType;
import org.eclipse.jt.core.type.GUID;
import org.eclipse.jt.core.type.SequenceDataType;
import org.eclipse.jt.core.type.TypeDetector;
import org.eclipse.jt.core.type.TypeDetectorBase;


public abstract class ConstExpr extends ValueExpr implements ConstExpression {

	public final static class ConstExpressionBuilderImpl implements
			ConstExpressionBuilder {

		public final ConstExpression expOf(Object value) {
			return ConstExpr.expOf(value);
		}

		public ConstExpression expOf(boolean value) {
			return BooleanConstExpr.valueOf(value);
		}

		public ConstExpression expOf(byte value) {
			return ByteConstExpr.valueOf(value);
		}

		public ConstExpression expOf(short value) {
			return ShortConstExpr.valueOf(value);
		}

		public ConstExpression expOf(char value) {
			return StringConstExpr.valueOf(String.valueOf(value));
		}

		public ConstExpression expOf(int value) {
			return IntConstExpr.valueOf(value);
		}

		public ConstExpression expOf(long value) {
			return LongConstExpr.valueOf(value);
		}

		public ConstExpression expOf(float value) {
			return FloatConstExpr.valueOf(value);
		}

		public ConstExpression expOf(double value) {
			return DoubleConstExpr.valueOf(value);
		}

		public ConstExpression expOf(byte[] value) {
			return BytesConstExpr.valueOf(value);
		}

		public ConstExpression expOf(String value) {
			return StringConstExpr.valueOf(value);
		}

		public ConstExpression expOf(Date value) {
			return DateConstExpr.valueOf(value.getTime());
		}

		public ConstExpression expOfDate(long value) {
			return DateConstExpr.valueOf(value);
		}

		public ConstExpression expOf(GUID value) {
			return GUIDConstExpr.valueOf(value);
		}
	}

	public final static ConstExpressionBuilderImpl builder = new ConstExpressionBuilderImpl();

	public static ConstExpr expOf(Object object) {
		if (object == null) {
			throw new NullArgumentException("常量值为空。");
		} else if (object instanceof ConstExpr) {
			return (ConstExpr) object;
		} else if (object instanceof GUID) {
			return GUIDConstExpr.valueOf((GUID) object);
		} else if (object instanceof Integer) {
			return IntConstExpr.valueOf(((Integer) object).intValue());
		} else if (object instanceof String || object instanceof Character) {
			return StringConstExpr.valueOf((String) object);
		} else if (object instanceof Date) {
			return DateConstExpr.valueOf(((Date) object).getTime());
		} else if (object instanceof byte[]) {
			return BytesConstExpr.valueOf((byte[]) object);
		} else if (object instanceof Byte) {
			return ByteConstExpr.valueOf(((Byte) object).byteValue());
		} else if (object instanceof Short) {
			return ShortConstExpr.valueOf(((Short) object).shortValue());
		} else if (object instanceof Long) {
			return LongConstExpr.valueOf(((Long) object).longValue());
		} else if (object instanceof Boolean) {
			return BooleanConstExpr.valueOf(((Boolean) object).booleanValue());
		} else if (object instanceof Float) {
			return FloatConstExpr.valueOf(((Float) object).floatValue());
		} else if (object instanceof Double) {
			return DoubleConstExpr.valueOf(((Double) object).doubleValue());
		}
		throw new UnsupportedOperationException("不支持的常量值:" + object);
	}

	public static ConstExpr constOf(DataType type, String str) {
		if (type == null) {
			throw new NullArgumentException("类型");
		}
		DataTypeBase t = (DataTypeBase) type;
		return t.detect(parser, str);
	}

	@Override
	public final String getXMLTagName() {
		return ConstExpr.xml_element_const;
	}

	@Override
	public final void render(SXElement element) {
		super.render(element);
		element.setAttribute(xml_attr_type, this.getType().toString());
		element.setAttribute(xml_attr_value, this.getString());
	}

	static final String xml_element_const = "const";
	static final String xml_attr_type = "type";
	static final String xml_attr_value = "value";

	@Override
	final ConstExpr clone(RelationRefDomain domain, ArgumentOwner arguments) {
		return this;
	}

	@Override
	final ConstExpr clone(RelationRef fromSample, RelationRef from,
			RelationRef toSample, RelationRef to) {
		return this;
	}

	static final ConstExpr loadConst(SXElement element) {
		DataType type = element.getAsType(xml_attr_type, null);
		return type.detect(parser, element.getString(xml_attr_value));
	}

	static final TypeDetector<ConstExpr, Object> parser = new TypeDetectorBase<ConstExpr, Object>() {

		@Override
		public ConstExpr inString(Object value, SequenceDataType type)
				throws Throwable {
			return StringConstExpr.valueOf(Convert.toString(value));
		}

		@Override
		public ConstExpr inShort(Object value) throws Throwable {
			return ShortConstExpr.valueOf(Convert.toShort(value));
		}

		@Override
		public ConstExpr inLong(Object value) throws Throwable {
			return LongConstExpr.valueOf(Convert.toLong(value));
		}

		@Override
		public ConstExpr inInt(Object value) throws Throwable {
			return IntConstExpr.valueOf(Convert.toInt(value));
		}

		@Override
		public ConstExpr inGUID(Object value) throws Throwable {
			return GUIDConstExpr.valueOf(Convert.toGUID(value));
		}

		@Override
		public ConstExpr inFloat(Object value) throws Throwable {
			return FloatConstExpr.valueOf(Convert.toFloat(value));
		}

		@Override
		public ConstExpr inDouble(Object value) throws Throwable {
			return DoubleConstExpr.valueOf(Convert.toDouble(value));
		}

		@Override
		public ConstExpr inDate(Object value) throws Throwable {
			return DateConstExpr.valueOf(Convert.toDate(value));
		}

		@Override
		public ConstExpr inBytes(Object value, SequenceDataType type)
				throws Throwable {
			return BytesConstExpr.valueOf(Convert.toBytes(value));
		}

		@Override
		public ConstExpr inByte(Object value) throws Throwable {
			return ByteConstExpr.valueOf(Convert.toByte(value));
		}

		@Override
		public ConstExpr inBoolean(Object value) throws Throwable {
			return BooleanConstExpr.valueOf(Convert.toBoolean(value));
		}

	};
}
