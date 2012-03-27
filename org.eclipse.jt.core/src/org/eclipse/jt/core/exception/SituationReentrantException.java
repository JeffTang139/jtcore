package org.eclipse.jt.core.exception;

/**
 * 景上下文重入异常
 * 
 * @author Jeff Tang
 * 
 */
public class SituationReentrantException extends CoreException {

	/**
     * 
     */
	private static final long serialVersionUID = 1L;

	public SituationReentrantException() {
		super("情景上下文重入异常");
	}

}
