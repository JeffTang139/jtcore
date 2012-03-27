package org.eclipse.jt.core.impl;

import java.io.IOException;

import org.eclipse.jt.core.def.exp.Operator;
import org.eclipse.jt.core.exception.InvalidExpressionException;
import org.eclipse.jt.core.type.DataType;


/**
 * 运算符实现类,包括函数
 * 
 * @author Jeff Tang
 * 
 */
public enum OperatorImpl implements Operator {

	/**
	 * 加
	 */
	ADD {

		@Override
		final void buildCode(CodeBuilder builder, OperateExpr operate)
				throws IOException {
			buildValueExprCallCode(builder, operate, "xAdd");
		}

		@Override
		protected DataType checkValues(ValueExpr[] values) {
			expectMin(this, values, 2);
			return checkNumber(this, values);
		}

		@Override
		final void render(ISqlExprBuffer buffer, OperateExpr expr) {
			buffer.add(expr.values.length);
		}

	},

	/**
	 * 减
	 */
	SUB {

		@Override
		final void buildCode(CodeBuilder builder, OperateExpr operate)
				throws IOException {
			buildValueExprCallCode(builder, operate, "xSub");
		}

		@Override
		protected DataType checkValues(ValueExpr[] values) {
			expectMin(this, values, 2);
			return checkNumber(this, values);
		}

		@Override
		void render(ISqlExprBuffer buffer, OperateExpr expr) {
			buffer.sub(expr.values.length);
		}

	},

	/**
	 * 乘
	 */
	MUL {

		@Override
		final void buildCode(CodeBuilder builder, OperateExpr operate)
				throws IOException {
			buildValueExprCallCode(builder, operate, "xMul");
		}

		@Override
		protected DataType checkValues(ValueExpr[] values) {
			expectMin(this, values, 2);
			return checkNumber(this, values);
		}

		@Override
		void render(ISqlExprBuffer buffer, OperateExpr expr) {
			buffer.mul(expr.values.length);
		}

	},

	/**
	 * 除
	 */
	DIV {

		@Override
		final void buildCode(CodeBuilder builder, OperateExpr operate)
				throws IOException {
			buildValueExprCallCode(builder, operate, "xDiv");
		}

		@Override
		protected DataType checkValues(ValueExpr[] values) {
			expectMin(this, values, 2);
			return checkNumber(this, values);
		}

		@Override
		void render(ISqlExprBuffer buffer, OperateExpr expr) {
			buffer.div(expr.values.length);
		}
	},

	/**
	 * 取负
	 */
	MINUS {

		@Override
		final void buildCode(CodeBuilder builder, OperateExpr operate)
				throws IOException {
			buildValueExprCallCode(builder, operate, "xMinus");
		}

		@Override
		protected final DataType checkValues(ValueExpr[] values) {
			expect(this, values, 1);
			return checkNumber(this, values[0]);
		}

		@Override
		void render(ISqlExprBuffer buffer, OperateExpr expr) {
			buffer.neg();
		}

	},

	/**
	 * 取余
	 */
	MOD {

		@Override
		final void buildCode(CodeBuilder builder, OperateExpr operate)
				throws IOException {
			buildValueExprCallCode(builder, operate, "xMod");
		}

		@Override
		protected final DataType checkValues(ValueExpr[] values) {
			expect(this, values, 2);
			return checkNumber(this, values);
		}

		@Override
		void render(ISqlExprBuffer buffer, OperateExpr expr) {
			buffer.mod();
		}

	},

	/**
	 * 字符串连接
	 */
	STR_CONCAT {

		@Override
		final void buildCode(CodeBuilder builder, OperateExpr operate)
				throws IOException {
			buildValueExprCallCode(builder, operate, "xStrConcat");
		}

		@Override
		protected DataType checkValues(ValueExpr[] values) {
			expectMin(this, values, 2);
			return checkString(this, values);
		}

		@Override
		void render(ISqlExprBuffer buffer, OperateExpr expr) {
			buffer.func(SqlFunction.STR_CONCAT, expr.values.length);
		}
	},

	BIN_CONCAT {

		@Override
		public void buildCode(CodeBuilder builder, OperateExpr operate)
				throws IOException {
			buildValueExprCallCode(builder, operate, "xBinConcat");
		}

		@Override
		protected DataType checkValues(ValueExpr[] values) {
			expectMin(this, values, 2);
			return checkBytes(this, values);
		}

		@Override
		void render(ISqlExprBuffer buffer, OperateExpr expr) {
			buffer.func(SqlFunction.BIN_CONCAT, expr.values.length);
		}

	},

	/**
	 * COALESCE(返回其参数中第一个非空表达式)
	 */
	COALESCE {

		@Override
		final void buildCode(CodeBuilder builder, OperateExpr operate)
				throws IOException {
			buildValueExprCallCode(builder, operate, "xCoalesce");
		}

		@Override
		protected DataType checkValues(ValueExpr[] values) {
			expectMin(this, values, 2);
			DataType t = values[0].getType();
			for (int i = 1; i < values.length; i++) {
				t = t.calcPrecedence(values[i].getType());
			}
			return t;
		}

		@Override
		void render(ISqlExprBuffer buffer, OperateExpr expr) {
			buffer.coalesce(expr.values.length);
		}
	},

	/**
	 * SIMPLE_CASE
	 */
	SIMPLE_CASE {

		@Override
		final void buildCode(CodeBuilder builder, OperateExpr operate)
				throws IOException {
			buildValueExprCallCode(builder, operate, "xSimpleCase");
		}

		@Override
		protected DataType checkValues(ValueExpr[] values) {
			expectMin(this, values, 3);
			DataType compareType = values[0].getType().calcPrecedence(
					values[1].getType());
			DataType returnType = values[2].getType();
			int i = 4;
			for (; i < values.length; i += 2) {
				compareType = compareType.calcPrecedence(values[i - 1]
						.getType());
				returnType = returnType.calcPrecedence(values[i].getType());
			}
			if (i == values.length) {
				returnType = returnType.calcPrecedence(values[i - 1].getType());
			}
			return returnType;
		}

		@Override
		void render(ISqlExprBuffer buffer, OperateExpr expr) {
			buffer.simpleCase(expr.values.length);
		}

	},

	// -------------------------- sql aggregate --------------------------

	COUNT_ALL {

		@Override
		final void buildCode(CodeBuilder builder, OperateExpr operate)
				throws IOException {
			buildSQLFuncCallCode(builder, operate, "xCount");
		}

		@Override
		protected final DataTypeBase checkValues(ValueExpr[] values) {
			expect(this, values, 1);
			return IntType.TYPE;
		}

		@Override
		void render(ISqlExprBuffer buffer, OperateExpr expr) {
			buffer.func(SqlFunction.COUNT, expr.values.length);
		}
	},

