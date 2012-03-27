package org.eclipse.jt.core.invoke;
/**
 * 任务的状态
 * @author Jeff Tang
 *
 */
public enum TaskState {
	/**
	 * 准备中
	 */
	PREPARING,
	/**
	 * 准备完毕
	 */
	PREPARED,
	/**
	 * 准备异常
	 */
	PREPARERROR,
	/**
	 * 处理中
	 */
	PROCESSING,
	/**
	 * 处理异常
	 */
	PROCESSERROR,
	/**
	 * 处理完毕
	 */
	PROCESSED
}
