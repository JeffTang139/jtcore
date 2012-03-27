package org.eclipse.jt.core.invoke;

/**
 * 异步处理请求的状态
 * 
 * @author Jeff Tang
 * 
 */
public enum AsyncState {
	/**
	 * 提交了异步请求（本地调用）
	 */
	POSTING(false, false, false),
	/**
	 * 等待调度线程的调度（本地调用）
	 */
	SCHEDULING(false, false, false),
	/**
	 * 因为并发控制的原因，进入并发控制列队中
	 */
	QUEUING(false, false, false),
	/**
	 * 进入并发控制列队中，且被等待结束。已经作废，此状态永远不会出现
	 */
	@Deprecated
	QUEUING_WAITED(false, true, false),
	/**
	 * 已经进入执行列队，只要有空闲的线程就会开始工作
	 */
	STARTING(false, false, false),
	/**
	 * 已经进入执行列队，且被等待结束。已经作废，此状态永远不会出现
	 */
	@Deprecated
	STARTING_WAITED(false, true, false),
	/**
	 * 正在处理
	 */
	PROCESSING(false, false, false),
	/**
	 * 正在处理，且被等待结束。已经作废，此状态永远不会出现
	 */
	@Deprecated
	PROCESSING_WAITED(false, true, false),
	/**
	 * 正在取消中
	 */
	CANCELING(false, false, true),
	/**
	 * 正在取消中，且被等待结束。已经作废，此状态永远不会出现
	 */
	@Deprecated
	CANCELING_WAITED(false, true, true),
	/**
	 * 完成异步处理
	 */
	FINISHED(true, false, false),
	/**
	 * 错误完成
	 */
	ERROR(true, false, false),
	/**
	 * 取消中止
	 */
	CANCELED(true, false, true);
	/**
	 * 是否已经停止
	 */
	public final boolean stopped;

	/**
	 * 是否正被等待，永远返回false
	 */
	@Deprecated
	public final boolean waited;
	/**
	 * 是否有取消动作
	 */
	public final boolean canceling;

	AsyncState(boolean stopped, boolean waited, boolean canceling) {
		this.stopped = stopped;
		this.waited = waited;
		this.canceling = canceling;
	}
}
