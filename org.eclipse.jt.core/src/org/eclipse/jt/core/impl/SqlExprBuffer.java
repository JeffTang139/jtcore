package org.eclipse.jt.core.impl;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jt.core.type.DataType;
import org.eclipse.jt.core.type.DateParser;


abstract class SqlExprBuffer extends SqlBuffer implements ISqlExprBuffer {
	static final char PREC_MAX = 64;
	static final char PREC_ADD = 6;
	static final char PREC_MUL = 7;
	static final char PREC_NEG = 8;
	static final char PREC_AND = 3;
	static final char PREC_OR = 2;
	static final char PREC_NOT = 4;
	static final char PREC_CMP = 5;
	private char[][] list;
	private int count;
	private ArrayList<ISqlSelectBuffer> subQuery;
	private int index;
	private SqlStringBuffer buffer;
	private int subQueryIndex;
	ArrayList<ParameterReserver> args;

	public SqlExprBuffer() {
		this.buffer = new SqlStringBuffer();
		this.list = new char[4][];
		this.index = -1;
	}

	protected int count() {
		return this.count;
	}

	protected void ensureCount(int count) {
		if (this.list.length < count) {
			int newCount = (this.list.length * 3) / 2 + 1;
			if (newCount < count) {
				newCount = count;
			}
			char[][] arr = new char[newCount][];
			System.arraycopy(this.list, 0, arr, 0, this.list.length);
			this.list = arr;
		}
	}

	protected void beginUpdate(int i) {
		if (this.index != -1) {
			throw new IllegalStateException();
		}
		this.index = i;
		this.buffer.clear();
		this.buffer.append('\0');
		this.subQueryIndex = -1;
	}

	protected void endUpdate(int prec) {
		this.buffer.set(0, (char) prec);
		char[] buffer = new char[this.buffer.size()];
		this.buffer.writeTo(buffer, 0);
		this.count = this.index + 1;
		this.ensureCount(this.count);
		this.list[this.index] = buffer;
		this.index = -1;
		if (this.subQueryIndex != -1) {
			int size = this.subQuery.size();
			while (size > this.subQueryIndex) {
				this.subQuery.remove(--size);
			}
		}
	}

	protected void push(String s) {
		this.beginUpdate(this.count);
		this.append(s);
		this.endUpdate(PREC_MAX);
	}

	protected int getPrec(int i) {
		return this.list[i][0];
	}

	protected void append(String s) {
		this.buffer.append(s);
	}

	protected void append(char c) {
		this.buffer.append(c);
	}

	public ISqlExprBuffer load(long val) {
		this.push(Long.toString(val));
		return this;
	}

	public ISqlExprBuffer load(double val) {
		this.push(Double.toString(val));
		return this;
	}

	public ISqlExprBuffer load(boolean val) {
		this.push(val ? "1" : "0");
		return this;
	}

	protected String escape(String s) {
		StringBuilder sb = null;
		int i = 0;
		int len = s.length();
		int j = 0;
		while (i < len) {
			char c = s.charAt(i);
			switch (c) {
				case '\n':
				case '\r':
				case '\t':
				case '\'':
					if (sb == null) {
						sb = new StringBuilder(s);
					}
					switch (c) {
						case '\n':
							sb.replace(j, ++j, "\n");
							break;
						case '\r':
							sb.replace(j, ++j, "\r");
							break;
						case '\t':
							sb.replace(j, ++j, "\t");
							break;
						case '\'':
							sb.replace(j, ++j, "''");
							break;
					}
					break;
			}
			i++;
			j++;
		}
		return sb == null ? s : sb.toString();
	}

	public ISqlExprBuffer loadDate(long val) {
		this.push("'" + DateParser.format(val, DateParser.FORMAT_DATE_TIME_MS)
				+ "'");
		return this;
	}

	public ISqlExprBuffer loadStr(String val) {
		this.push("'" + this.escape(val) + "'");
		return this;
	}

	public ISqlExprBuffer loadVar(String name) {
		this.push(name);
		return this;
	}

	public ISqlExprBuffer loadVar(ParameterReserver reserver) {
		this.push("?");
		if (this.args == null) {
			this.args = new ArrayList<ParameterReserver>();
		}
		this.args.add(reserver);
		return this;
	}

	public ISqlExprBuffer loadNull(DataType type) {
		this.push("null");
		return this;
	}

	public ISqlExprBuffer loadField(String tableRef, String field) {
		this.push(tableRef + '.' + field);
		return this;
	}

	protected final void bytesToBuffer(byte[] val) {
		for (byte b : val) {
			int j = (b >>> 4) & 0x0f;
			j = j > 9 ? (j - 10 + 'A') : (j + '0');
			this.buffer.append((char) j);
			j = b & 0x0f;
			j = j > 9 ? (j - 10 + 'A') : (j + '0');
			this.buffer.append((char) j);
		}
	}