	COUNT_DISTINCT {

		@Override
		protected final DataTypeBase checkValues(ValueExpr[] values) {
			expect(this, values, 1);
			return IntType.TYPE;
		}

		@Override
		final void buildCode(CodeBuilder builder, OperateExpr operate)
				throws IOException {
			buildSQLFuncCallCode(builder, operate, "xCountDistinct");
		}

		@Override
		void render(ISqlExprBuffer buffer, OperateExpr expr) {
			buffer.func(SqlFunction.COUNT_DISTINCT, expr.values.length);
		}

	},

	COUNT_ASTERISK {

		@Override
		protected final DataTypeBase checkValues(ValueExpr[] values) {
			expect(this, values, 0);
			return IntType.TYPE;
		}

		@Override
		final void buildCode(CodeBuilder builder, OperateExpr operate)
				throws IOException {
			buildSQLFuncCallCode(builder, operate, "xCount");
		}

		@Override
		protected void render(ISqlExprBuffer buffer, OperateExpr expr) {
			buffer.func(SqlFunction.COUNT, expr.values.length);
		}

	},

	AVG_ALL {

		@Override
		final void buildCode(CodeBuilder builder, OperateExpr operate)
				throws IOException {
			buildSQLFuncCallCode(builder, operate, "xAvg");
		}

		@Override
		protected DataType checkValues(ValueExpr[] values) {
			expect(this, values, 1);
			return checkNumber(this, values[0]);
		}

		@Override
		void render(ISqlExprBuffer buffer, OperateExpr expr) {
			buffer.func(SqlFunction.AVG, expr.values.length);
		}
	},

	AVG_DISTINCT {

		@Override
		final void buildCode(CodeBuilder builder, OperateExpr operate)
				throws IOException {
			buildSQLFuncCallCode(builder, operate, "xAvgDistinct");
		}

		@Override
		protected DataType checkValues(ValueExpr[] values) {
			expect(this, values, 1);
			return checkNumber(this, values[0]);
		}

		@Override
		void render(ISqlExprBuffer buffer, OperateExpr expr) {
			buffer.func(SqlFunction.AVG_DISTINCT, expr.values.length);
		}
	},

	SUM_ALL {

		@Override
		final void buildCode(CodeBuilder builder, OperateExpr operate)
				throws IOException {
			buildSQLFuncCallCode(builder, operate, "xSum");
		}

		@Override
		protected DataType checkValues(ValueExpr[] values) {
			expect(this, values, 1);
			return checkNumber(this, values[0]);
		}

		@Override
		void render(ISqlExprBuffer buffer, OperateExpr expr) {
			buffer.func(SqlFunction.SUM, expr.values.length);
		}
	},

	SUM_DISTINCT {

		@Override
		final void buildCode(CodeBuilder builder, OperateExpr operate)
				throws IOException {
			buildSQLFuncCallCode(builder, operate, "xSumDistinct");
		}

		@Override
		protected DataType checkValues(ValueExpr[] values) {
			expect(this, values, 1);
			return checkNumber(this, values[0]);
		}

		@Override
		void render(ISqlExprBuffer buffer, OperateExpr expr) {
			buffer.func(SqlFunction.SUM_DISTINCT, expr.values.length);
		}
	},

	MIN {

		@Override
		final void buildCode(CodeBuilder builder, OperateExpr operate)
				throws IOException {
			buildSQLFuncCallCode(builder, operate, "xMin");
		}

		@Override
		protected DataType checkValues(ValueExpr[] values) {
			expect(this, values, 1);
			return super.checkValues(values);
		}

		@Override
		void render(ISqlExprBuffer buffer, OperateExpr expr) {
			buffer.func(SqlFunction.MIN, expr.values.length);
		}

	},

	MAX {

		@Override
		final void buildCode(CodeBuilder builder, OperateExpr operate)
				throws IOException {
			buildSQLFuncCallCode(builder, operate, "xMax");
		}

		@Override
		protected DataType checkValues(ValueExpr[] values) {
			expect(this, values, 1);
			return super.checkValues(values);
		}

		@Override
		void render(ISqlExprBuffer buffer, OperateExpr expr) {
			buffer.func(SqlFunction.MAX, expr.values.length);
		}
	},

	// 分析函数
	GROUPING {

		@Override
		void buildCode(CodeBuilder builder, OperateExpr operate)
				throws IOException {
			buildSQLFuncCallCode(builder, operate, "xGrouping");
		}

		@Override
		protected DataTypeBase checkValues(ValueExpr[] values) {
			expect(this, values, 1);
			return IntType.TYPE;
		}

		@Override
		void render(ISqlExprBuffer buffer, OperateExpr expr) {
			buffer.func(SqlFunction.GROUPING, expr.values.length);
		}
	},

	// -------------------------- sql date --------------------------

	GETDATE {

		@Override
		final void buildCode(CodeBuilder builder, OperateExpr operate)
				throws IOException {
			buildSQLFuncCallCode(builder, operate, "xGetDate");
		}

		@Override
		protected DateType checkValues(ValueExpr[] values) {
			expect(this, values, 0);
			return DateType.TYPE;
		}

		@Override
		protected boolean isNonDeterministic() {
			return true;
		}

		@Override
		void render(ISqlExprBuffer buffer, OperateExpr expr) {
			buffer.func(SqlFunction.GETDATE, expr.values.length);
		}
	},

	YEAR_OF {

		@Override
		final void buildCode(CodeBuilder builder, OperateExpr operate)
				throws IOException {
			buildSQLFuncCallCode(builder, operate, "xYearOf");
		}

		@Override
		protected DataTypeBase checkValues(ValueExpr[] values) {
			expect(this, values, 1);
			checkDate(this, values[0]);
			return IntType.TYPE;
		}

		@Override
		void render(ISqlExprBuffer buffer, OperateExpr expr) {
			buffer.func(SqlFunction.YEAROF, expr.values.length);
		}
	},

	QUARTER_OF {

		@Override
		final void buildCode(CodeBuilder builder, OperateExpr operate)
				throws IOException {
			buildSQLFuncCallCode(builder, operate, "xQuarterOf");
		}

		@Override
		protected DataTypeBase checkValues(ValueExpr[] values) {
			expect(this, values, 1);
			checkDate(this, values[0]);
			return IntType.TYPE;
		}

		@Override
		void render(ISqlExprBuffer buffer, OperateExpr expr) {
			buffer.func(SqlFunction.QUARTEROF, expr.values.length);
		}

	},

	MONTH_OF {

		@Override
		final void buildCode(CodeBuilder builder, OperateExpr operate)
				throws IOException {
			buildSQLFuncCallCode(builder, operate, "xMonthOf");
		}

		@Override
		protected DataTypeBase checkValues(ValueExpr[] values) {
			expect(this, values, 1);
			checkDate(this, values[0]);
			return IntType.TYPE;
		}

		@Override
		void render(ISqlExprBuffer buffer, OperateExpr expr) {
			buffer.func(SqlFunction.MONTHOF, expr.values.length);
		}
	},

