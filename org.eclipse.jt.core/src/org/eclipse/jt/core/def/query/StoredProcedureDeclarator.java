package org.eclipse.jt.core.def.query;

import org.eclipse.jt.core.impl.StoredProcedureDefineImpl;

public abstract class StoredProcedureDeclarator extends
		ModifyStatementDeclarator<StoredProcedureDefine> {

	protected StoredProcedureDeclare procedure;

	@Override
	public StoredProcedureDefine getDefine() {
		return this.procedure;
	}

	public StoredProcedureDeclarator(String name) {
		super(false);
		this.procedure = new StoredProcedureDefineImpl(name, this);
	}

	private final static Class<?>[] intf_classes = { StoredProcedureDefine.class };

	@Override
	protected Class<?>[] getDefineIntfRegClasses() {
		return intf_classes;
	}

}
