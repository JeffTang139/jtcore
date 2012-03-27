package org.eclipse.jt.core.exception;

/**
 * 过程结束调用次数与过程开始调用次数不符异常。
 * 
 * @author Jeff Tang
 */
public class EndProcessException extends CoreException {
	private static final long serialVersionUID = -941874826554628291L;

	public EndProcessException() {
		super("结束过程的调用次数多于开始过程的次数");
	}
}