	WEEK_OF {

		@Override
		final void buildCode(CodeBuilder builder, OperateExpr operate)
				throws IOException {
			buildSQLFuncCallCode(builder, operate, "xWeekOf");
		}

		@Override
		protected DataTypeBase checkValues(ValueExpr[] values) {
			expect(this, values, 1);
			checkDate(this, values[0]);
			return IntType.TYPE;
		}

		@Override
		void render(ISqlExprBuffer buffer, OperateExpr expr) {
			buffer.func(SqlFunction.WEEKOF, expr.values.length);
		}
	},

	DAY_OF {

		@Override
		final void buildCode(CodeBuilder builder, OperateExpr operate)
				throws IOException {
			buildSQLFuncCallCode(builder, operate, "xDayOf");
		}

		@Override
		protected DataTypeBase checkValues(ValueExpr[] values) {
			expect(this, values, 1);
			checkDate(this, values[0]);
			return IntType.TYPE;
		}

		@Override
		void render(ISqlExprBuffer buffer, OperateExpr expr) {
			buffer.func(SqlFunction.DAYOF, expr.values.length);
		}
	},

	DAY_OF_YEAR {

		@Override
		final void buildCode(CodeBuilder builder, OperateExpr operate)
				throws IOException {
			buildSQLFuncCallCode(builder, operate, "xDayOfYear");
		}

		@Override
		protected DataTypeBase checkValues(ValueExpr[] values) {
			expect(this, values, 1);
			checkDate(this, values[0]);
			return IntType.TYPE;
		}

		@Override
		void render(ISqlExprBuffer buffer, OperateExpr expr) {
			buffer.func(SqlFunction.DAYOFYEAR, expr.values.length);
		}
	},

	DAY_OF_WEEK {

		@Override
		final void buildCode(CodeBuilder builder, OperateExpr operate)
				throws IOException {
			buildSQLFuncCallCode(builder, operate, "xDayOfWeek");
		}

		@Override
		protected DataTypeBase checkValues(ValueExpr[] values) {
			expect(this, values, 1);
			checkDate(this, values[0]);
			return IntType.TYPE;
		}

		@Override
		void render(ISqlExprBuffer buffer, OperateExpr expr) {
			buffer.func(SqlFunction.DAYOFWEEK, expr.values.length);
		}
	},

	HOUR_OF {

		@Override
		final void buildCode(CodeBuilder builder, OperateExpr operate)
				throws IOException {
			buildSQLFuncCallCode(builder, operate, "xHourOf");
		}

		@Override
		protected DataTypeBase checkValues(ValueExpr[] values) {
			expect(this, values, 1);
			checkDate(this, values[0]);
			return IntType.TYPE;
		}

		@Override
		void render(ISqlExprBuffer buffer, OperateExpr expr) {
			buffer.func(SqlFunction.HOUROF, expr.values.length);
		}
	},

	MINUTE_OF {

		@Override
		final void buildCode(CodeBuilder builder, OperateExpr operate)
				throws IOException {
			buildSQLFuncCallCode(builder, operate, "xMinuteOf");
		}

		@Override
		protected DataTypeBase checkValues(ValueExpr[] values) {
			expect(this, values, 1);
			checkDate(this, values[0]);
			return IntType.TYPE;
		}

		@Override
		void render(ISqlExprBuffer buffer, OperateExpr expr) {
			buffer.func(SqlFunction.MINUTEOF, expr.values.length);
		}
	},

	SECOND_OF {

		@Override
		final void buildCode(CodeBuilder builder, OperateExpr operate)
				throws IOException {
			buildSQLFuncCallCode(builder, operate, "xSecondOf");
		}

		@Override
		protected DataTypeBase checkValues(ValueExpr[] values) {
			expect(this, values, 1);
			checkDate(this, values[0]);
			return IntType.TYPE;
		}

		@Override
		void render(ISqlExprBuffer buffer, OperateExpr expr) {
			buffer.func(SqlFunction.SECONDOF, expr.values.length);
		}

	},

	MILLISECOND_OF {

		@Override
		final void buildCode(CodeBuilder builder, OperateExpr operate)
				throws IOException {
			buildSQLFuncCallCode(builder, operate, "xMillisecondOf");
		}

		@Override
		protected DataTypeBase checkValues(ValueExpr[] values) {
			expect(this, values, 1);
			checkDate(this, values[0]);
			return IntType.TYPE;
		}

		@Override
		void render(ISqlExprBuffer buffer, OperateExpr expr) {
			buffer.func(SqlFunction.MILLISECONDOF, expr.values.length);
		}
	},

	ADD_YEAR {

		@Override
		final void buildCode(CodeBuilder builder, OperateExpr operate)
				throws IOException {
			buildSQLFuncCallCode(builder, operate, "xAddYear");
		}

		@Override
		protected DataType checkValues(ValueExpr[] values) {
			expect(this, values, 2);
			checkDate(this, values[0]);
			Expr.checkNonDecimalNumber(this.toString(), values[1]);
			return DateType.TYPE;
		}

		@Override
		void render(ISqlExprBuffer buffer, OperateExpr expr) {
			buffer.func(SqlFunction.ADDYEAR, expr.values.length);
		}
	},

	ADD_QUARTER {

		@Override
		final void buildCode(CodeBuilder builder, OperateExpr operate)
				throws IOException {
			buildSQLFuncCallCode(builder, operate, "xAddQuarter");
		}

		@Override
		protected DataType checkValues(ValueExpr[] values) {
			expect(this, values, 2);
			checkDate(this, values[0]);
			Expr.checkNonDecimalNumber(this.toString(), values[1]);
			return DateType.TYPE;
		}

		@Override
		void render(ISqlExprBuffer buffer, OperateExpr expr) {
			buffer.func(SqlFunction.ADDQUARTER, expr.values.length);
		}
	},

	ADD_MONTH {

		@Override
		final void buildCode(CodeBuilder builder, OperateExpr operate)
				throws IOException {
			buildSQLFuncCallCode(builder, operate, "xAddMonth");
		}

		@Override
		protected DataType checkValues(ValueExpr[] values) {
			expect(this, values, 2);
			checkDate(this, values[0]);
			Expr.checkNonDecimalNumber(this.toString(), values[1]);
			return DateType.TYPE;
		}

		@Override
		void render(ISqlExprBuffer buffer, OperateExpr expr) {
			buffer.func(SqlFunction.ADDMONTH, expr.values.length);
		}

	},

	ADD_WEEK {

		@Override
		final void buildCode(CodeBuilder builder, OperateExpr operate)
				throws IOException {
			buildSQLFuncCallCode(builder, operate, "xAddWeek");
		}

		@Override
		protected DataType checkValues(ValueExpr[] values) {
			expect(this, values, 2);
			checkDate(this, values[0]);
			Expr.checkNonDecimalNumber(this.toString(), values[1]);
			return DateType.TYPE;
		}

		@Override
		void render(ISqlExprBuffer buffer, OperateExpr expr) {
			buffer.func(SqlFunction.ADDWEEK, expr.values.length);
		}
	},

