package org.eclipse.jt.core.impl;

import java.util.List;

import org.eclipse.jt.core.type.DateParser;


class MySqlExprBuffer extends SqlExprBuffer {

	static final String quote(String name) {
		return "`" + name + "`";
	}

	@Override
	public final MySqlExprBuffer load(byte[] val) {
		this.beginUpdate(this.count());
		this.append("0x");
		super.bytesToBuffer(val);
		this.endUpdate(PREC_MAX);
		return this;
	}

	@Override
	public final MySqlExprBuffer loadDate(long val) {
		this.push("timestamp'"
				+ DateParser.format(val, DateParser.FORMAT_DATE_TIME_MS) + "'");
		return this;
	}

	@Override
	public final MySqlExprBuffer loadField(String table, String field) {
		this.push(quote(table) + "." + quote(field));
		return this;
	}

	@Override
	public final ISqlExprBuffer func(SqlFunction func, int paramCount) {
		switch (func) {
			case GETDATE:
				this.call("now", paramCount);
				break;
			case YEAROF:
				this.call("year", paramCount);
				break;
			case QUARTEROF:
				this.call("quarter", paramCount);
				break;
			case MONTHOF:
				this.call("month", paramCount);
				break;
			case WEEKOF: {
				int FIRST_DAY_OF_WEEK = 7;
				if (FIRST_DAY_OF_WEEK == 7) {
					int i = this.count() - paramCount;
					this.beginUpdate(i);
					this.append("week(");
					this.appendBuffer(i, 0);
					this.append(",0)+1");
					this.endUpdate(PREC_ADD);
					break;
				} else if (FIRST_DAY_OF_WEEK == 1) {
					int i = this.count() - paramCount;
					this.beginUpdate(i);
					this.append("week(");
					this.appendBuffer(i, 0);
					this.append(",1)+1");
					this.endUpdate(PREC_ADD);
					break;
				}
				throw new UnsupportedOperationException();
			}
			case DAYOF:
				this.call("dayofmonth", paramCount);
				break;
			case DAYOFYEAR:
				this.call("dayofyear", paramCount);
				break;
			case DAYOFWEEK: {
				int i = this.count() - paramCount;
				this.beginUpdate(i);
				this.append("weekday(");
				this.appendBuffer(i, 0);
				this.append(")+1");
				this.endUpdate(PREC_ADD);
				break;
			}
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
				this.timestampadd("year", paramCount);
				break;
			case ADDQUARTER:
				this.timestampadd("quarter", paramCount);
				break;
			case ADDMONTH:
				this.timestampadd("month", paramCount);
				break;
			case ADDWEEK:
				this.timestampadd("week", paramCount);
				break;
			case ADDDAY:
				this.timestampadd("day", paramCount);
				break;
			case ADDHOUR:
				this.timestampadd("hour", paramCount);
				break;
			case ADDMINUTE:
				this.timestampadd("minute", paramCount);
				break;
			case ADDSECOND:
				this.timestampadd("second", paramCount);
				break;
			case YEARDIFF:
				this.timestampdiff("year", paramCount);
				break;
			case QUARTERDIFF:
				this.timestampdiff("quarter", paramCount);
				break;
			case MONTHDIFF:
				this.timestampdiff("month", paramCount);
				break;
			case WEEKDIFF:
				// CORE2.5
				this.timestampdiff("week", paramCount);
				break;
			case DAYDIFF:
				this.timestampdiff("day", paramCount);
				break;
			case HOURDIFF:
				this.timestampdiff("hour", paramCount);
				break;
			case MINUTEDIFF:
				this.timestampdiff("minute", paramCount);
				break;
			case SECONDDIFF:
				this.timestampdiff("second", paramCount);
				break;
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
			case GROUPING:
				// CORE2.5
				this.load(-1);
				break;
			case BIN_CONCAT: {
				int i = this.count() - paramCount;
				this.beginUpdate(i);
				this.append("unhex(concat(");
				this.coalesceEmptyBin(i);
				for (int j = i + 1; j < this.count(); j++) {
					this.append(',');
					this.coalesceEmptyBin(j);
				}
				this.append("))");
				this.endUpdate(PREC_MAX);
				break;
			}
			case BIN_LEN:
				this.call("length", paramCount);
				break;
			case BIN_SUBSTR:
				// CORE2.5
				this.call("substr", paramCount);
				break;
			case BIN_TO_CHAR:
				this.call("hex", paramCount);
				break;
			case CHR:
				// CORE2.5
				this.call("char", paramCount);
				break;
			case NCHR:
				// CORE2.5
				this.call("char", paramCount);
				break;
			case LEN:
				this.call("char_length", paramCount);
				break;
			case INDEXOF:
				this.call("locate", paramCount);
				break;
			case REPLACE:
				this.call("replace", paramCount);
				break;
			case STR_CONCAT: {
				int i = this.count() - paramCount;
				this.beginUpdate(i);
				this.append("concat(");
				this.coalesceEmptyStr(i);
				for (int j = i + 1; j < this.count(); j++) {
					this.append(',');
					this.coalesceEmptyStr(j);
				}
				this.append(")");
				this.endUpdate(PREC_MAX);
				break;
			}
			case SUBSTR:
				this.call("substr", paramCount);
				break;
			case LG:
				this.call("log10", paramCount);
				break;
			case NEW_RECID:
				this.call("dna_newrecid", paramCount);
				break;
			case TO_CHAR: {
				int i = this.count() - paramCount;
				this.beginUpdate(i);
				this.append("cast(");
				this.appendBuffer(i, 0);
				this.append(" as char)");
				this.endUpdate(PREC_MAX);
				break;
			}
			case TO_INT: {
				int i = this.count() - paramCount;
				this.beginUpdate(i);
				this.append("cast(");
				this.appendBuffer(i, 0);
				this.append(" as decimal)");
				this.endUpdate(PREC_MAX);
				break;
			}
			case ROW_COUNT:
				this.call("row_count", paramCount);
				break;
			default:
				super.func(func, paramCount);
		}
		return this;
	}

	private final void timestampadd(String unit, int paramCount) {
		int i = this.count() - paramCount;
		this.beginUpdate(i);
		this.append("timestampadd(");
		this.append(unit);
		this.append(',');
		// interval
		this.appendBuffer(i + 1, 0);
		this.append(',');
		// ts
		this.appendBuffer(i, 0);
		this.append(")");
		this.endUpdate(PREC_MAX);
	}

	private final void timestampdiff(String unit, int paramCount) {
		int i = this.count() - paramCount;
		this.beginUpdate(i);
		this.append("timestampdiff(");
		this.append(unit);
		this.append(',');
		// startTs
		this.appendBuffer(i, 0);
		this.append(',');
		// endTs
		this.appendBuffer(i + 1, 0);
		this.append(")");
		this.endUpdate(PREC_MAX);
	}

	final void coalesceEmptyBin(int i) {
		this.append("hex(coalesce(");
		this.appendBuffer(i, PREC_MAX);
		this.append(",0x))");
	}

	@Override
	protected final ISqlSelectBuffer newSubQuery() {
		return new MySqlSelectBuffer(this.command);
	}

	@Override
	protected final void writeSubQuery(SqlStringBuffer sql,
			List<ParameterReserver> args, ISqlSelectBuffer q) {
		((MySqlSelectBuffer) q).writeTo(sql, args);
	}

	final MySqlCommandBuffer command;

	MySqlExprBuffer(MySqlCommandBuffer command) {
		this.command = command;
	}

}
