package org.eclipse.jt.core.impl;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jt.core.impl.DB2SelectBuffer.DB2SelectColumnBuffer;
import org.eclipse.jt.core.type.DataType;


class DB2SegmentBuffer extends SqlCommandBuffer implements ISqlSegmentBuffer {

	DB2SegmentBuffer() {
		super(null);
	}

	DB2SegmentBuffer(ISqlSegmentBuffer scope) {
		super(scope);
	}

	final ArrayList<ISqlBuffer> statements = new ArrayList<ISqlBuffer>();

	static class VariableDeclare {

		final String name;
		final DataType type;

		public VariableDeclare(String name, DataType type) {
			this.name = DB2ExprBuffer.quote(name);
			this.type = type;
		}

		public void writeTo(SqlStringBuffer sql) {
			sql.append("declare ");
			sql.append(this.name).append(' ');
			this.type.detect(OracleTypeFormatter.instance, sql);
		}
	}

	ArrayList<VariableDeclare> vars = new ArrayList<VariableDeclare>();

	public final void declare(String name, DataType type) {
		if (this.scope == null) {
			if (this.vars == null) {
				this.vars = new ArrayList<VariableDeclare>();
			}
			this.vars.add(new VariableDeclare(DB2ExprBuffer.quote(name), type));
		} else {
			this.scope.declare(name, type);
		}
	}

	private ArrayList<DB2CursorLoopBuffer> cursors;

	final void addCursorDeclare(DB2CursorLoopBuffer cursor) {
		if (this.scope == null) {
			if (this.cursors == null) {
				this.cursors = new ArrayList<DB2CursorLoopBuffer>();
			}
			this.cursors.add(cursor);
		} else {
			DB2SegmentBuffer scope = (DB2SegmentBuffer) this.scope;
			scope.addCursorDeclare(cursor);
		}
	}

	public final DB2InsertBuffer insert(String table) {
		DB2InsertBuffer i = new DB2InsertBuffer(this, table);
		this.statements.add(i);
		return i;
	}

	public final DB2UpdateBuffer update(String table, String alias,
			boolean assignFromSlaveTable) {
		DB2UpdateBuffer u = new DB2UpdateBuffer(this, table, alias,
				assignFromSlaveTable);
		this.statements.add(u);
		return u;
	}

	public final DB2DeleteBuffer delete(String table, String alias) {
		DB2DeleteBuffer d = new DB2DeleteBuffer(this, table, alias);
		this.statements.add(d);
		return d;
	}

	static final class DB2AssignBuffer extends DB2ExprBuffer {

		final String var;

		DB2AssignBuffer(String var) {
			this.var = quote(var);
		}

		@Override
		public final void writeTo(SqlStringBuffer sql,
				List<ParameterReserver> args) {
			sql.append("set ").append(this.var).append('=');
			this.writeTo(sql, args);
		}
	}

	public final DB2AssignBuffer assign(String var) {
		DB2AssignBuffer a = new DB2AssignBuffer(var);
		this.statements.add(a);
		return a;
	}

	static final class DB2SelectIntoBuffer implements ISqlSelectIntoBuffer {

		final ArrayList<DB2RelationRefBuffer> sources = new ArrayList<DB2RelationRefBuffer>();

		public final DB2TableRefBuffer newTable(String table, String alias) {
			DB2TableRefBuffer r = new DB2TableRefBuffer(table, alias);
			this.sources.add(r);
			return r;
		}

		public final DB2QueryRefBuffer newSubQuery(String alias) {
			DB2QueryRefBuffer r = new DB2QueryRefBuffer(alias);
			this.sources.add(r);
			return r;
		}

		final ArrayList<DB2SelectColumnBuffer> columns = new ArrayList<DB2SelectColumnBuffer>();

		public final DB2SelectColumnBuffer newColumn(String var) {
			DB2SelectColumnBuffer c = new DB2SelectColumnBuffer(var);
			this.columns.add(c);
			return c;
		}

		private DB2ExprBuffer where;

		public final DB2ExprBuffer where() {
			if (this.where == null) {
				this.where = new DB2ExprBuffer();
			}
			return this.where;
		}

		public final void writeTo(SqlStringBuffer sql,
				List<ParameterReserver> args) {
			sql.append("set (");
			for (int i = 0, c = this.columns.size(); i < c; i++) {
				if (i > 0) {
					sql.append(',');
				}
				sql.append(this.columns.get(i).alias);
			}
			sql.append(") = (select ");
			for (int i = 0, c = this.columns.size(); i < c; i++) {
				if (i > 0) {
					sql.append(',');
				}
				this.columns.get(i).writeTo(sql, args);
			}
			sql.append(" from ");
			for (int i = 0, c = this.sources.size(); i < c; i++) {
				if (i > 0) {
					sql.append(", ");
				}
				DB2RelationRefBuffer s = this.sources.get(i);
				s.writeTo(sql, args);
			}
			sql.append(')');
		}

	}

	public final DB2SelectIntoBuffer selectInto() {
		DB2SelectIntoBuffer si = new DB2SelectIntoBuffer();
		this.statements.add(si);
		return si;
	}

	public final ISqlConditionBuffer ifThenElse() {
		DB2ConditionBuffer ifs = new DB2ConditionBuffer(this);
		this.statements.add(ifs);
		return ifs;
	}

	public final ISqlLoopBuffer loop() {
		DB2LoopBuffer loop = new DB2LoopBuffer(this);
		this.statements.add(loop);
		return loop;
	}

	public final DB2CursorLoopBuffer cursorLoop(String cursor, boolean forUpdate) {
		DB2CursorLoopBuffer c = new DB2CursorLoopBuffer(this, cursor, forUpdate);
		DB2SegmentBuffer scope = (DB2SegmentBuffer) this.scope;
		scope.addCursorDeclare(c);
		this.statements.add(c);
		return c;
	}

	public final void breakLoop() {
		this.statements.add(LEAVE);
	}

	static final ISqlBuffer LEAVE = new ISqlBuffer() {

		public void writeTo(SqlStringBuffer sql, List<ParameterReserver> args) {
			sql.append("leave");
		}
	};

	public ISqlExprBuffer print() {
		throw new UnsupportedOperationException();
	}

	public void exit() {
		this.statements.add(RETURN);
	}

	public ISqlExprBuffer returnValue() {
		throw new UnsupportedOperationException();
	}

	static final ISqlBuffer RETURN = new ISqlBuffer() {

		public void writeTo(SqlStringBuffer sql, List<ParameterReserver> args) {
			sql.append("return");
		}
	};

	public <T> T getFeature(Class<T> clazz) {
		return null;
	}

	public void writeTo(SqlStringBuffer sql, List<ParameterReserver> args) {
		if (this.scope == null) {
			sql.append("begin atomic ");
			sql.append("declare \"").append(
					DB2ExprBuffer.ROWCOUNT_VAR + "\" int;");
			if (this.vars != null) {
				for (int i = 0, c = this.vars.size(); i < c; i++) {
					this.vars.get(i).writeTo(sql);
					sql.append(';');
				}
			}
		}
		this.writeStatement(sql, args);
		if (this.scope == null) {
			sql.append("end");
		}
	}

	final void writeStatement(SqlStringBuffer sql, List<ParameterReserver> args) {
		for (ISqlBuffer s : this.statements) {
			s.writeTo(sql, args);
			sql.append(';');
		}
	}
}
