package org.eclipse.jt.core.invoke;

/**
 * 异步任务的句柄
 * 
 * @author Jeff Tang
 * 
 * @param <TTask>
 *            任务类型
 */
public interface AsyncTask<TTask extends Task<TMethod>, TMethod extends Enum<TMethod>>
        extends AsyncHandle {
	/**
	 * 获得执行完后的任务
	 * 
	 * @return 返回执行完后的任务
	 * @throws IllegalStateException
	 *             如果任务还未执行完，则抛出该异常
	 */
	public TTask getTask() throws IllegalStateException;

	/**
	 * 获得该任务的执行方法
	 * 
	 * @return 返回执行方法
	 */
	public TMethod getMethod();
}