	public ISqlExprBuffer load(byte[] val) {
		this.beginUpdate(this.count);
		this.append('\'');
		this.bytesToBuffer(val);
		this.append('\'');
		this.endUpdate(PREC_MAX);
		return this;
	}

	protected abstract ISqlSelectBuffer newSubQuery();

	protected abstract void writeSubQuery(SqlStringBuffer sql,
			List<ParameterReserver> args, ISqlSelectBuffer q);

	public ISqlSelectBuffer subQuery() {
		if (this.subQuery == null) {
			this.subQuery = new ArrayList<ISqlSelectBuffer>();
		}
		ISqlSelectBuffer q = this.newSubQuery();
		this.subQuery.add(q);
		this.ensureCount(++this.count);
		char p = (char) (PREC_MAX + this.subQuery.size());
		if (this.list[this.count - 1] == null) {
			this.list[this.count - 1] = new char[] { p };
		} else {
			this.list[this.count - 1][0] = p;
		}
		return q;
	}

	protected final void appendBuffer(int i, int prec) {
		char[] buffer = this.list[i];
		if (buffer[0] > PREC_MAX) {
			this.buffer.append('(');
			ISqlSelectBuffer q = this.subQuery.get(buffer[0] - PREC_MAX - 1);
			if (this.args == null) {
				this.args = new ArrayList<ParameterReserver>();
			}
			this.writeSubQuery(this.buffer, this.args, q);
			this.buffer.append(')');
			if (i < this.subQueryIndex || this.subQueryIndex == -1) {
				this.subQueryIndex = i;
			}
		} else if (prec > buffer[0]) {
			this.buffer.append('(');
			this.buffer.append(buffer, 1, buffer.length - 1);
			this.buffer.append(')');
		} else {
			this.buffer.append(buffer, 1, buffer.length - 1);
		}
	}

	protected final void binary(String op, int prec, int paramCount) {
		int i = this.count - paramCount;
		this.beginUpdate(i);
		this.appendBuffer(i, prec);
		for (int j = i + 1; j < this.count; j++) {
			this.append(op);
			this.appendBuffer(j, prec);
		}
		this.endUpdate(prec);
	}

	protected final void unary(String op, int prec) {
		int i = this.count - 1;
		this.beginUpdate(i);
		this.append(op);
		this.appendBuffer(i, prec);
		this.endUpdate(prec);
	}

	protected final void call(String name, int count) {
		if (count == 0) {
			this.push(name + "()");
		} else {
			int i = this.count - count;
			this.beginUpdate(i);
			this.append(name);
			this.append('(');
			this.appendBuffer(i, 0);
			for (int j = i + 1; j < this.count; j++) {
				this.append(',');
				this.appendBuffer(j, 0);
			}
			this.append(')');
			this.endUpdate(PREC_MAX);
		}
	}

	public ISqlExprBuffer neg() {
		this.unary("-", PREC_NEG);
		return this;
	}

	public ISqlExprBuffer add(int paramCount) {
		this.binary("+", PREC_ADD, paramCount);
		return this;
	}

	public ISqlExprBuffer sub(int paramCount) {
		this.binary("-", PREC_ADD, paramCount);
		return this;
	}

	public ISqlExprBuffer mul(int paramCount) {
		this.binary("*", PREC_MUL, paramCount);
		return this;
	}

	public ISqlExprBuffer div(int paramCount) {
		this.binary("/", PREC_MUL, paramCount);
		return this;
	}

	public ISqlExprBuffer mod() {
		this.binary("%", PREC_MUL, 2);
		return this;
	}

	private final void distinct_func(String name, int paramCount) {
		int i = this.count - paramCount;
		this.beginUpdate(i);
		this.append(name);
		this.append("(distinct ");
		this.appendBuffer(i, 0);
		for (int j = i + 1; j < this.count; j++) {
			this.append(',');
			this.appendBuffer(j, 0);
		}
		this.append(')');
		this.endUpdate(PREC_MAX);
	}

	public ISqlExprBuffer func(SqlFunction func, int paramCount) {
		switch (func) {
			case COUNT:
				if (paramCount == 0) {
					this.push("count(1)");
				} else {
					this.call(func.toString().toLowerCase(), paramCount);
				}
				break;
			case COUNT_DISTINCT:
				if (paramCount == 0) {
					this.push("count(1)");
				} else {
					this.distinct_func("count", paramCount);
				}
				break;
			case AVG_DISTINCT:
				this.distinct_func("avg", paramCount);
				break;
			case SUM_DISTINCT:
				this.distinct_func("sum", paramCount);
				break;
			default:
				this.call(func.toString().toLowerCase(), paramCount);
				break;
		}
		return this;
	}

	public ISqlExprBuffer coalesce(int paramCount) {
		this.call("coalesce", paramCount);
		return this;
	}

