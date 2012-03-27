package org.eclipse.jt.core.impl;

import java.util.List;

import org.eclipse.jt.core.type.DateParser;


class SQLServerExprBuffer extends SqlExprBuffer {
	static final String quote(String name) {
		return "[" + name + "]";
	}

	@Override
	protected ISqlSelectBuffer newSubQuery() {
		return new SQLServerSelectBuffer();
	}

	@Override
	protected void writeSubQuery(SqlStringBuffer sql,
			List<ParameterReserver> args, ISqlSelectBuffer q) {
		((SQLServerSelectBuffer) q).writeTo(sql, args);
	}

	@Override
	public ISqlExprBuffer load(byte[] val) {
		this.beginUpdate(this.count());
		this.append("0x");
		this.bytesToBuffer(val);
		this.endUpdate(PREC_MAX);
		return this;
	}

	@Override
	public ISqlExprBuffer loadDate(long val) {
		this.push("cast('"
				+ DateParser.format(val, DateParser.FORMAT_DATE_TIME)
				+ "' as datetime)");
		return this;
	}

	@Override
	public ISqlExprBuffer loadField(String tableRef, String field) {
		this.push(quote(tableRef) + "." + quote(field));
		return this;
	}

	@Override
	public ISqlExprBuffer loadVar(String name) {
		this.push("@" + name);
		return this;
	}

	private final void datepart(String type, int paramCount) {
		int i = this.count() - paramCount;
		this.beginUpdate(i);
		this.append("datepart(");
		this.append(type);
		this.append(',');
		this.appendBuffer(i, 0);
		this.append(')');
		this.endUpdate(PREC_MAX);
	}

	private final void dateadd(String type, int paramCount) {
		int i = this.count() - paramCount;
		this.beginUpdate(i);
		this.append("dateadd(");
		this.append(type);
		this.append(',');
		this.appendBuffer(i + 1, 0);
		this.append(',');
		this.appendBuffer(i, 0);
		this.append(')');
		this.endUpdate(PREC_MAX);
	}

	private final void datediff(String type, int paramCount) {
		int i = this.count() - paramCount;
		this.beginUpdate(i);
		this.append("datediff(");
		this.append(type);
		this.append(',');
		this.appendBuffer(i, 0);
		this.append(',');
		this.appendBuffer(i + 1, 0);
		this.append(')');
		this.endUpdate(PREC_MAX);
	}

	private final void cast(String type, int paramCount) {
		int i = this.count() - paramCount;
		this.beginUpdate(i);
		this.append("cast(");
		this.appendBuffer(i, 0);
		this.append(" as ");
		this.append(type);
		this.append(")");
		this.endUpdate(PREC_MAX);
	}

	private final void trunc(String type, int paramCount) {
		int i = this.count() - paramCount;
		this.beginUpdate(i);
		this.append("dateadd(");
		this.append(type);
		this.append(",datediff(");
		this.append(type);
		this.append(",0,");
		this.appendBuffer(i, 0);
		this.append("),0)");
		this.endUpdate(PREC_MAX);
	}

	private final void noneNullBin(int i) {
		this.append("isnull(");
		this.appendBuffer(i, PREC_MAX);
		this.append(",0x)");
	}

