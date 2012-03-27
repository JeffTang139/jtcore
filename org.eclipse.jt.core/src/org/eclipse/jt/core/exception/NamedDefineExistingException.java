package org.eclipse.jt.core.exception;

import org.eclipse.jt.core.def.NamedDefine;

public final class NamedDefineExistingException extends CoreException {

	public NamedDefineExistingException(String msg) {
		super(msg);
	}

	public NamedDefineExistingException(NamedDefine define) {
		super("名称为[" + define.getName() + "]的元素已经存在.");
	}

	private static final long serialVersionUID = -3289360691045446371L;

}
