package org.eclipse.jt.core.exception;

/**
 * 不支持带cube汇总输出的groupby
 * 
 * @author Jeff Tang
 * 
 */
public class CubeGroupbyNotSupportedException extends CoreException {

	private static final long serialVersionUID = 7685169230624116812L;

	public CubeGroupbyNotSupportedException() {
		super("不支持Cube类型的汇总.");
	}
}
