package org.eclipse.jt.core.def.exp;

import java.util.Date;

import org.eclipse.jt.core.impl.ConstExpr;
import org.eclipse.jt.core.type.GUID;
import org.eclipse.jt.core.type.ReadableValue;


/**
 * 常量表达式
 * 
 * @author Jeff Tang
 * 
 */
public interface ConstExpression extends ValueExpression, ReadableValue {

	public static final ConstExpressionBuilder builder = ConstExpr.builder;

	public static final ConstExpression TRUE = builder.expOf(true);

	public static final ConstExpression FALSE = builder.expOf(false);

	/**
	 * 常量表达式构造器
	 * 
	 * @author Jeff Tang
	 */
	public static interface ConstExpressionBuilder extends
			ValueExpressionBuilder {

		public ConstExpression expOf(Object value);

		public ConstExpression expOf(boolean value);

		public ConstExpression expOf(byte value);

		public ConstExpression expOf(short value);

		public ConstExpression expOf(char value);

		public ConstExpression expOf(int value);

		public ConstExpression expOf(long value);

		public ConstExpression expOf(float value);

		public ConstExpression expOf(double value);

		public ConstExpression expOf(byte[] value);

		public ConstExpression expOf(String value);

		public ConstExpression expOf(Date value);

		public ConstExpression expOfDate(long value);

		public ConstExpression expOf(GUID value);
	}

}
