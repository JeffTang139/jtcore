package org.eclipse.jt.core.spi.log;

/**
 * 日志项类别
 * 
 * @author Jeff Tang
 * 
 */
public enum LogEntryKind {
	/**
	 * 提示<br>
	 */
	HINT,
	/**
	 * 警告<br>
	 */
	WARNING,
	/**
	 * 错误<br>
	 */
	ERROR,
	/**
	 * 过程开始<br>
	 */
	PROCESS_BEGIN,
	/**
	 * 过程成功结束
	 */
	PROCESS_SUCCESS,
	/**
	 * 过程失败结束
	 */
	PROCESS_FAIL
}