	ADD_DAY {

		@Override
		final void buildCode(CodeBuilder builder, OperateExpr operate)
				throws IOException {
			buildSQLFuncCallCode(builder, operate, "xAddDay");
		}

		@Override
		protected DataType checkValues(ValueExpr[] values) {
			expect(this, values, 2);
			checkDate(this, values[0]);
			Expr.checkNonDecimalNumber(this.toString(), values[1]);
			return DateType.TYPE;
		}

		@Override
		void render(ISqlExprBuffer buffer, OperateExpr expr) {
			buffer.func(SqlFunction.ADDDAY, expr.values.length);
		}
	},

	ADD_HOUR {

		@Override
		final void buildCode(CodeBuilder builder, OperateExpr operate)
				throws IOException {
			buildSQLFuncCallCode(builder, operate, "xAddHour");
		}

		@Override
		protected DataType checkValues(ValueExpr[] values) {
			expect(this, values, 2);
			checkDate(this, values[0]);
			Expr.checkNonDecimalNumber(this.toString(), values[1]);
			return DateType.TYPE;
		}

		@Override
		void render(ISqlExprBuffer buffer, OperateExpr expr) {
			buffer.func(SqlFunction.ADDHOUR, expr.values.length);
		}
	},

	ADD_MINUTE {

		@Override
		final void buildCode(CodeBuilder builder, OperateExpr operate)
				throws IOException {
			buildSQLFuncCallCode(builder, operate, "xAddMinute");
		}

		@Override
		protected DataType checkValues(ValueExpr[] values) {
			expect(this, values, 2);
			checkDate(this, values[0]);
			Expr.checkNonDecimalNumber(this.toString(), values[1]);
			return DateType.TYPE;
		}

		@Override
		void render(ISqlExprBuffer buffer, OperateExpr expr) {
			buffer.func(SqlFunction.ADDMINUTE, expr.values.length);
		}
	},

	ADD_SECOND {

		@Override
		final void buildCode(CodeBuilder builder, OperateExpr operate)
				throws IOException {
			buildSQLFuncCallCode(builder, operate, "xAddSecond");
		}

		@Override
		protected DataType checkValues(ValueExpr[] values) {
			expect(this, values, 2);
			checkDate(this, values[0]);
			Expr.checkNonDecimalNumber(this.toString(), values[1]);
			return DateType.TYPE;
		}

		@Override
		void render(ISqlExprBuffer buffer, OperateExpr expr) {
			buffer.func(SqlFunction.ADDSECOND, expr.values.length);
		}
	},

	YEAR_DIFF {

		@Override
		final void buildCode(CodeBuilder builder, OperateExpr operate)
				throws IOException {
			buildSQLFuncCallCode(builder, operate, "xYearDiff");
		}

		@Override
		protected DataTypeBase checkValues(ValueExpr[] values) {
			expect(this, values, 2);
			checkDate(this, values[0]);
			checkDate(this, values[1]);
			return IntType.TYPE;
		}

		@Override
		void render(ISqlExprBuffer buffer, OperateExpr expr) {
			buffer.func(SqlFunction.YEARDIFF, expr.values.length);
		}

	},

	QUARTER_DIFF {

		@Override
		final void buildCode(CodeBuilder builder, OperateExpr operate)
				throws IOException {
			buildSQLFuncCallCode(builder, operate, "xQuarterDiff");
		}

		@Override
		protected DataTypeBase checkValues(ValueExpr[] values) {
			expect(this, values, 2);
			checkDate(this, values[0]);
			checkDate(this, values[1]);
			return IntType.TYPE;
		}

		@Override
		void render(ISqlExprBuffer buffer, OperateExpr expr) {
			buffer.func(SqlFunction.QUARTERDIFF, expr.values.length);
		}
	},

	MONTH_DIFF {

		@Override
		final void buildCode(CodeBuilder builder, OperateExpr operate)
				throws IOException {
			buildSQLFuncCallCode(builder, operate, "xMonthDiff");
		}

		@Override
		protected DataTypeBase checkValues(ValueExpr[] values) {
			expect(this, values, 2);
			checkDate(this, values[0]);
			checkDate(this, values[1]);
			return IntType.TYPE;
		}

		@Override
		void render(ISqlExprBuffer buffer, OperateExpr expr) {
			buffer.func(SqlFunction.MONTHDIFF, expr.values.length);
		}
	},

	DAY_DIFF {

		@Override
		final void buildCode(CodeBuilder builder, OperateExpr operate)
				throws IOException {
			buildSQLFuncCallCode(builder, operate, "xDayDiff");
		}

		@Override
		protected DataTypeBase checkValues(ValueExpr[] values) {
			expect(this, values, 2);
			checkDate(this, values[0]);
			checkDate(this, values[1]);
			return IntType.TYPE;
		}

		@Override
		void render(ISqlExprBuffer buffer, OperateExpr expr) {
			buffer.func(SqlFunction.DAYDIFF, expr.values.length);
		}
	},

	WEEK_DIFF {

		@Override
		final void buildCode(CodeBuilder builder, OperateExpr operate)
				throws IOException {
			buildSQLFuncCallCode(builder, operate, "xWeekDiff");
		}

		@Override
		protected DataTypeBase checkValues(ValueExpr[] values) {
			expect(this, values, 2);
			checkDate(this, values[0]);
			checkDate(this, values[1]);
			return IntType.TYPE;
		}

		@Override
		void render(ISqlExprBuffer buffer, OperateExpr expr) {
			buffer.func(SqlFunction.WEEKDIFF, expr.values.length);
		}

	},

	HOUR_DIFF {

		@Override
		final void buildCode(CodeBuilder builder, OperateExpr operate)
				throws IOException {
			buildSQLFuncCallCode(builder, operate, "xHourDiff");
		}

		@Override
		protected DataTypeBase checkValues(ValueExpr[] values) {
			expect(this, values, 2);
			checkDate(this, values[0]);
			checkDate(this, values[1]);
			return IntType.TYPE;
		}

		@Override
		void render(ISqlExprBuffer buffer, OperateExpr expr) {
			buffer.func(SqlFunction.HOURDIFF, expr.values.length);
		}

	},

	MINUTE_DIFF {

		@Override
		final void buildCode(CodeBuilder builder, OperateExpr operate)
				throws IOException {
			buildSQLFuncCallCode(builder, operate, "xMinuteDiff");
		}

		@Override
		protected DataTypeBase checkValues(ValueExpr[] values) {
			expect(this, values, 2);
			checkDate(this, values[0]);
			checkDate(this, values[1]);
			return IntType.TYPE;
		}

		@Override
		void render(ISqlExprBuffer buffer, OperateExpr expr) {
			buffer.func(SqlFunction.MINUTEDIFF, expr.values.length);
		}

	},

