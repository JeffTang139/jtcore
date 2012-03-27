package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.exception.ExceptionFromRemote;

/**
 * �쳣����Ϣ���������л���Զ�̽ڵ㻹ԭ�쳣��Ϣ����
 * 
 * @author Jeff Tang
 * 
 */
final class ExceptionInfo {
	public final StackTraceElement[] stackTrace;
	public final String message;
	public final String exceptionClassName;
	public final ExceptionInfo cause;

	final ExceptionFromRemote toException() {
		ExceptionFromRemote efr = new ExceptionFromRemote(this.message,
				this.exceptionClassName, this.cause != null ? this.cause
						.toException() : null);
		efr.setStackTrace(this.stackTrace);
		return efr;
	}

	ExceptionInfo(Throwable e) {
		this.exceptionClassName = e.getClass().getName();
		this.stackTrace = e.getStackTrace();
		this.message = e.getMessage();
		Throwable cause = e.getCause();
		if (cause != null) {
			this.cause = new ExceptionInfo(cause);
		} else {
			this.cause = null;
		}
	}
}