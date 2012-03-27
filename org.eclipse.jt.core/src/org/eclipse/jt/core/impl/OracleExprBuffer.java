package org.eclipse.jt.core.impl;

import java.util.List;

import org.eclipse.jt.core.type.DateParser;


class OracleExprBuffer extends SqlExprBuffer {
	int firstDayOfWeek = 7;
	private String targetAlias;
	private String alternateAlias;

	static final String quote(String name) {
		return "\"" + name + "\"";
	}

	public void replace(String targetAlias, String alternateAlias) {
		this.targetAlias = targetAlias;
		this.alternateAlias = alternateAlias;
	}

	@Override
	protected ISqlSelectBuffer newSubQuery() {
		OracleSelectBuffer sub = new OracleSelectBuffer();
		sub.replace(this.targetAlias, this.alternateAlias);
		return sub;
	}

	@Override
	protected void writeSubQuery(SqlStringBuffer sql,
			List<ParameterReserver> args, ISqlSelectBuffer q) {
		((OracleSelectBuffer) q).writeTo(sql, args);
	}

	@Override
	public ISqlExprBuffer load(byte[] val) {
		this.beginUpdate(this.count());
		this.append("hextoraw('");
		this.bytesToBuffer(val);
		this.append("')");
		this.endUpdate(PREC_MAX);
		return this;
	}

	@Override
	public ISqlExprBuffer loadDate(long val) {
		this.push("timestamp '"
				+ DateParser.format(val, DateParser.FORMAT_DATE_TIME_MS) + "'");
		return this;
	}

	@Override
	public ISqlExprBuffer loadField(String tableRef, String field) {
		tableRef = quote(tableRef);
		if (this.targetAlias != null && tableRef.equals(this.targetAlias)) {
			tableRef = this.alternateAlias;
		}
		this.push(tableRef + "." + quote(field));
		return this;
	}

	@Override
	public ISqlExprBuffer loadVar(String name) {
		return super.loadVar(quote(name));
	}

	@Override
	public ISqlExprBuffer mod() {
		this.call("mod", 2);
		return this;
	}

	private final void add_interval(int paramCount, String type) {
		int i = this.count() - paramCount;
		this.beginUpdate(i);
		this.appendBuffer(i, PREC_ADD);
		this.append("+numtodsinterval(");
		this.appendBuffer(i + 1, 0);
		this.append(",'");
		this.append(type);
		this.append("')");
		this.endUpdate(PREC_ADD);
	}

	@SuppressWarnings("unused")
	private final void extract(String type, int paramCount) {
		int i = this.count() - paramCount;
		this.beginUpdate(i);
		this.append("extract(");
		this.append(type);
		this.append(" from ");
		this.appendBuffer(i, 0);
		this.append(')');
		this.endUpdate(PREC_MAX);
	}

	private final void trunc(String type, int paramCount) {
		int i = this.count() - paramCount;
		this.beginUpdate(i);
		this.append("trunc(");
		this.appendBuffer(i, 0);
		this.append(",'");
		this.append(type);
		this.append("')");
		this.endUpdate(PREC_MAX);
	}

	private final void dnafunc(String func, int paramCount) {
		this.call("dna.".concat(func), paramCount);
	}

