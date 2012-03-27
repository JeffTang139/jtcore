package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.da.DBCommand;
import org.eclipse.jt.core.da.RecordIterateAction;
import org.eclipse.jt.core.def.arg.ArgumentDefine;
import org.eclipse.jt.core.def.obja.DynamicObject;

final class DBCommandProxy implements PsHolderProxy, DBCommand {

	public final IStatement getStatement() {
		return this.command.statement;
	}

	public final RecordSetImpl executeQuery() {
		return this.command.executeQuery();
	}

	public final RecordSetImpl executeQueryTop(long rowCount) {
		return this.command.executeQueryTop(rowCount);
	}

	public final RecordSetImpl executeQueryLimit(long offset, long rowCount) {
		return this.command.executeQueryLimit(offset, rowCount);
	}

	public final void iterateQuery(RecordIterateAction action) {
		this.command.iterateQuery(action);
	}

	public final void iterateQueryTop(long rowCount, RecordIterateAction action) {
		this.command.iterateQueryTop(action, rowCount);
	}

	public final void iterateQueryLimit(RecordIterateAction action, long offset,
			long rowCount) {
		this.command.iterateQueryLimit(action, offset, rowCount);
	}

	public final Object executeScalar() {
		return this.command.executeScalar();
	}

	public final int executeUpdate() {
		return this.command.executeUpdate();
	}

	public final int rowCountOf() {
		return (int) this.command.rowCountOf();
	}

	public final long rowCountOfL() {
		return this.command.rowCountOf();
	}

	public final DynamicObject getArgumentsObj() {
		return this.command.getArgumentsObj();
	}

	public final void setArgumentValue(int argIndex, Object argValue) {
		this.command.setArgumentValue(argIndex, argValue);
	}

	public final void setArgumentValue(ArgumentDefine arg, Object argValue) {
		this.command.setArgumentValue(arg, argValue);
	}

	public final void setArgumentValues(Object... argValues) {
		this.command.setArgumentValues(argValues);
	}

	public final void unuse() {
		this.command.unuse();
	}

	final DBCommandImpl command;

	DBCommandProxy(DBAdapterImpl dbAdapter, IStatement statement) {
		this.command = new DBCommandImpl(dbAdapter, statement, this);
	}
}
