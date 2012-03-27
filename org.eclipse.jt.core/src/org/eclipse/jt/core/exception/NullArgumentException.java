package org.eclipse.jt.core.exception;

/**
 * 参数为空异常
 * 
 * @author Jeff Tang
 * 
 */
public class NullArgumentException extends CoreException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3342006009116895632L;

	public NullArgumentException(String argumentName) {
		super("参数[" + argumentName + "]不可为空");
	}
}
