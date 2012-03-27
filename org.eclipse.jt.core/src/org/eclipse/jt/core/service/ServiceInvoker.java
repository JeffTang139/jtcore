package org.eclipse.jt.core.service;

import org.eclipse.jt.core.LifeHandle;
import org.eclipse.jt.core.ListQuerier;
import org.eclipse.jt.core.None;
import org.eclipse.jt.core.ObjectQuerier;
import org.eclipse.jt.core.TreeQuerier;
import org.eclipse.jt.core.exception.DeadLockException;
import org.eclipse.jt.core.invoke.AsyncHandle;
import org.eclipse.jt.core.invoke.AsyncResult;
import org.eclipse.jt.core.invoke.AsyncResultList;
import org.eclipse.jt.core.invoke.AsyncTask;
import org.eclipse.jt.core.invoke.Event;
import org.eclipse.jt.core.invoke.OneKeyOverlappedResult;
import org.eclipse.jt.core.invoke.OneKeyOverlappedResultList;
import org.eclipse.jt.core.invoke.SimpleTask;
import org.eclipse.jt.core.invoke.Task;
import org.eclipse.jt.core.invoke.ThreeKeyOverlappedResult;
import org.eclipse.jt.core.invoke.ThreeKeyOverlappedResultList;
import org.eclipse.jt.core.invoke.TwoKeyOverlappedResult;
import org.eclipse.jt.core.invoke.TwoKeyOverlappedResultList;

/**
 * 模块调用器接口
 * 
 * @author Jeff Tang
 * 
 */
public interface ServiceInvoker extends ObjectQuerier, ListQuerier,
        TreeQuerier, LifeHandle {
	/**
	 * 获取调用器的阻力
	 * 
	 * @return 返回0代表本进程调用，0和1之间代表本地调用，1以及1以上代表远程调用
	 */
	public float getResistance();

	// /////////////////////////////////////////
	// /////// 异步请求数据
	// /////////////////////////////////////////
	public <TResult> AsyncResult<TResult> asyncGet(Class<TResult> resultClass);

	public <TResult, TKey> OneKeyOverlappedResult<TResult, TKey> asyncGet(
	        Class<TResult> resultClass, TKey key);

	public <TResult, TKey1, TKey2> TwoKeyOverlappedResult<TResult, TKey1, TKey2> asyncGet(
	        Class<TResult> resultClass, TKey1 key, TKey2 key2);

	public <TResult, TKey1, TKey2, TKey3> ThreeKeyOverlappedResult<TResult, TKey1, TKey2, TKey3> asyncGet(
	        Class<TResult> resultClass, TKey1 key, TKey2 key2, TKey3 key3);

	public <TResult> AsyncResultList<TResult> asyncGetList(
	        Class<TResult> resultClass);

	public <TResult, TKey1> OneKeyOverlappedResultList<TResult, TKey1> asyncGetList(
	        Class<TResult> resultClass, TKey1 key1);

	public <TResult, TKey1, TKey2> TwoKeyOverlappedResultList<TResult, TKey1, TKey2> asyncGetList(
	        Class<TResult> resultClass, TKey1 key1, TKey2 key2);

	public <TResult, TKey1, TKey2, TKey3> ThreeKeyOverlappedResultList<TResult, TKey1, TKey2, TKey3> asyncGetList(
	        Class<TResult> resultClass, TKey1 key1, TKey2 key2, TKey3 key3);

	// /////////////////////////////////////////
	// /////// 处理任务
	// /////////////////////////////////////////
	public <TMethod extends Enum<TMethod>> void handle(Task<TMethod> task,
	        TMethod method) throws DeadLockException;

	public void handle(SimpleTask task) throws DeadLockException;

	public <TTask extends Task<TMethod>, TMethod extends Enum<TMethod>> AsyncTask<TTask, TMethod> asyncHandle(
	        TTask task, TMethod method);

	public <TSimpleTask extends SimpleTask> AsyncTask<TSimpleTask, None> asyncHandle(
	        TSimpleTask task);

	public <TTask extends Task<TMethod>, TMethod extends Enum<TMethod>> AsyncTask<TTask, TMethod> asyncHandle(
	        TTask task, TMethod method, AsyncInfo info);

	public <TSimpleTask extends SimpleTask> AsyncTask<TSimpleTask, None> asyncHandle(
	        TSimpleTask task, AsyncInfo info);

	// /////////////////////////////////////////
	// /////// 触发事件
	// /////////////////////////////////////////
	/**
	 * 触发异步事件，该方法一经调用马上返回，每个事件处理都拥有独立的事务。
	 * 
	 * @param event
	 *            事件对象
	 */
	public AsyncHandle occur(Event event);

	/**
	 * 触发同步事件，该方法等待该事件的全部处理器执行完毕后返回，事件处理与调用者在同一事务中工作。
	 * 
	 * @param event事件对象
	 * @return 返回false表示没有事件响应器
	 */
	public boolean dispatch(Event event);

	// /////////////////////////////////////////
	// /////// 等待异步处理
	// /////////////////////////////////////////
	/**
	 * 等待异步处理的全部结束
	 */
	public void waitFor(AsyncHandle one, AsyncHandle... others)
	        throws InterruptedException;

	/**
	 * 等待异步处理的全部结束
	 * 
	 * @param timeout
	 *            超时毫秒数，0代表永远不超时
	 */
	public void waitFor(long timeout, AsyncHandle one, AsyncHandle... others)
	        throws InterruptedException;
}
