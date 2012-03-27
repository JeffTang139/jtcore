package org.eclipse.jt.core.exception;

public class InvalidDerivedQueryDomainException extends CoreException {

	private static final long serialVersionUID = 5925413245205714192L;

	public InvalidDerivedQueryDomainException() {
		super("DerivedQueryDefine的使用域不是其创建域.");
	}

}