	@Override
	public ISqlExprBuffer func(SqlFunction func, int paramCount) {
		switch (func) {
		case ADDDAY:
			this.add_interval(paramCount, "day");
			break;
		case ADDHOUR:
			this.add_interval(paramCount, "hour");
			break;
		case ADDMINUTE:
			this.add_interval(paramCount, "minute");
			break;
		case ADDMONTH: {
			int i = this.count() - paramCount;
			this.beginUpdate(i);
			this.append("cast(add_months(");
			this.appendBuffer(i, 0);
			this.append(',');
			this.appendBuffer(i + 1, 0);
			this.append(") as timestamp)");
			this.endUpdate(PREC_MAX);
			break;
		}
		case ADDQUARTER: {
			int i = this.count() - paramCount;
			this.beginUpdate(i);
			this.append("cast(add_months(");
			this.appendBuffer(i, 0);
			this.append(",3*(");
			this.appendBuffer(i + 1, PREC_MUL);
			this.append(")) as timestamp)");
			this.endUpdate(PREC_MAX);
			break;
		}
		case ADDSECOND:
			this.add_interval(paramCount, "second");
			break;
		case ADDWEEK: {
			int i = this.count() - paramCount;
			this.beginUpdate(i);
			this.appendBuffer(i, PREC_ADD);
			this.append("+numtodsinterval((");
			this.appendBuffer(i + 1, PREC_MUL);
			this.append(")*7,'day')");
			this.endUpdate(PREC_ADD);
			break;
		}
		case ADDYEAR: {
			int i = this.count() - paramCount;
			this.beginUpdate(i);
			this.append("cast(add_months(");
			this.appendBuffer(i, 0);
			this.append(",(");
			this.appendBuffer(i + 1, PREC_MUL);
			this.append(")*12) as timestamp)");
			this.endUpdate(PREC_MAX);
			break;
		}
		case BIN_CONCAT: {
			int i = this.count() - paramCount;
			this.beginUpdate(i);
			int j = i;
			int deep = 0;
			int count = this.count();
			while (j < count) {
				this.append("utl_raw.concat(");
				deep++;
				int c = j + 11;
				if (c > count) {
					c = count;
				}
				this.appendBuffer(j++, 0);
				while (j < c) {
					this.append(',');
					this.appendBuffer(j++, 0);
				}
				if (j < count) {
					this.append(',');
				}
				if (count - j == 1) {
					this.appendBuffer(j++, 0);
				}
			}
			for (; deep > 0; deep--) {
				this.append(')');
			}
			this.endUpdate(PREC_MAX);
			break;
		}
		case BIN_LEN:
			this.call("utl_raw.length", paramCount);
			break;
		case BIN_SUBSTR:
			this.call("utl_raw.substr", paramCount);
			break;
		case BIN_TO_CHAR:
			this.call("rawtohex", paramCount);
			break;
		case DAYDIFF: {
			// int i = this.count() - paramCount;
			// this.beginUpdate(i);
			// this.append("trunc(");
			// this.appendBuffer(i, 0);
			// this.append(",'dd')-trunc(");
			// this.appendBuffer(i + 1, 0);
			// this.append(",'dd')");
			// this.endUpdate(PREC_ADD);
			this.dnafunc("daydiff", paramCount);
			break;
		}
		case DAYOF:
			// this.extract("day", paramCount);
			this.dnafunc("dayofmonth", paramCount);
			break;
		case DAYOFWEEK: {
			// int i = this.count() - paramCount;
			// this.beginUpdate(i);
			// this.append("trunc(");
			// this.appendBuffer(i, 0);
			// this.append(",'dd')-trunc(");
			// this.appendBuffer(i, 0);
			// this.append(",'iw')+1");
			// this.endUpdate(PREC_ADD);
			this.dnafunc("dayofweek", paramCount);
			break;
		}
		case DAYOFYEAR:
			// this.extract("year", paramCount);
			this.dnafunc("dayofyear", paramCount);
			break;
		case GETDATE:
			this.push("systimestamp");
			break;
		case HOURDIFF:
			throw new UnsupportedOperationException();
		case HOUROF:
			// this.extract("hour", paramCount);
			this.dnafunc("hour", paramCount);
			break;
		case INDEXOF: {
			int i = this.count() - paramCount;
			this.beginUpdate(i);
			this.append("instr(");
			this.appendBuffer(i, 0);
			this.append(',');
			this.appendBuffer(i + 1, 0);
			if (paramCount > 2) {
				this.append(',');
				this.appendBuffer(i + 2, 0);
			}
			this.append(')');
			this.endUpdate(PREC_MAX);
			break;
		}
		case ISLEAPDAY: {
			// int i = this.count() - paramCount;
			// this.beginUpdate(i);
			// this.append("case when extract(month from ");
			// this.appendBuffer(i, 0);
			// this.append(")=2 and extract(day from ");
			// this.appendBuffer(i, 0);
			// this.append(")=29 then 1 else 0 end");
			// this.endUpdate(PREC_MAX);
			this.dnafunc("isleapday", paramCount);
			break;
		}
		case ISLEAPMONTH: {
			// int i = this.count() - paramCount;
			// this.beginUpdate(i);
			// this.append("decode(extract(day from last_day(");
			// this.appendBuffer(i, 0);
			// this.append(")),29,1,0)");
			// this.endUpdate(PREC_MAX);
			this.dnafunc("isleapmonth", paramCount);
			break;
		}
		case ISLEAPYEAR: {
			// int i = this.count() - paramCount;
			// this.beginUpdate(i);
			// this.append("decode(extract(day from last_day(add_months(trunc(");
			// this.appendBuffer(i, 0);
			// this.append(",'y'),1))),29,1,0)");
			// this.endUpdate(PREC_MAX);
			this.dnafunc("isleapyear", paramCount);
			break;
		}
		case LEN:
			this.call("length", paramCount);
			break;
		case LG: {
			int i = this.count() - paramCount;
			this.beginUpdate(i);
			this.append("log(10,");
			this.appendBuffer(i, 0);
			this.append(')');
			this.endUpdate(PREC_MAX);
			break;
		}
		case MILLISECONDOF: {
			// int i = this.count() - paramCount;
			// this.beginUpdate(i);
			// this.append("to_number(to_char(");
			// this.appendBuffer(i, 0);
			// this.append(",'ff3'))");
			// this.endUpdate(PREC_MAX);
			this.dnafunc("millisecond", paramCount);
			break;
		}
		case MINUTEDIFF:
			throw new UnsupportedOperationException();
		case MINUTEOF:
			// this.extract("minute", paramCount);
			this.dnafunc("hour", paramCount);
			break;
		case MONTHDIFF: {
			// int i = this.count() - paramCount;
			// this.beginUpdate(i);
			// this.append("months_between(trunc(");
			// this.appendBuffer(i + 1, 0);
			// this.append(",'mm'),trunc(");
			// this.appendBuffer(i, 0);
			// this.append(",'mm'))");
			// this.endUpdate(PREC_MAX);
			this.dnafunc("monthdiff", paramCount);
			break;
		}
		case MONTHOF:
			// this.extract("month", paramCount);
			this.dnafunc("month", paramCount);
			break;
		case NEW_RECID:
			this.push("sys_guid()");
			break;
		case QUARTERDIFF: {
			// int i = this.count() - paramCount;
			// this.beginUpdate(i);
			// this.append("months_between(trunc(");
			// this.appendBuffer(i + 1, 0);
			// this.append(",'q'),trunc(");
			// this.appendBuffer(i, 0);
			// this.append(",'q'))/3");
			// this.endUpdate(PREC_MUL);
			this.dnafunc("quarterdiff", paramCount);
			break;
		}
		case QUARTEROF: {
			// int i = this.count() - paramCount;
			// this.beginUpdate(i);
			// this.append("ceil(extract(month from ");
			// this.appendBuffer(i, 0);
			// this.append(")/3)");
			// this.endUpdate(PREC_MAX);
			this.dnafunc("quarter", paramCount);
			break;
		}
		case ROW_COUNT:
			this.push("SQL%ROWCOUNT");
			break;
		case SECONDDIFF:
			throw new UnsupportedOperationException();
		case SECONDOF: {
			// int i = this.count() - paramCount;
			// this.beginUpdate(i);
			// this.append("floor(extract(second from ");
			// this.appendBuffer(i, 0);
			// this.append("))");
			// this.endUpdate(PREC_MAX);
			this.dnafunc("second", paramCount);
			break;
		}
		case STR_CONCAT:
			this.binary("||", PREC_ADD, paramCount);
			break;
		case TO_INT: {
			int i = this.count() - paramCount;
			this.beginUpdate(i);
			this.append("to_number(");
			this.appendBuffer(i, 0);
			this.append(",'9999999999')");
			this.endUpdate(PREC_MAX);
			break;
		}
		case TRUNCDAY:
			this.trunc("dd", paramCount);
			// this.dnafunc("truncday", paramCount);
			break;
		case TRUNCMONTH:
			this.trunc("mm", paramCount);
			// this.dnafunc("truncmonth", paramCount);
			break;
		case TRUNCYEAR:
			this.trunc("yy", paramCount);
			// this.dnafunc("truncyear", paramCount);
			break;
		case WEEKDIFF: {
			// int i = this.count() - paramCount;
			if (this.firstDayOfWeek == 1) {
				// this.beginUpdate(i);
				// this.append("trunc((trunc(");
				// this.appendBuffer(i + 1, 0);
				// this.append(",'iw')-trunc(");
				// this.appendBuffer(i, 0);
				// this.append(",'iw'))/7)");
				// this.endUpdate(PREC_MAX);
				this.dnafunc("weekdiff_iso", paramCount);
			} else {
				// this.beginUpdate(i);
				// this.append("trunc((trunc(");
				// this.appendBuffer(i + 1, 0);
				// this.append(",'d')-trunc(");
				// this.appendBuffer(i, 0);
				// this.append(",'d'))/7)");
				// this.endUpdate(PREC_MAX);
				this.dnafunc("weekdiff", paramCount);
			}
			break;
		}
		case WEEKOF: {
			if (this.firstDayOfWeek == 1) {
				// int i = this.count() - paramCount;
				// this.beginUpdate(i);
				// this.append("trunc((trunc(");
				// this.appendBuffer(i, 0);
				// this.append(",'iw')-trunc(trunc(");
				// this.appendBuffer(i, 0);
				// this.append(",'y'),'iw'))/7+1)");
				// this.endUpdate(PREC_MAX);
				this.dnafunc("weekofyear_iso", paramCount);
			} else {
				// int i = this.count() - paramCount;
				// this.beginUpdate(i);
				// this.append("trunc((trunc(");
				// this.appendBuffer(i, 0);
				// this.append(",'d')-trunc(trunc(");
				// this.appendBuffer(i, 0);
				// this.append(",'y'),'d'))/7+1)");
				// this.endUpdate(PREC_MAX);
				this.dnafunc("weekofyear", paramCount);
			}
			break;
		}
		case YEARDIFF: {
			// int i = this.count() - paramCount;
			// this.beginUpdate(i);
			// this.append("months_between(trunc(");
			// this.appendBuffer(i + 1, 0);
			// this.append(",'y'),trunc(");
			// this.appendBuffer(i, 0);
			// this.append(",'y'))/12");
			// this.endUpdate(PREC_MUL);
			this.dnafunc("yeardiff", paramCount);
			break;
		}
		case YEAROF:
			// this.extract("year", paramCount);
			this.dnafunc("year", paramCount);
			break;
		default:
			super.func(func, paramCount);
			break;
		}
		return this;
	}
}
