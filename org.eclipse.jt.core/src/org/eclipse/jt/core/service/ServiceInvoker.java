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
 * ģ��������ӿ�
 * 
 * @author Jeff Tang
 * 
 */
public interface ServiceInvoker extends ObjectQuerier, ListQuerier,
        TreeQuerier, LifeHandle {
	/**
	 * ��ȡ������������
	 * 
	 * @return ����0�������̵��ã�0��1֮������ص��ã�1�Լ�1���ϴ���Զ�̵���
	 */
	public float getResistance();

	// /////////////////////////////////////////
	// /////// �첽��������
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
	// /////// ��������
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
	// /////// �����¼�
	// /////////////////////////////////////////
	/**
	 * �����첽�¼����÷���һ���������Ϸ��أ�ÿ���¼�����ӵ�ж���������
	 * 
	 * @param event
	 *            �¼�����
	 */
	public AsyncHandle occur(Event event);

	/**
	 * ����ͬ���¼����÷����ȴ����¼���ȫ��������ִ����Ϻ󷵻أ��¼��������������ͬһ�����й�����
	 * 
	 * @param event�¼�����
	 * @return ����false��ʾû���¼���Ӧ��
	 */
	public boolean dispatch(Event event);

	// /////////////////////////////////////////
	// /////// �ȴ��첽����
	// /////////////////////////////////////////
	/**
	 * �ȴ��첽�����ȫ������
	 */
	public void waitFor(AsyncHandle one, AsyncHandle... others)
	        throws InterruptedException;

	/**
	 * �ȴ��첽�����ȫ������
	 * 
	 * @param timeout
	 *            ��ʱ��������0������Զ����ʱ
	 */
	public void waitFor(long timeout, AsyncHandle one, AsyncHandle... others)
	        throws InterruptedException;
}
