package org.eclipse.jt.core.info;


/**
 * 过程处理信息
 * 
 * @author Jeff Tang
 * 
 */
public interface ProcessInfo extends Info {
	/**
	 * 获取消耗时长
	 */
	public long getDuration();

	/**
	 * 是否有错误
	 */
	public boolean hasError();

	/**
	 * 过程是否结束
	 */
	public boolean isFinished();
}
