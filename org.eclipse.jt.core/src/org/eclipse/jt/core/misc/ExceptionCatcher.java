package org.eclipse.jt.core.misc;

public interface ExceptionCatcher {
	/**
	 * �����쳣������֤���׳�������쳣
	 * @param e �쳣
	 * @param sender ������صĶ��� 
	 */
	public void catchException(Throwable e,Object sender);
}