	SECOND_DIFF {

		@Override
		final void buildCode(CodeBuilder builder, OperateExpr operate)
				throws IOException {
			buildSQLFuncCallCode(builder, operate, "xSecondDiff");
		}

		@Override
		protected DataTypeBase checkValues(ValueExpr[] values) {
			expect(this, values, 2);
			checkDate(this, values[0]);
			checkDate(this, values[1]);
			return IntType.TYPE;
		}

		@Override
		void render(ISqlExprBuffer buffer, OperateExpr expr) {
			buffer.func(SqlFunction.SECONDDIFF, expr.values.length);
		}
	},

	TRUNC_YEAR {

		@Override
		void buildCode(CodeBuilder builder, OperateExpr operate)
				throws IOException {
			buildSQLFuncCallCode(builder, operate, "xTruncYear");
		}

		@Override
		void render(ISqlExprBuffer buffer, OperateExpr expr) {
			buffer.func(SqlFunction.TRUNCYEAR, expr.values.length);
		}

		@Override
		protected DataType checkValues(ValueExpr[] values) {
			expect(this, values, 1);
			checkDate(this, values[0]);
			return DateType.TYPE;
		}

	},

	TRUNC_MONTH {

		@Override
		void buildCode(CodeBuilder builder, OperateExpr operate)
				throws IOException {
			buildSQLFuncCallCode(builder, operate, "xTruncMonth");
		}

		@Override
		void render(ISqlExprBuffer buffer, OperateExpr expr) {
			buffer.func(SqlFunction.TRUNCMONTH, expr.values.length);
		}

		@Override
		protected DataType checkValues(ValueExpr[] values) {
			expect(this, values, 1);
			checkDate(this, values[0]);
			return DateType.TYPE;
		}

	},

	TRUNC_DAY {

		@Override
		void buildCode(CodeBuilder builder, OperateExpr operate)
				throws IOException {
			buildSQLFuncCallCode(builder, operate, "xTruncDay");
		}

		@Override
		void render(ISqlExprBuffer buffer, OperateExpr expr) {
			buffer.func(SqlFunction.TRUNCDAY, expr.values.length);
		}

		@Override
		protected DataType checkValues(ValueExpr[] values) {
			expect(this, values, 1);
			checkDate(this, values[0]);
			return DateType.TYPE;
		}

	},

	IS_LEAP_YEAR {

		@Override
		final void buildCode(CodeBuilder builder, OperateExpr operate)
				throws IOException {
			buildSQLFuncCallCode(builder, operate, "xIsLeapYear");
		}

		@Override
		protected BooleanType checkValues(ValueExpr[] values) {
			expect(this, values, 1);
			checkDate(this, values[0]);
			return BooleanType.TYPE;
		}

		@Override
		void render(ISqlExprBuffer buffer, OperateExpr expr) {
			buffer.func(SqlFunction.ISLEAPYEAR, expr.values.length);
		}

	},

	IS_LEAP_MONTH {

		@Override
		final void buildCode(CodeBuilder builder, OperateExpr operate)
				throws IOException {
			buildSQLFuncCallCode(builder, operate, "xIsLeapMonth");
		}

		@Override
		protected BooleanType checkValues(ValueExpr[] values) {
			expect(this, values, 1);
			checkDate(this, values[0]);
			return BooleanType.TYPE;
		}

		@Override
		void render(ISqlExprBuffer buffer, OperateExpr expr) {
			buffer.func(SqlFunction.ISLEAPMONTH, expr.values.length);
		}

	},

	IS_LEAP_DAY {

		@Override
		final void buildCode(CodeBuilder builder, OperateExpr operate)
				throws IOException {
			buildSQLFuncCallCode(builder, operate, "xIsLeapDay");
		}

		@Override
		protected BooleanType checkValues(ValueExpr[] values) {
			expect(this, values, 1);
			checkDate(this, values[0]);
			return BooleanType.TYPE;
		}

		@Override
		void render(ISqlExprBuffer buffer, OperateExpr expr) {
			buffer.func(SqlFunction.ISLEAPDAY, expr.values.length);
		}
	},

	SIN {

		@Override
		final void buildCode(CodeBuilder builder, OperateExpr operate)
				throws IOException {
			buildSQLFuncCallCode(builder, operate, "xSin");
		}

		@Override
		protected DataType checkValues(ValueExpr[] values) {
			expect(this, values, 1);
			checkNumber(this, values[0]);
			return DoubleType.TYPE;
		}

		@Override
		void render(ISqlExprBuffer buffer, OperateExpr expr) {
			buffer.func(SqlFunction.SIN, expr.values.length);
		}
	},

	COS {

		@Override
		final void buildCode(CodeBuilder builder, OperateExpr operate)
				throws IOException {
			buildSQLFuncCallCode(builder, operate, "xCos");
		}

		@Override
		protected DataType checkValues(ValueExpr[] values) {
			expect(this, values, 1);
			checkNumber(this, values[0]);
			return DoubleType.TYPE;
		}

		@Override
		void render(ISqlExprBuffer buffer, OperateExpr expr) {
			buffer.func(SqlFunction.COS, expr.values.length);
		}
	},

	TAN {

		@Override
		final void buildCode(CodeBuilder builder, OperateExpr operate)
				throws IOException {
			buildSQLFuncCallCode(builder, operate, "xTan");
		}

		@Override
		protected DataType checkValues(ValueExpr[] values) {
			expect(this, values, 1);
			checkNumber(this, values[0]);
			return DoubleType.TYPE;
		}

		@Override
		void render(ISqlExprBuffer buffer, OperateExpr expr) {
			buffer.func(SqlFunction.TAN, expr.values.length);
		}
	},

	ASIN {

		@Override
		final void buildCode(CodeBuilder builder, OperateExpr operate)
				throws IOException {
			buildSQLFuncCallCode(builder, operate, "xAsin");
		}

		@Override
		protected DataType checkValues(ValueExpr[] values) {
			expect(this, values, 1);
			checkNumber(this, values[0]);
			return DoubleType.TYPE;
		}

		@Override
		void render(ISqlExprBuffer buffer, OperateExpr expr) {
			buffer.func(SqlFunction.ASIN, expr.values.length);
		}
	},

	ACOS {

		@Override
		final void buildCode(CodeBuilder builder, OperateExpr operate)
				throws IOException {
			buildSQLFuncCallCode(builder, operate, "xAcos");
		}

		@Override
		protected DataType checkValues(ValueExpr[] values) {
			expect(this, values, 1);
			checkNumber(this, values[0]);
			return DoubleType.TYPE;
		}

		@Override
		void render(ISqlExprBuffer buffer, OperateExpr expr) {
			buffer.func(SqlFunction.ACOS, expr.values.length);
		}
	},

	ATAN {

		@Override
		final void buildCode(CodeBuilder builder, OperateExpr operate)
				throws IOException {
			buildSQLFuncCallCode(builder, operate, "xAtan");
		}

		@Override
		protected DataType checkValues(ValueExpr[] values) {
			expect(this, values, 1);
			checkNumber(this, values[0]);
			return DoubleType.TYPE;
		}

		@Override
		void render(ISqlExprBuffer buffer, OperateExpr expr) {
			buffer.func(SqlFunction.ATAN, expr.values.length);
		}
	},

