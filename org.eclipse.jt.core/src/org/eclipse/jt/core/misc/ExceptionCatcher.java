package org.eclipse.jt.core.misc;

public interface ExceptionCatcher {
	/**
	 * 处置异常，并保证不抛出额外的异常
	 * @param e 异常
	 * @param sender 错误相关的对象 
	 */
	public void catchException(Throwable e,Object sender);
}
