package org.eclipse.jt.core.impl;

import java.util.List;

import org.eclipse.jt.core.type.DataType;
import org.eclipse.jt.core.type.DateParser;


class DB2ExprBuffer extends SqlExprBuffer {

	static final String quote(String name) {
		return "\"" + name + "\"";
	}

	@Override
	public final DB2ExprBuffer loadNull(DataType type) {
		this.push("null");
		return this;
	}

	@Override
	public ISqlExprBuffer load(byte[] val) {
		this.beginUpdate(this.count());
		this.append("x'");
		this.bytesToBuffer(val);
		this.append('\'');
		this.endUpdate(PREC_MAX);
		return this;
	}

	@Override
	public ISqlExprBuffer loadDate(long val) {
		this.push("timestamp('"
				+ DateParser.format(val, DateParser.FORMAT_DATE_TIME_MS) + "')");
		return this;
	}

	@Override
	public ISqlExprBuffer loadField(String tableRef, String field) {
		this.push(quote(tableRef) + "." + quote(field));
		return this;
	}

	@Override
	public ISqlExprBuffer loadStr(String val) {
		this.push("'" + this.escape(val) + "'");
		return this;
	}

	@Override
	public ISqlExprBuffer mod() {
		this.call("mod", 2);
		return this;
	}

	@Override
	protected DB2SelectBuffer newSubQuery() {
		return new DB2SelectBuffer();
	}

	@Override
	protected void writeSubQuery(SqlStringBuffer sql,
			List<ParameterReserver> args, ISqlSelectBuffer q) {
		((DB2SelectBuffer) q).writeTo(sql, args);
	}

	private final void timestampadd(String interval, int mul, int paramCount) {
		int i = this.count() - paramCount;
		this.beginUpdate(i);
		this.appendBuffer(i, 0);// timestamp
		this.append("+");
		if (mul != 0) {
			this.append("(" + mul);
			this.append("*");
			this.appendBuffer(i + 1, 0);// interval
			this.append(')');
		} else {
			this.appendBuffer(i + 1, 0);// interval
		}
		this.append(' ');
		this.append(interval);
		this.endUpdate(PREC_ADD);
	}

	private final void timestampdiff(int interval, int paramCount) {
		int i = this.count() - paramCount;
		this.beginUpdate(i);
		this.append("timestampdiff(");
		this.append(Integer.toString(interval));
		this.append(",cast(");
		// endTs
		this.appendBuffer(i + 1, 0);
		this.append('-');
		// startTs
		this.appendBuffer(i, 0);
		this.append(" as char(22)))");
		this.endUpdate(PREC_MAX);
	}

