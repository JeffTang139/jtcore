package org.eclipse.jt.core.da;

import org.eclipse.jt.core.def.exp.OperateExpression;
import org.eclipse.jt.core.def.query.SQLFunc;
import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.impl.BooleanType;
import org.eclipse.jt.core.impl.BytesType;
import org.eclipse.jt.core.impl.DateType;
import org.eclipse.jt.core.impl.DoubleType;
import org.eclipse.jt.core.impl.GUIDType;
import org.eclipse.jt.core.impl.IntType;
import org.eclipse.jt.core.impl.LongType;
import org.eclipse.jt.core.impl.StringType;
import org.eclipse.jt.core.type.AssignCapability;
import org.eclipse.jt.core.type.DataType;

/**
 * DNA-SQL֧�ֺ����Ĺ��
 * 
 * @author Jeff Tang
 * 
 */
public enum SQLFuncSpec {

	// ---------------------- ���ں��� ----------------------

	getdate(new SQLFuncPattern("��ȡ�������˵�ǰ����ʱ��", DateType.TYPE) {
		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xGetDate();
		}
	}),

	yearof(new SQLFuncPattern("��ȡ���ڱ��ʽ����", IntType.TYPE, ArgumentSpec.date) {

		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xYearOf(values[0]);
		}
	}),

	quarterof(new SQLFuncPattern("�������ڱ��ʽ�����еļ�����", IntType.TYPE,
			ArgumentSpec.date) {

		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xQuarterOf(values[0]);
		}
	}),

	monthof(
			new SQLFuncPattern("�������ڱ��ʽ�����е�����", IntType.TYPE, ArgumentSpec.date) {

				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xMonthOf(values[0]);
				}
			}),

	weekof(
			new SQLFuncPattern("�������ڱ��ʽ�������е�����", IntType.TYPE,
					ArgumentSpec.date) {

				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xWeekOf(values[0]);
				}
			}),

	dayof(
			new SQLFuncPattern("�������ڱ��ʽ�������е�����", IntType.TYPE,
					ArgumentSpec.date) {
				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xDayOf(values[0]);
				}
			}),

	dayofyear(new SQLFuncPattern("�������ڱ��ʽ�������е�����", IntType.TYPE,
			ArgumentSpec.date) {
		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xDayOfYear(values[0]);
		}
	}),

	dayofweek(new SQLFuncPattern("�������ڱ��ʽ�������е�����", IntType.TYPE,
			ArgumentSpec.date) {

		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xDayOfWeek(values[0]);
		}
	}),

	hourof(new SQLFuncPattern("�������ڱ��ʽ��Сʱ��", IntType.TYPE, ArgumentSpec.date) {
		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xHourOf(values[0]);
		}
	}),

	minuteof(new SQLFuncPattern("�������ڱ��ʽ�ķ���", IntType.TYPE, ArgumentSpec.date) {
		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xMinuteOf(values[0]);
		}
	}),

	secondof(new SQLFuncPattern("�������ڱ��ʽ������", IntType.TYPE, ArgumentSpec.date) {
		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xSecondOf(values[0]);
		}
	}),

	millisecondof(new SQLFuncPattern("�������ڱ��ʽ�ĺ�����", IntType.TYPE,
			ArgumentSpec.date) {
		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xMillisecondOf(values[0]);
		}
	}),

	addyear(new SQLFuncPattern("���ڱ��ʽ��������Ϊ��λ��ʱ����", DateType.TYPE,
			ArgumentSpec.date, ArgumentSpec.interval) {
		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xAddYear(values[0], values[1]);
		}
	}),

	addquarter(new SQLFuncPattern("���ڱ��ʽ�����Լ���Ϊ��λ��ʱ����", DateType.TYPE,
			ArgumentSpec.date, ArgumentSpec.interval) {
		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xAddQuarter(values[0], values[1]);
		}
	}),

	addmonth(new SQLFuncPattern("���ڱ��ʽ��������Ϊ��λ��ʱ����", DateType.TYPE,
			ArgumentSpec.date, ArgumentSpec.interval) {
		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xAddMonth(values[0], values[1]);
		}
	}),

	addweek(new SQLFuncPattern("���ڱ��ʽ��������Ϊ��λ��ʱ����", DateType.TYPE,
			ArgumentSpec.date, ArgumentSpec.interval) {
		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xAddWeek(values[0], values[1]);
		}
	}),

	addday(new SQLFuncPattern("���ڱ��ʽ��������Ϊ��λ��ʱ����", DateType.TYPE,
			ArgumentSpec.date, ArgumentSpec.interval) {
		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xAddDay(values[0], values[1]);
		}
	}),

	addhour(new SQLFuncPattern("���ڱ��ʽ������СʱΪ��λ��ʱ����", DateType.TYPE,
			ArgumentSpec.date, ArgumentSpec.interval) {
		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xAddHour(values[0], values[1]);
		}
	}),

	addminute(new SQLFuncPattern("���ڱ��ʽ�����Է�Ϊ��λ��ʱ����", DateType.TYPE,
			ArgumentSpec.date, ArgumentSpec.interval) {

		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xAddMinute(values[0], values[1]);
		}
	}),

	addsecond(new SQLFuncPattern("���ڱ��ʽ��������Ϊ��λ��ʱ����", DateType.TYPE,
			ArgumentSpec.date, ArgumentSpec.interval) {

		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xAddSecond(values[0], values[1]);
		}

	}),

	yeardiff(new SQLFuncPattern("�����������ڵļ������", IntType.TYPE,
			ArgumentSpec.startdate, ArgumentSpec.enddate) {

		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xYearDiff(values[0], values[1]);
		}

	}),

	quarterdiff(new SQLFuncPattern("�����������ڵļ��������", IntType.TYPE,
			ArgumentSpec.startdate, ArgumentSpec.enddate) {

		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xQuarterDiff(values[0], values[1]);
		}

	}),

	monthdiff(new SQLFuncPattern("�����������ڵļ������", IntType.TYPE,
			ArgumentSpec.startdate, ArgumentSpec.enddate) {

		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xMonthDiff(values[0], values[1]);
		}

	}),

	daydiff(new SQLFuncPattern("�����������ڵļ������", IntType.TYPE,
			ArgumentSpec.startdate, ArgumentSpec.enddate) {

		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xDayDiff(values[0], values[1]);
		}

	}),

	weekdiff(new SQLFuncPattern("�����������ڵļ������", IntType.TYPE,
			ArgumentSpec.startdate, ArgumentSpec.enddate) {

		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xWeekDiff(values[0], values[1]);
		}

	}),

	// hourdiff(new SQLFuncPattern("�����������ڵļ��Сʱ��", IntType.TYPE,
	// ArgumentSpec.startdate, ArgumentSpec.enddate) {
	//
	// @Override
	// public OperateExpression expOf(Object[] values) {
	// return SQLFunc.xHourDiff(values[0], values[1]);
	// }
	//
	// }),
	//
	// minutediff(new SQLFuncPattern("�����������ڵļ������", IntType.TYPE,
	// ArgumentSpec.startdate, ArgumentSpec.enddate) {
	//
	// @Override
	// public OperateExpression expOf(Object[] values) {
	// return SQLFunc.xMinuteDiff(values[0], values[1]);
	// }
	//
	// }),
	//
	// seconddiff(new SQLFuncPattern("�����������ڵļ������", IntType.TYPE,
	// ArgumentSpec.startdate, ArgumentSpec.enddate) {
	//
	// @Override
	// public OperateExpression expOf(Object[] values) {
	// return SQLFunc.xSecondDiff(values[0], values[1]);
	// }
	//
	// }),

	truncyear(new SQLFuncPattern("��ȡ���ڵ������һ����������", DateType.TYPE,
			ArgumentSpec.date) {

		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xTruncYear(values[0]);
		}
	}),

	truncmonth(new SQLFuncPattern("��ȡ���ڵ����µ�һ����������", DateType.TYPE,
			ArgumentSpec.date) {

		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xTruncMonth(values[0]);
		}
	}),

	truncday(new SQLFuncPattern("��ȡ���ڵ�������������", DateType.TYPE,
			ArgumentSpec.date) {

		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xTruncDay(values[0]);
		}
	}),

	isleapyear(
			new SQLFuncPattern("�����Ƿ�����", BooleanType.TYPE, ArgumentSpec.date) {

				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xIsLeapYear(values[0]);
				}

			}),

	isleapmonth(new SQLFuncPattern("�����Ƿ�����", BooleanType.TYPE,
			ArgumentSpec.date) {

		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xIsLeapMonth(values[0]);
		}

	}),

	isleapday(
			new SQLFuncPattern("�����Ƿ�����", BooleanType.TYPE, ArgumentSpec.date) {

				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xIsLeapDay(values[0]);
				}

			}),

	// ---------------------- ��ѧ���� ----------------------

	sin(new SQLFuncPattern("���ؽǶ�(�Ի���Ϊ��λ)������ֵ", DoubleType.TYPE,
			ArgumentSpec.radians) {

		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xSin(values[0]);
		}

	}),

	cos(new SQLFuncPattern("���ؽǶ�(�Ի���Ϊ��λ)������ֵ", DoubleType.TYPE,
			ArgumentSpec.radians) {

		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xCos(values[0]);
		}

	}),

	tan(new SQLFuncPattern("���ؽǶ�(�Ի���Ϊ��λ)������ֵ", DoubleType.TYPE,
			ArgumentSpec.radians) {

		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xTan(values[0]);
		}

	}),

	asin(new SQLFuncPattern("���ؽǶ�(�Ի���Ϊ��λ)�ķ�����ֵ", DoubleType.TYPE,
			ArgumentSpec.radians) {

		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xAsin(values[0]);
		}

	}),

	acos(new SQLFuncPattern("���ؽǶ�(�Ի���Ϊ��λ)�ķ�����ֵ", DoubleType.TYPE,
			ArgumentSpec.radians) {

		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xAcos(values[0]);
		}

	}),

	atan(new SQLFuncPattern("���ؽǶ�(�Ի���Ϊ��λ)�ķ�����ֵ", DoubleType.TYPE,
			ArgumentSpec.radians) {

		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xAtan(values[0]);
		}

	}),

	exp(new SQLFuncPattern("����ָ��ֵ��ָ��ֵ", DoubleType.TYPE, ArgumentSpec.power) {

		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xExp(values[0]);
		}

	}),

	power(new SQLFuncPattern("����ָ��ֵ��ָ���ݵ�ֵ", DoubleType.TYPE, ArgumentSpec.base,
			ArgumentSpec.power) {

		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xPower(values[0], values[1]);
		}

	}),

	ln(new SQLFuncPattern("����ָ��ֵ��eΪ�׵Ķ���ֵ,����Ȼ����", DoubleType.TYPE,
			ArgumentSpec.number) {

		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xLn(values[0]);
		}

	}),

	lg(new SQLFuncPattern("����ָ��ֵ��10Ϊ�׵Ķ���ֵ", DoubleType.TYPE,
			ArgumentSpec.number) {

		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xLg(values[0]);
		}

	}),

	sqrt(new SQLFuncPattern("����ָ��ֵ��ƽ����", DoubleType.TYPE, ArgumentSpec.number) {

		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xSqrt(values[0]);
		}

	}),

	ceil(new SQLFuncPattern("���ش��ڻ����ָ��ֵ����С����", LongType.TYPE,
			ArgumentSpec.number) {

		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xCeil(values[0]);
		}

	}),

	floor(new SQLFuncPattern("����С�ڻ����ָ��ֵ���������", LongType.TYPE,
			ArgumentSpec.number) {

		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xFloor(values[0]);
		}

	}),

	round(
			new SQLFuncPattern("������ӽ����ʽ������", LongType.TYPE,
					ArgumentSpec.number) {

				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xRound(values[0], Integer.valueOf(0));
				}

			}, new SQLFuncPattern("�������뵽���ȵ�����", LongType.TYPE,
					ArgumentSpec.number, ArgumentSpec.scale) {

				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xRound(values[0], values[1]);
				}

			}),

	sign(new SQLFuncPattern("���ز����ķ��ź���ֵ", IntType.TYPE, ArgumentSpec.number) {

		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xSign(values[0]);
		}

	}),

	abs(new SQLFuncPattern("���ز����ľ���ֵ", DoubleType.TYPE, ArgumentSpec.number) {

		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xAbs(values[0]);
		}

	}),

	// ---------------------- �ַ������� ----------------------

	chr(new SQLFuncPattern("��ASCII����ת��Ϊ�ַ�", StringType.TYPE, new ArgumentSpec(
			"ascii", "asciiֵ", IntType.TYPE)) {

		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xChr(values[0]);
		}

	}),

	nchr(new SQLFuncPattern("���ؾ���ָ�������������Unicode�ַ�", StringType.TYPE,
			new ArgumentSpec("unicode", "unicodeֵ", IntType.TYPE)) {

		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xNchr(values[0]);
		}

	}),

	ascii(new SQLFuncPattern("�����ַ����ʽ���������ַ���ASCII����ֵ", IntType.TYPE,
			ArgumentSpec.string) {

		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xAscii(values[0]);
		}

	}),

	len(
			new SQLFuncPattern("����ָ���ַ������ʽ���ַ���", IntType.TYPE,
					ArgumentSpec.string) {

				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xLen(values[0]);
				}

			}, new SQLFuncPattern("����ָ���������ַ������ʽ���ֽ���", IntType.TYPE,
					ArgumentSpec.bytes) {

				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xLen(values[0]);
				}

			}),

	indexof(new SQLFuncPattern("���ز����ַ���(search)��ָ���ַ���(str)�е�һ���ֵ����(��1��ʼ)",
			IntType.TYPE, ArgumentSpec.string, ArgumentSpec.search) {

		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xIndexOf(values[0], values[1]);
		}
	}, new SQLFuncPattern("���ز����ַ���(search)��ָ���ַ���(str)�е�һ���ֵ����(��1��ʼ)",
			IntType.TYPE, ArgumentSpec.string, ArgumentSpec.search,
			ArgumentSpec.start_position) {

		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xIndexOf(values[0], values[1], values[2]);
		}
	}),

	lower(new SQLFuncPattern("����д�ַ�����ת��ΪСд�ַ����ݺ󷵻�", StringType.TYPE,
			ArgumentSpec.string) {

		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xLower(values[0]);
		}

	}),

	upper(new SQLFuncPattern("��Сд�ַ�����ת��Ϊ��д�ַ����ݺ󷵻�", StringType.TYPE,
			ArgumentSpec.string) {

		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xUpper(values[0]);
		}

	}),

	ltrim(new SQLFuncPattern("ɾ���ַ����ʽ��ǰ���ո�", StringType.TYPE,
			ArgumentSpec.string) {

		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xLtrim(values[0]);
		}

	}),

	rtrim(new SQLFuncPattern("ɾ���ַ����ʽ��β��ո�", StringType.TYPE,
			ArgumentSpec.string) {

		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xRtrim(values[0]);
		}

	}),

	trim(new SQLFuncPattern("ɾ���ַ����ʽ��ǰ��Ŀո�", StringType.TYPE,
			ArgumentSpec.string) {

		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xTrim(values[0]);
		}

	}),

	substr(new SQLFuncPattern("�����ַ������ʽ���Ӵ�", StringType.TYPE,
			ArgumentSpec.string, ArgumentSpec.start_position) {

		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xSubstr(values[0], values[1]);
		}

	}, new SQLFuncPattern("�����ַ������ʽ���Ӵ�", StringType.TYPE, ArgumentSpec.string,
			ArgumentSpec.start_position, ArgumentSpec.length) {

		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xSubstr(values[0], values[1], values[2]);
		}

	}, new SQLFuncPattern("���ض������ַ������ʽ���Ӵ�", BytesType.TYPE, ArgumentSpec.bytes,
			ArgumentSpec.start_position) {

		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xSubstr(values[0], values[1]);
		}

	}, new SQLFuncPattern("�����ַ������ʽ���Ӵ�", BytesType.TYPE, ArgumentSpec.bytes,
			ArgumentSpec.start_position, ArgumentSpec.length) {

		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xSubstr(values[0], values[1], values[2]);
		}

	}),

	replace(new SQLFuncPattern("�滻ԭ�ַ���(str)�е����������ַ���(search)Ϊ���ַ���(replace)",
			StringType.TYPE, ArgumentSpec.string, ArgumentSpec.search,
			ArgumentSpec.replace) {
		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xReplace(values[0], values[1], values[2]);
		}
	}),

	to_char(new SQLFuncPattern("��ֵ(number)ת��Ϊ���ݿ�������͵��ַ���", StringType.TYPE,
			ArgumentSpec.number) {
		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xToChar(values[0]);
		}
	}, new SQLFuncPattern("����(date)ת��Ϊ���ݿ�������͵��ַ���", StringType.TYPE,
			ArgumentSpec.date) {
		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xToChar(values[0]);
		}
	}, new SQLFuncPattern("GUIDת��Ϊ���ݿ�������͵��ַ���", StringType.TYPE,
			new ArgumentSpec("guid", "GUID", GUIDType.TYPE)) {
		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xToChar(values[0]);
		}
	}, new SQLFuncPattern("�������ַ���(bytes)ת��Ϊ���ݿ�������͵��ַ���", StringType.TYPE,
			new ArgumentSpec("bytes", "�������ַ���", BytesType.TYPE)) {
		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xToChar(values[0]);
		}
	}, new SQLFuncPattern("�ַ���(str)ת��Ϊ���ݿ�������͵��ַ���", StringType.TYPE,
			ArgumentSpec.string) {
		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xToChar(values[0]);
		}
	}), new_recid(new SQLFuncPattern("����GUID����ֵ", GUIDType.TYPE) {

		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xNewRecid();
		}
	}), to_int(
			new SQLFuncPattern("ת��Ϊ������ֵ", IntType.TYPE, ArgumentSpec.string) {
				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xToInt(values[0]);
				}
			})

	;

	private SQLFuncSpec(SQLFuncPattern pattern, SQLFuncPattern... patterns) {
		if (patterns == null) {
			this.patterns = new SQLFuncPattern[] { pattern };
		} else {
			SQLFuncPattern[] arr = new SQLFuncPattern[patterns.length + 1];
			int i = 0;
			arr[i++] = pattern;
			for (SQLFuncPattern p : patterns) {
				arr[i++] = p;
			}
			this.patterns = arr;
		}
	}

	private final SQLFuncPattern[] patterns;

	public final SQLFuncPattern accept(DataType[] types) {
		for (SQLFuncPattern p : this.patterns) {
			if (p.accept(types)) {
				return p;
			}
		}
		return null;
	}

	public final SQLFuncPattern getPattern(int i) {
		return this.patterns[i];
	}

	public final int size() {
		return this.patterns.length;
	}

	/**
	 * �����Ĳ������˵��
	 * 
	 * @author Jeff Tang
	 * 
	 */
	public static class ArgumentSpec {

		private static final ArgumentSpec date = new ArgumentSpec("date",
				"���ڱ��ʽ", DateType.TYPE);

		private static final ArgumentSpec interval = new ArgumentSpec(
				"interval", "ʱ������", LongType.TYPE);

		private static final ArgumentSpec startdate = new ArgumentSpec(
				"startdate", "��ʼ����", DateType.TYPE);

		private static final ArgumentSpec enddate = new ArgumentSpec("enddate",
				"��������", DateType.TYPE);

		private static final ArgumentSpec radians = new ArgumentSpec("radians",
				"����", DoubleType.TYPE);

		private static final ArgumentSpec base = new ArgumentSpec("base", "����",
				DoubleType.TYPE);

		private static final ArgumentSpec power = new ArgumentSpec("power",
				"ָ��", DoubleType.TYPE);

		private static final ArgumentSpec number = new ArgumentSpec("number",
				"��ֵ", DoubleType.TYPE);

		private static final ArgumentSpec string = new ArgumentSpec("str",
				"�ַ���", StringType.TYPE);

		private static final ArgumentSpec bytes = new ArgumentSpec("bytes",
				"�������ַ���", BytesType.TYPE);

		private static final ArgumentSpec search = new ArgumentSpec("search",
				"�����ַ���", StringType.TYPE);

		private static final ArgumentSpec start_position = new ArgumentSpec(
				"start_position", "��ʼλ��", LongType.TYPE);

		private static final ArgumentSpec length = new ArgumentSpec("length",
				"��ȡ����", LongType.TYPE);

		private static final ArgumentSpec scale = new ArgumentSpec("scale",
				"��ȡ����", LongType.TYPE);

		private static final ArgumentSpec replace = new ArgumentSpec("replace",
				"�滻�ַ���", StringType.TYPE);

		private static final ArgumentSpec[] emptyArray = new ArgumentSpec[] {};

		/**
		 * Ĭ�ϵı�־��
		 */
		public final String str;

		/**
		 * ����˵��
		 */
		public final String description;

		/**
		 * ��������������
		 */
		public final DataType type;

		private ArgumentSpec(String str, String description, DataType type) {
			this.str = str;
			this.description = description;
			this.type = type;
		}
	}

	public static abstract class SQLFuncPattern {

		/**
		 * ģʽ��˵��
		 */
		public final String description;

		/**
		 * ģʽ�ķ���ֵ����
		 */
		public final DataType type;

		/**
		 * ģʽ�Ĳ�����Ϣ
		 */
		public final ArgumentSpec[] args;

		private SQLFuncPattern(String description, DataType type,
				ArgumentSpec... args) {
			this.description = description;
			this.type = type;
			if (args == null) {
				this.args = ArgumentSpec.emptyArray;
			} else {
				this.args = args;
			}
		}

		/**
		 * �����Ƿ����ָ�����������б�Ĳ���ֵ
		 * 
		 * @param types
		 * @return
		 */
		public final boolean accept(DataType[] types) {
			if (types == null) {
				throw new NullArgumentException("���������б�Ϊ��.");
			}
			if (types.length != this.args.length) {
				return false;
			}
			final int c = types.length;
			for (int i = 0; i < c; i++) {
				AssignCapability ac = this.args[i].type
						.isAssignableFrom(types[i]);
				if (ac == AssignCapability.SAME
						|| ac == AssignCapability.IMPLICIT) {
					continue;
				}
				return false;
			}
			return true;
		}

		/**
		 * ������ʽ,����ǰȷ�Ϻ����ӿ�ָ���Ĳ���ǩ��������
		 * 
		 * @param values
		 * @return
		 */
		public abstract OperateExpression expOf(Object[] values);

	}

}