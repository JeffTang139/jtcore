package org.eclipse.jt.core.exception;

import org.eclipse.jt.core.def.table.TableDefine;

public final class InvalidTableDefineExceptiopn extends CoreException {

	private static final long serialVersionUID = 2301701985842090978L;

	public final TableDefine table;

	public InvalidTableDefineExceptiopn(TableDefine table, String msg) {
		super(msg);
		this.table = table;
	}

}