	@Override
	public final DB2ExprBuffer func(SqlFunction func, int paramCount) {
		switch (func) {
			case GETDATE: {
				int i = this.count() - paramCount;
				this.beginUpdate(i);
				this.append("current timestamp");
				this.endUpdate(PREC_MAX);
				break;
			}
			case YEAROF:
				this.call("year", paramCount);
				break;
			case QUARTEROF:
				this.call("quarter", paramCount);
				break;
			case MONTHOF:
				this.call("month", paramCount);
				break;
			case WEEKOF:
				this.call("week", paramCount);
				break;
			case DAYOF:
				this.call("day", paramCount);
				break;
			case DAYOFYEAR:
				this.call("dayofyear", paramCount);
				break;
			case DAYOFWEEK:
				this.call("dayofweek", paramCount);
				break;
			case HOUROF:
				this.call("hour", paramCount);
				break;
			case MINUTEOF:
				this.call("minute", paramCount);
				break;
			case SECONDOF:
				this.call("second", paramCount);
				break;
			case MILLISECONDOF: {
				int i = this.count() - paramCount;
				this.beginUpdate(i);
				this.append("microsecond(");
				this.appendBuffer(i, 0);
				this.append(")/1000");
				this.endUpdate(PREC_MUL);
				break;
			}
			case ADDYEAR:
				this.timestampadd("YEARS", 0, paramCount);
				break;
			case ADDQUARTER:
				this.timestampadd("MONTHS", 3, paramCount);
				break;
			case ADDMONTH:
				this.timestampadd("MONTHS", 0, paramCount);
				break;
			case ADDWEEK:
				this.timestampadd("DAYS", 7, paramCount);
				break;
			case ADDDAY:
				this.timestampadd("DAYS", 0, paramCount);
				break;
			case ADDHOUR:
				this.timestampadd("HOURS", 0, paramCount);
				break;
			case ADDMINUTE:
				this.timestampadd("MINUTES", 0, paramCount);
				break;
			case ADDSECOND:
				this.timestampadd("SECONDS", 0, paramCount);
				break;
			case YEARDIFF:
				this.call("dna_yeardiff", paramCount);
				break;
			case QUARTERDIFF:
				this.call("dna_quarterdiff", paramCount);
				break;
			case MONTHDIFF:
				this.call("dna_monthdiff", paramCount);
				break;
			case WEEKDIFF:
				// CORE2.5
				this.timestampdiff(32, paramCount);
				break;
			case DAYDIFF:
				this.call("dna_daydiff", paramCount);
				break;
			case HOURDIFF:
				throw new UnsupportedOperationException();
			case MINUTEDIFF:
				throw new UnsupportedOperationException();
			case SECONDDIFF:
				throw new UnsupportedOperationException();
			case TRUNCYEAR:
				this.call("dna_truncyear", paramCount);
				break;
			case TRUNCMONTH:
				this.call("dna_truncmonth", paramCount);
				break;
			case TRUNCDAY:
				this.call("dna_truncday", paramCount);
				break;
			case ISLEAPYEAR:
				this.call("dna_isleapyear", paramCount);
				break;
			case ISLEAPMONTH:
				this.call("dna_isleapmonth", paramCount);
				break;
			case ISLEAPDAY:
				this.call("dna_isleapday", paramCount);
				break;
			case BIN_CONCAT: {
				int i = this.count() - paramCount;
				this.beginUpdate(i);
				this.coalesceEmptyBin(i);
				for (int j = i + 1; j < this.count(); j++) {
					this.append("||");
					this.coalesceEmptyBin(j);
				}
				this.endUpdate(PREC_ADD);
				break;
			}
			case BIN_LEN:
				this.call("length", paramCount);
				break;
			case BIN_SUBSTR:
				this.call("substr", paramCount);
				break;
			case BIN_TO_CHAR:
				// CORE2.5
				this.call("char", paramCount);
				break;
			case LG:
				this.call("log10", paramCount);
				break;
			case CHR:
				this.call("char", paramCount);
				break;
			case NCHR:
				// XXX
				this.call("char", paramCount);
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
			case INDEXOF: {
				int i = this.count() - paramCount;
				this.beginUpdate(i);
				this.append("locate(");
				this.appendBuffer(i + 1, 0);// search_str
				this.append(',');
				this.appendBuffer(i, 0);// source_str
				if (paramCount > 2) {
					this.append(',');
					this.appendBuffer(i + 2, 0);
				}
				this.append(')');
				this.endUpdate(PREC_MAX);
				break;
			}
			case LEN:
				this.call("length", paramCount);
				break;
			case TO_CHAR:
				this.call("char", paramCount);
				break;
			case TO_INT:
				this.call("integer", paramCount);
				break;
			case NEW_RECID:
				this.call("dna_newrecid", paramCount);
				break;
			case ROW_COUNT: {
				this.useRowcount = true;
				int i = this.count() - paramCount;
				this.beginUpdate(i);
				this.append("\"" + ROWCOUNT_VAR + "\"");
				this.endUpdate(PREC_MAX);
				break;
			}
			case STR_CONCAT: {
				int i = this.count() - paramCount;
				this.beginUpdate(i);
				this.coalesceEmptyStr(i);
				for (int j = i + 1; j < this.count(); j++) {
					this.append("||");
					this.coalesceEmptyStr(j);
				}
				this.endUpdate(PREC_ADD);
				break;
			}
			default:
				super.func(func, paramCount);
				break;
		}
		return this;
	}

	final void coalesceEmptyBin(int j) {
		this.append("coalesce(");
		this.appendBuffer(j, PREC_MAX);
		this.append(",x'')");
	}

	static final String ROWCOUNT_VAR = "$ROWCOUNT";

	boolean useRowcount;

}