	public ISqlExprBuffer simpleCase(int paramCount) {
		int i = this.count - paramCount;
		this.beginUpdate(i);
		this.append("case ");
		this.appendBuffer(i, 0);
		int j = i + 1;
		for (int c = this.count - 1; j < c;) {
			this.append(" when ");
			this.appendBuffer(j++, 0);
			this.append(" then ");
			this.appendBuffer(j++, 0);
		}
		if (j < this.count) {
			this.append(" else ");
			this.appendBuffer(j, 0);
		}
		this.append(" end");
		this.endUpdate(PREC_MAX);
		return this;
	}

	public ISqlExprBuffer searchedCase(int paramCount) {
		int i = this.count - paramCount;
		this.beginUpdate(i);
		this.append("case ");
		int j = i;
		for (int c = this.count - 1; j < c;) {
			this.append("when ");
			this.appendBuffer(j++, 0);
			this.append(" then ");
			this.appendBuffer(j++, 0);
		}
		if (j < this.count) {
			this.append(" else ");
			this.appendBuffer(j, 0);
		}
		this.append(" end");
		this.endUpdate(PREC_MAX);
		return this;
	}

	public ISqlExprBuffer lt() {
		this.binary("<", PREC_CMP, 2);
		return this;
	}

	public ISqlExprBuffer le() {
		this.binary("<=", PREC_CMP, 2);
		return this;
	}

	public ISqlExprBuffer gt() {
		this.binary(">", PREC_CMP, 2);
		return this;
	}

	public ISqlExprBuffer ge() {
		this.binary(">=", PREC_CMP, 2);
		return this;
	}

	public ISqlExprBuffer eq() {
		this.binary("=", PREC_CMP, 2);
		return this;
	}

	public ISqlExprBuffer ne() {
		this.binary("<>", PREC_CMP, 2);
		return this;
	}

	public ISqlExprBuffer and(int paramCount) {
		this.binary(" and ", PREC_AND, paramCount);
		return this;
	}

	public ISqlExprBuffer or(int paramCount) {
		this.binary(" or ", PREC_OR, paramCount);
		return this;
	}

	public ISqlExprBuffer not() {
		this.unary("not ", PREC_NOT);
		return this;
	}

	public ISqlExprBuffer predicate(SqlPredicate pred, int paramCount) {
		int i = this.count - paramCount;
		switch (pred) {
			case BETWEEN:
			case NOT_BETWEEN:
				this.beginUpdate(i);
				this.appendBuffer(i, 0);
				this.append(pred == SqlPredicate.BETWEEN ? " between "
						: " not between ");
				this.appendBuffer(i + 1, 0);
				this.append(" and ");
				this.appendBuffer(i + 2, 0);
				this.endUpdate(PREC_CMP);
				break;
			case EXISTS:
				this.beginUpdate(i);
				this.append("exists");
				this.appendBuffer(i, 0);
				this.endUpdate(PREC_MAX);
				break;
			case IN:
			case NOT_IN:
				this.beginUpdate(i);
				this.appendBuffer(i, 0);
				this.append(pred == SqlPredicate.IN ? " in " : " not in ");
				if (paramCount == 2) {
					this.appendBuffer(i + 1, PREC_MAX - 1);
				} else {
					this.append('(');
					this.appendBuffer(i + 1, 0);
					for (int j = i + 2, c = this.count; j < c; j++) {
						this.append(',');
						this.appendBuffer(j, 0);
					}
					this.append(')');
				}
				this.endUpdate(PREC_CMP);
				break;
			case LIKE:
			case NOT_LIKE:
				this.beginUpdate(i);
				this.appendBuffer(i, 0);
				this.append(pred == SqlPredicate.LIKE ? " like " : " not like ");
				this.appendBuffer(i + 1, 0);
				if (paramCount > 2) {
					this.append(" escape ");
					this.appendBuffer(i + 2, 0);
				}
				this.endUpdate(PREC_CMP);
				break;
			case IS_NULL:
			case IS_NOT_NULL:
				this.beginUpdate(i);
				this.appendBuffer(i, 0);
				this.append(pred == SqlPredicate.IS_NULL ? " is null"
						: " is not null");
				this.endUpdate(PREC_CMP);
				break;
			default:
				throw new IllegalStateException();
		}
		return this;
	}

	public void writeTo(SqlStringBuffer sql, List<ParameterReserver> args) {
		char[] b = this.list[0];
		if (b[0] > PREC_MAX) {
			this.writeSubQuery(sql, args,
					this.subQuery.get(b[0] - PREC_MAX - 1));
		} else {
			sql.append(b, 1, b.length - 1);
		}
		if (args != null && this.args != null) {
			args.addAll(this.args);
		}
	}

	final void coalesceEmptyStr(int i) {
		this.append("coalesce(");
		this.appendBuffer(i, PREC_MAX);
		this.append(", '')");
	}

}