	EXP {

		@Override
		final void buildCode(CodeBuilder builder, OperateExpr operate)
				throws IOException {
			buildSQLFuncCallCode(builder, operate, "xExp");
		}

		@Override
		protected DataType checkValues(ValueExpr[] values) {
			expect(this, values, 1);
			checkNumber(this, values[0]);
			return DoubleType.TYPE;
		}

		@Override
		void render(ISqlExprBuffer buffer, OperateExpr expr) {
			buffer.func(SqlFunction.EXP, expr.values.length);
		}
	},

	LN {

		@Override
		final void buildCode(CodeBuilder builder, OperateExpr operate)
				throws IOException {
			buildSQLFuncCallCode(builder, operate, "xLn");
		}

		@Override
		protected DataType checkValues(ValueExpr[] values) {
			expect(this, values, 1);
			checkNumber(this, values[0]);
			return DoubleType.TYPE;
		}

		@Override
		void render(ISqlExprBuffer buffer, OperateExpr expr) {
			buffer.func(SqlFunction.LN, expr.values.length);
		}
	},

	LG {

		@Override
		final void buildCode(CodeBuilder builder, OperateExpr operate)
				throws IOException {
			buildSQLFuncCallCode(builder, operate, "xLg");
		}

		@Override
		protected DataType checkValues(ValueExpr[] values) {
			expect(this, values, 1);
			checkNumber(this, values[0]);
			return DoubleType.TYPE;
		}

		@Override
		void render(ISqlExprBuffer buffer, OperateExpr expr) {
			buffer.func(SqlFunction.LG, expr.values.length);
		}
	},

	POWER {

		@Override
		final void buildCode(CodeBuilder builder, OperateExpr operate)
				throws IOException {
			buildSQLFuncCallCode(builder, operate, "xPower");
		}

		@Override
		protected DataType checkValues(ValueExpr[] values) {
			expect(this, values, 2);
			checkNumber(this, values[0]);
			checkNumber(this, values[1]);
			return DoubleType.TYPE;
		}

		@Override
		void render(ISqlExprBuffer buffer, OperateExpr expr) {
			buffer.func(SqlFunction.POWER, expr.values.length);
		}
	},

	SQRT {

		@Override
		final void buildCode(CodeBuilder builder, OperateExpr operate)
				throws IOException {
			buildSQLFuncCallCode(builder, operate, "xSqrt");
		}

		@Override
		protected DataType checkValues(ValueExpr[] values) {
			expect(this, values, 1);
			checkNumber(this, values[0]);
			return DoubleType.TYPE;
		}

		@Override
		void render(ISqlExprBuffer buffer, OperateExpr expr) {
			buffer.func(SqlFunction.SQRT, expr.values.length);
		}
	},

	CEIL {

		@Override
		final void buildCode(CodeBuilder builder, OperateExpr operate)
				throws IOException {
			buildSQLFuncCallCode(builder, operate, "xCeil");
		}

		@Override
		protected DataType checkValues(ValueExpr[] values) {
			expect(this, values, 1);
			return checkNumber(this, values[0]);
		}

		@Override
		void render(ISqlExprBuffer buffer, OperateExpr expr) {
			buffer.func(SqlFunction.CEIL, expr.values.length);
		}

	},

	FLOOR {

		@Override
		final void buildCode(CodeBuilder builder, OperateExpr operate)
				throws IOException {
			buildSQLFuncCallCode(builder, operate, "xFloor");
		}

		@Override
		protected DataType checkValues(ValueExpr[] values) {
			expect(this, values, 1);
			return checkNumber(this, values[0]);
		}

		@Override
		void render(ISqlExprBuffer buffer, OperateExpr expr) {
			buffer.func(SqlFunction.FLOOR, expr.values.length);
		}

	},

	ROUND {

		@Override
		final void buildCode(CodeBuilder builder, OperateExpr operate)
				throws IOException {
			buildSQLFuncCallCode(builder, operate, "xRound");
		}

		@Override
		protected DataType checkValues(ValueExpr[] values) {
			expect(this, values, 1, 2);
			return checkNumber(this, values);
		}

		@Override
		void render(ISqlExprBuffer buffer, OperateExpr expr) {
			buffer.func(SqlFunction.ROUND, expr.values.length);
		}
	},

	SIGN {

		@Override
		final void buildCode(CodeBuilder builder, OperateExpr operate)
				throws IOException {
			buildSQLFuncCallCode(builder, operate, "xSign");
		}

		@Override
		protected DataType checkValues(ValueExpr[] values) {
			expect(this, values, 1);
			checkNumber(this, values[0]);
			return IntType.TYPE;
		}

		@Override
		void render(ISqlExprBuffer buffer, OperateExpr expr) {
			buffer.func(SqlFunction.SIGN, expr.values.length);
		}
	},

	ABS {

		@Override
		final void buildCode(CodeBuilder builder, OperateExpr operate)
				throws IOException {
			buildSQLFuncCallCode(builder, operate, "xAbs");
		}

		@Override
		protected DataType checkValues(ValueExpr[] values) {
			expect(this, values, 1);
			return checkNumber(this, values[0]);
		}

		@Override
		void render(ISqlExprBuffer buffer, OperateExpr expr) {
			buffer.func(SqlFunction.ABS, expr.values.length);
		}
	},

	// --------------------------- sql character ---------------------------

	CHR {

		@Override
		final void buildCode(CodeBuilder builder, OperateExpr operate)
				throws IOException {
			buildSQLFuncCallCode(builder, operate, "xChr");
		}

		private final DataTypeBase char1 = CharDBType.map.get(1, 0, 0);

		@Override
		protected DataTypeBase checkValues(ValueExpr[] values) {
			expect(this, values, 1);
			Expr.checkNonDecimalNumber(this.toString(), values[0]);
			// 实际上oracle中,该函数返回varchar2(1),sqlserver是返回char(1)
			return this.char1;
		}

		@Override
		void render(ISqlExprBuffer buffer, OperateExpr expr) {
			buffer.func(SqlFunction.CHR, expr.values.length);
		}
	},

	NCHR {

		@Override
		final void buildCode(CodeBuilder builder, OperateExpr operate)
				throws IOException {
			buildSQLFuncCallCode(builder, operate, "xNchr");
		}

		final DataTypeBase nchar1 = NCharDBType.map.get(1, 0, 0);

		@Override
		protected DataTypeBase checkValues(ValueExpr[] values) {
			expect(this, values, 1);
			Expr.checkNonDecimalNumber(this.toString(), values[0]);
			return this.nchar1;
		}

		@Override
		void render(ISqlExprBuffer buffer, OperateExpr expr) {
			buffer.func(SqlFunction.NCHR, expr.values.length);
		}
	},

