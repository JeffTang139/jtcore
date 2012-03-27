package org.eclipse.jt.core.impl;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jt.core.type.DataType;


class MySqlSegmentBuffer extends MySqlCommandBuffer implements
		ISqlSegmentBuffer, ISqlReplaceCommandFactory,
		ISqlUpdateMultiCommandFactory, ISqlDeleteMultiCommandFactory {

	final ArrayList<ISqlBuffer> stmts = new ArrayList<ISqlBuffer>();

	MySqlSegmentBuffer(ISqlSegmentBuffer scope) {
		super(scope);
	}

	public void declare(String name, DataType type) {
		throw new UnsupportedOperationException();
	}

	public MySqlInsertBuffer insert(String table) {
		MySqlInsertBuffer i = new MySqlInsertBuffer(this, table);
		this.stmts.add(i);
		return i;
	}

	public MySqlDeleteBuffer delete(String table, String alias) {
		MySqlDeleteBuffer d = new MySqlDeleteBuffer(this, table, alias);
		this.stmts.add(d);
		return d;
	}

	public ISqlUpdateBuffer update(String table, String alias,
			boolean assignFromSlaveTable) {
		MySqlUpdateBuffer i = new MySqlUpdateBuffer(this, table, alias,
				assignFromSlaveTable);
		this.stmts.add(i);
		return i;
	}

	public ISqlUpdateMultiBuffer updateMulti(String table, String alias) {
		MySqlUpdateMultiBuffer u = new MySqlUpdateMultiBuffer(this, table,
				alias);
		this.stmts.add(u);
		return u;
	}

	public ISqlDeleteMultiBuffer deleteMulti(String table, String alias) {
		MySqlDeleteMultiBuffer d = new MySqlDeleteMultiBuffer(this, table,
				alias);
		this.stmts.add(d);
		return d;
	}

	public MySqlReplaceBuffer replace(String table) {
		MySqlReplaceBuffer r = new MySqlReplaceBuffer(this, table);
		this.stmts.add(r);
		return r;
	}

	public ISqlExprBuffer assign(String var) {
		throw new UnsupportedOperationException();
	}

	public ISqlSelectIntoBuffer selectInto() {
		throw new UnsupportedOperationException();
	}

	public ISqlConditionBuffer ifThenElse() {
		throw new UnsupportedOperationException();
	}

	public ISqlLoopBuffer loop() {
		throw new UnsupportedOperationException();
	}

	public ISqlCursorLoopBuffer cursorLoop(String cursor, boolean forUpdate) {
		throw new UnsupportedOperationException();
	}

	public void breakLoop() {
		throw new UnsupportedOperationException();
	}

	public ISqlExprBuffer print() {
		throw new UnsupportedOperationException();
	}

	public void exit() {
		throw new UnsupportedOperationException();
	}

	public ISqlExprBuffer returnValue() {
		throw new UnsupportedOperationException();
	}

	@SuppressWarnings("unchecked")
	public <T> T getFeature(Class<T> clazz) {
		if (clazz == ISqlReplaceCommandFactory.class) {
			return (T) this;
		} else if (clazz == ISqlUpdateMultiCommandFactory.class) {
			return (T) this;
		} else if (clazz == ISqlDeleteMultiCommandFactory.class) {
			return (T) this;
		}
		return null;
	}

	public void writeTo(SqlStringBuffer sql, List<ParameterReserver> args) {
		for (int i = 0, c = this.stmts.size(); i < c; i++) {
			this.stmts.get(i).writeTo(sql, args);
			sql.append(';');
		}
	}

}
