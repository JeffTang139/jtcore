package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.obja.DynamicObject;

/**
 * 数据库访问语句实现类
 * 
 * @author Jeff Tang
 * 
 */
abstract class StatementImpl extends ArgumentableImpl implements IStatement {
	public final boolean ignorePrepareIfDBInvalid() {
		return true;
	}

	StatementImpl(String name) {
		super(name, DynamicObject.class);
	}

	StatementImpl(String name, StructDefineImpl arguments) {
		super(name, arguments);
	}

	public final StructDefineImpl getArgumentsDefine() {
		return this.arguments;
	}

	public abstract Sql getSql(DBAdapterImpl dbAdapter);

}