	@Override
	public ISqlExprBuffer func(SqlFunction func, int paramCount) {
		switch (func) {
			case ADDDAY:
				this.dateadd("day", paramCount);
				break;
			case ADDHOUR:
				this.dateadd("hour", paramCount);
				break;
			case ADDMINUTE:
				this.dateadd("minute", paramCount);
				break;
			case ADDMONTH:
				this.dateadd("month", paramCount);
				break;
			case ADDSECOND:
				this.dateadd("second", paramCount);
				break;
			case ADDWEEK:
				this.dateadd("week", paramCount);
				break;
			case ADDYEAR:
				this.dateadd("year", paramCount);
				break;
			case ADDQUARTER:
				this.dateadd("quarter", paramCount);
				break;
			case BIN_CONCAT: {
				int i = this.count() - paramCount;
				this.beginUpdate(i);
				this.noneNullBin(i);
				for (int j = i + 1; j < this.count(); j++) {
					this.append('+');
					this.noneNullBin(j);
				}
				this.endUpdate(PREC_ADD);
				break;
			}
			case BIN_LEN:
				this.call("datalength", paramCount);
				break;
			case BIN_SUBSTR:
			case SUBSTR: {
				if (paramCount > 2) {
					this.call("substring", paramCount);
				} else {
					int i = this.count() - paramCount;
					this.beginUpdate(i);
					this.append("substring(");
					this.appendBuffer(i, 0);
					this.append(',');
					this.appendBuffer(i + 1, 0);
					this.append(",len(");
					this.appendBuffer(i, 0);
					this.append("))");
					this.endUpdate(PREC_MAX);
				}
				break;
			}
			case BIN_TO_CHAR:
				// CORE2.5
				this.cast("char", paramCount);
				break;
			case CEIL:
				this.call("ceiling", paramCount);
				break;
			case CHR:
				this.call("char", paramCount);
				break;
			case DAYDIFF:
				this.datediff("day", paramCount);
				break;
			case DAYOF:
				this.datepart("day", paramCount);
				break;
			case DAYOFWEEK:
				this.datepart("weekday", paramCount);
				break;
			case DAYOFYEAR:
				this.datepart("dayofyear", paramCount);
				break;
			case HOURDIFF:
				this.datediff("hour", paramCount);
				break;
			case HOUROF:
				this.datepart("hour", paramCount);
				break;
			case INDEXOF: {
				int i = this.count() - paramCount;
				this.beginUpdate(i);
				this.append("charindex(");
				this.appendBuffer(i + 1, 0);
				this.append(',');
				this.appendBuffer(i, 0);
				if (paramCount > 2) {
					this.append(',');
					this.appendBuffer(i + 2, 0);
				}
				this.append(')');
				this.endUpdate(PREC_MAX);
				break;
			}
			case ISLEAPDAY:
				this.call("dbo.dna_isleapday", paramCount);
				break;
			case ISLEAPMONTH:
				this.call("dbo.dna_isleapmonth", paramCount);
				break;
			case ISLEAPYEAR:
				this.call("dbo.dna_isleapyear", paramCount);
				break;
			case LG:
				this.call("log10", paramCount);
				break;
			case LN:
				this.call("log", paramCount);
				break;
			case MILLISECONDOF:
				this.datepart("millisecond", paramCount);
				break;
			case MINUTEDIFF:
				this.datediff("minute", paramCount);
				break;
			case MINUTEOF:
				this.datepart("minute", paramCount);
				break;
			case MONTHDIFF:
				this.datediff("month", paramCount);
				break;
			case MONTHOF:
				this.datepart("month", paramCount);
				break;
			case NEW_RECID:
				// CORE2.5
				this.push("newid()");
				break;
			case NCHR:
				this.call("nchar", paramCount);
				break;
			case QUARTERDIFF:
				this.datediff("quarter", paramCount);
				break;
			case QUARTEROF:
				this.datepart("quarter", paramCount);
				break;
			case ROW_COUNT:
				this.push("@@ROWCOUNT");
				break;
			case SECONDDIFF:
				this.datediff("second", paramCount);
				break;
			case SECONDOF:
				this.datepart("second", paramCount);
				break;
			case STR_CONCAT: {
				int i = this.count() - paramCount;
				this.beginUpdate(i);
				this.coalesceEmptyStr(i);
				for (int j = i + 1; j < this.count(); j++) {
					this.append('+');
					this.coalesceEmptyStr(j);
				}
				this.endUpdate(PREC_ADD);
				break;
			}
			case TO_INT:
				this.cast("int", paramCount);
				break;
			case TO_CHAR:
				this.cast("char", paramCount);
				break;
			case TRIM: {
				int i = this.count() - paramCount;
				this.beginUpdate(i);
				this.append("ltrim(rtrim(");
				this.appendBuffer(i, 0);
				this.append("))");
				this.endUpdate(PREC_MAX);
				break;
			}
			case TRUNCDAY:
				this.trunc("dd", paramCount);
				break;
			case TRUNCMONTH:
				this.trunc("mm", paramCount);
				break;
			case TRUNCYEAR:
				this.trunc("yy", paramCount);
				break;
			case WEEKDIFF:
				this.datediff("week", paramCount);
				break;
			case WEEKOF:
				this.datepart("week", paramCount);
				break;
			case YEARDIFF:
				this.datediff("year", paramCount);
				break;
			case YEAROF:
				this.datepart("year", paramCount);
				break;
			default:
				super.func(func, paramCount);
				break;
		}
		return this;
	}
}
