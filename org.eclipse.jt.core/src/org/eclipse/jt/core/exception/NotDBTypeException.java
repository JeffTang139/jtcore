package org.eclipse.jt.core.exception;

public final class NotDBTypeException extends CoreException {

	private static final long serialVersionUID = 1L;

	public NotDBTypeException(String type) {
		super("[" + type + "]不是框架支持的数据库类型");
	}
}