	ASCII {

		@Override
		final void buildCode(CodeBuilder builder, OperateExpr operate)
				throws IOException {
			buildSQLFuncCallCode(builder, operate, "xAscii");
		}

		@Override
		protected final DataTypeBase checkValues(ValueExpr[] values) {
			expect(this, values, 1);
			checkString(this, values[0]);
			return IntType.TYPE;
		}

		@Override
		void render(ISqlExprBuffer buffer, OperateExpr expr) {
			buffer.func(SqlFunction.ASCII, expr.values.length);
		}

	},

	LEN {

		@Override
		final void buildCode(CodeBuilder builder, OperateExpr operate)
				throws IOException {
			buildSQLFuncCallCode(builder, operate, "xLen");
		}

		@Override
		protected final DataTypeBase checkValues(ValueExpr[] values) {
			expect(this, values, 1);
			DataType type = values[0].getType().getRootType();
			if (type != BytesType.TYPE && type != StringType.TYPE) {
				throw new IllegalArgumentException("参数类型错误");
			}
			return IntType.TYPE;
		}

		@Override
		void render(ISqlExprBuffer buffer, OperateExpr expr) {
			DataType type = expr.values[0].getType().getRootType();
			if (type == BytesType.TYPE) {
				buffer.func(SqlFunction.BIN_LEN, expr.values.length);
			} else {
				buffer.func(SqlFunction.LEN, expr.values.length);
			}
		}
	},

	INDEXOF {

		@Override
		final void buildCode(CodeBuilder builder, OperateExpr operate)
				throws IOException {
			buildSQLFuncCallCode(builder, operate, "xIndexOf");
		}

		@Override
		protected DataTypeBase checkValues(ValueExpr[] values) {
			expect(this, values, 2, 3);
			checkString(this, values[0]);
			checkString(this, values[1]);
			return IntType.TYPE;
		}

		@Override
		void render(ISqlExprBuffer buffer, OperateExpr expr) {
			buffer.func(SqlFunction.INDEXOF, expr.values.length);
		}

	},

	UPPER {

		@Override
		final void buildCode(CodeBuilder builder, OperateExpr operate)
				throws IOException {
			buildSQLFuncCallCode(builder, operate, "xUpper");
		}

		@Override
		protected DataType checkValues(ValueExpr[] values) {
			expect(this, values, 1);
			return checkString(this, values[0]);
		}

		@Override
		void render(ISqlExprBuffer buffer, OperateExpr expr) {
			buffer.func(SqlFunction.UPPER, expr.values.length);
		}

	},

	LOWER {

		@Override
		final void buildCode(CodeBuilder builder, OperateExpr operate)
				throws IOException {
			buildSQLFuncCallCode(builder, operate, "xLower");
		}

		@Override
		protected DataType checkValues(ValueExpr[] values) {
			expect(this, values, 1);
			return checkString(this, values[0]);
		}

		@Override
		void render(ISqlExprBuffer buffer, OperateExpr expr) {
			buffer.func(SqlFunction.LOWER, expr.values.length);
		}

	},

	LTRIM {

		@Override
		final void buildCode(CodeBuilder builder, OperateExpr operate)
				throws IOException {
			buildSQLFuncCallCode(builder, operate, "xLtrim");
		}

		@Override
		protected DataType checkValues(ValueExpr[] values) {
			expect(this, values, 1);
			return checkString(this, values[0]);
		}

		@Override
		void render(ISqlExprBuffer buffer, OperateExpr expr) {
			buffer.func(SqlFunction.LTRIM, expr.values.length);
		}

	},

	RTRIM {

		@Override
		final void buildCode(CodeBuilder builder, OperateExpr operate)
				throws IOException {
			buildSQLFuncCallCode(builder, operate, "xRtrim");
		}

		@Override
		protected DataType checkValues(ValueExpr[] values) {
			expect(this, values, 1);
			return checkString(this, values[0]);
		}

		@Override
		void render(ISqlExprBuffer buffer, OperateExpr expr) {
			buffer.func(SqlFunction.RTRIM, expr.values.length);
		}

	},

	TRIM {

		@Override
		final void buildCode(CodeBuilder builder, OperateExpr operate)
				throws IOException {
			buildSQLFuncCallCode(builder, operate, "xTrim");
		}

		@Override
		protected DataType checkValues(ValueExpr[] values) {
			expect(this, values, 1);
			return checkString(this, values[0]);
		}

		@Override
		void render(ISqlExprBuffer buffer, OperateExpr expr) {
			buffer.func(SqlFunction.TRIM, expr.values.length);
		}

	},

	REPLACE {

		@Override
		final void buildCode(CodeBuilder builder, OperateExpr operate)
				throws IOException {
			buildSQLFuncCallCode(builder, operate, "xReplace");
		}

		@Override
		protected DataType checkValues(ValueExpr[] values) {
			expect(this, values, 3);
			return checkString(this, values);
		}

		@Override
		void render(ISqlExprBuffer buffer, OperateExpr expr) {
			buffer.func(SqlFunction.REPLACE, expr.values.length);
		}

	},

	SUBSTR {

		@Override
		final void buildCode(CodeBuilder builder, OperateExpr operate)
				throws IOException {
			buildSQLFuncCallCode(builder, operate, "xSubstr");
		}

		@Override
		protected DataType checkValues(ValueExpr[] values) {
			expect(this, values, 2, 3);
			checkNumber(this, values[1]);
			if (values.length > 2) {
				checkNumber(this, values[2]);
			}
			DataType type = values[0].getType();
			if (type.getRootType() == BytesType.TYPE
					|| type.getRootType() == StringType.TYPE) {
				return type;
			}
			throw new InvalidExpressionException(this.toString(), "字符串或二进制串",
					type);
		}

		@Override
		void render(ISqlExprBuffer buffer, OperateExpr expr) {
			DataType type = expr.values[0].getType().getRootType();
			if (type == BytesType.TYPE) {
				buffer.func(SqlFunction.BIN_SUBSTR, expr.values.length);
			} else {
				buffer.func(SqlFunction.SUBSTR, expr.values.length);
			}
		}

	},

	TO_CHAR {

		@Override
		public void buildCode(CodeBuilder builder, OperateExpr operate)
				throws IOException {
			buildSQLFuncCallCode(builder, operate, "xToChar");
		}

		@Override
		protected DataType checkValues(ValueExpr[] values) {
			return StringType.TYPE;
		}

		@Override
		void render(ISqlExprBuffer buffer, OperateExpr expr) {
			ValueExpr value = expr.values[0];
			DataType type = value.getType().getRootType();
			if (type == BooleanType.TYPE || type instanceof NumberType
					|| type == DateType.TYPE || type == StringType.TYPE) {
				buffer.func(SqlFunction.TO_CHAR, expr.values.length);
			} else if (type == BytesType.TYPE || type == GUIDType.TYPE) {
				buffer.func(SqlFunction.BIN_TO_CHAR, expr.values.length);
			} else {
				throw new UnsupportedOperationException();
			}

		}

	},

	NEW_RECID {

		@Override
		final void buildCode(CodeBuilder builder, OperateExpr operate)
				throws IOException {
			buildSQLFuncCallCode(builder, operate, "xNewRecid");
		}

		@Override
		protected DataType checkValues(ValueExpr[] values) {
			expect(this, values, 0);
			return GUIDType.TYPE;
		}

		@Override
		protected boolean isNonDeterministic() {
			return true;
		}

		@Override
		void render(ISqlExprBuffer buffer, OperateExpr expr) {
			buffer.func(SqlFunction.NEW_RECID, expr.values.length);
		}

	},

	TO_INT {

		@Override
		public void buildCode(CodeBuilder builder, OperateExpr operate)
				throws IOException {
			buildSQLFuncCallCode(builder, operate, "xToInt");
		}

		@Override
		protected DataType checkValues(ValueExpr[] values) {
			return IntType.TYPE;
		}

		@Override
		void render(ISqlExprBuffer buffer, OperateExpr expr) {
			buffer.func(SqlFunction.TO_INT, expr.values.length);
		}

	},

	PARENT_RECID {

		@Override
		public void buildCode(CodeBuilder builder, OperateExpr operate)
				throws IOException {
			buildValueExprCallCode(builder, operate, "xParentRECID");
		}

		@Override
		protected DataType checkValues(ValueExpr[] values) {
			return GUIDType.TYPE;
		}

		@Override
		void render(ISqlExprBuffer buffer, OperateExpr expr) {
			throw Utils.notImplemented();
		}
	},

	RELATIVE_ANCESTOR_RECID {

		@Override
		public void buildCode(CodeBuilder builder, OperateExpr operate)
				throws IOException {
			buildValueExprCallCode(builder, operate, "xAncestorRECID");
		}

		@Override
		protected DataType checkValues(ValueExpr[] values) {
			return GUIDType.TYPE;
		}

		@Override
		void render(ISqlExprBuffer buffer, OperateExpr expr) {
			throw Utils.notImplemented();
		}
	},

	ABUSOLUTE_ANCESTOR_RECID {

		@Override
		public void buildCode(CodeBuilder builder, OperateExpr operate)
				throws IOException {
			buildValueExprCallCode(builder, operate, "xAncestorRECIDOfLevel");
		}

		@Override
		protected DataType checkValues(ValueExpr[] values) {
			return GUIDType.TYPE;
		}

		@Override
		void render(ISqlExprBuffer buffer, OperateExpr expr) {
			throw Utils.notImplemented();
		}
	},

	LEVEVL_OF {

		@Override
		public void buildCode(CodeBuilder builder, OperateExpr operate)
				throws IOException {
			buildValueExprCallCode(builder, operate, "xLevelOf");
		}

		@Override
		protected DataType checkValues(ValueExpr[] values) {
			return IntType.TYPE;
		}

		@Override
		void render(ISqlExprBuffer buffer, OperateExpr expr) {
			throw Utils.notImplemented();
		}
	};

	protected DataType checkValues(ValueExpr[] values) {
		DataType type = null;
		for (ValueExpr value : values) {
			type = value.getType();
			if (!(type instanceof EnumTypeImpl<?>)) {
				return type;
			}
		}
		return type;
	}

	abstract void buildCode(CodeBuilder builder, OperateExpr operate)
			throws IOException;

	private static final void buildSQLFuncCallCode(CodeBuilder builder,
			OperateExpr operate, String funcCallName) throws IOException {
		builder.append("SQLFunc.%s", funcCallName);
		builder.append('(');
		for (int i = 0; i < operate.values.length; i++) {
			if (i > 0) {
				builder.append(", ");
			}
			operate.values[i].visit(builder, null);
		}
		builder.append(')');
	}

	private static final void buildValueExprCallCode(CodeBuilder builder,
			OperateExpr operate, String callName) throws IOException {
		operate.values[0].visit(builder, null);
		builder.append(".%s(", callName);
		for (int i = 1; i < operate.values.length; i++) {
			if (i > 1) {
				builder.append(", ");
			}
			operate.values[i].visit(builder, null);
		}
		builder.append(')');
	}

	protected boolean isNonDeterministic() {
		return false;
	}

	abstract void render(ISqlExprBuffer buffer, OperateExpr expr);

	private static final void expect(OperatorImpl operator, ValueExpr[] values,
			int expected) {
		if (values.length != expected) {
			throw new InvalidExpressionException(operator.toString(), expected,
					values.length);
		}
	}

	private static final void expect(OperatorImpl operator, ValueExpr[] values,
			int min, int max) {
		final int c = values.length;
		if (c < min || c > max) {
			throw new InvalidExpressionException(operator.toString(), min,
					values.length);
		}
	}

	private static final void expectMin(OperatorImpl operator,
			ValueExpr[] values, int min) {
		if (values.length < min) {
			throw new InvalidExpressionException(operator.toString(), min,
					values.length);
		}
	}

	private static final DataType checkNumber(OperatorImpl operator,
			ValueExpr expr) {
		DataType type = expr.getType();
		if (!(type instanceof NumberType)) {
			throw new InvalidExpressionException(operator.toString(), "数值",
					type);
		}
		return type;
	}

	private static final DataType checkNumber(OperatorImpl operator,
			ValueExpr[] values) {
		DataType type = checkNumber(operator, values[0]);
		for (int i = 1; i < values.length; i++) {
			type = type.calcPrecedence(checkNumber(operator, values[i]));
		}
		return type;
	}

	static final DataType checkString(OperatorImpl operator, ValueExpr value) {
		DataType type = value.getType();
		if (type.isString()) {
			return type;
		}
		throw new InvalidExpressionException(operator.toString(), "字符串", type);
	}

	static final DataType checkString(OperatorImpl operator, ValueExpr[] values) {
		DataType type = checkString(operator, values[0]);
		for (int i = 1; i < values.length; i++) {
			type = type.calcPrecedence(checkString(operator, values[i]));
		}
		return type;
	}

	static final DataType checkNonLobString(OperatorImpl operator,
			ValueExpr value) {
		DataType type = value.getType();
		if (type.getRootType() == StringType.TYPE && type instanceof CharsType) {
			return type;
		}
		throw new InvalidExpressionException(operator.toString(), "非Lob字符串",
				type);
	}

	private static final DataType checkBytes(OperatorImpl operator,
			ValueExpr value) {
		DataType type = value.getType();
		if (type.isBytes()) {
			return type;
		}
		throw new InvalidExpressionException(operator.toString(), "二进制串", type);
	}

	private static final DataType checkBytes(OperatorImpl operator,
			ValueExpr[] values) {
		DataType type = checkBytes(operator, values[0]);
		for (int i = 1; i < values.length; i++) {
			type = type.calcPrecedence(checkBytes(operator, values[i]));
		}
		return type;
	}

	private static final DataType checkDate(OperatorImpl operator,
			ValueExpr expr) {
		DataType type = expr.getType();
		if (expr.getType() == DateType.TYPE) {
			return type;
		}
		throw new InvalidExpressionException(operator.toString(), "日期时间", type);
	}

}
