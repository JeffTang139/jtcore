package org.eclipse.jt.core.invoke;

import java.util.List;

import org.eclipse.jt.core.None;
import org.eclipse.jt.core.def.obja.StructClass;
import org.eclipse.jt.core.impl.Utils;
import org.eclipse.jt.core.misc.SafeItrList;

import sun.reflect.Reflection;


/**
 * ������Ļ���
 * 
 * @author Jeff Tang
 * 
 * @param <TMethod>
 */
@StructClass
public abstract class Task<TMethod extends Enum<TMethod>> {
	/**
	 * ׷��������
	 * 
	 * @param task
	 *            ����
	 * @param method
	 *            �����ִ�з���
	 */
	public final <TSubTaskMethod extends Enum<TSubTaskMethod>> void addSubTask(
	        Task<TSubTaskMethod> task, TSubTaskMethod method) {
		if (task == null || method == null) {
			throw new NullPointerException();
		}
		if (task.parent != null) {
			throw new IllegalArgumentException("�����Ѿ���Ϊ������뵽ĳ������");
		}
		if (this.subTasks == null) {
			this.subTasks = new SafeItrList<Task<?>>();
		}
		task.parent = this;
		task.method = method;
		this.subTasks.add(task);
	}

	/**
	 * ���һ������
	 * 
	 * @return ������ӵ�����
	 */
	public final TaskGroup addNewGroup() {
		TaskGroup group = new TaskGroup();
		this.addSubTask(group, None.NONE);
		return group;
	}

	/**
	 * ��������״̬
	 * 
	 * @return ���������״̬
	 */
	public final TaskState getState() {
		return this.state;
	}

	/**
	 * ��������Ĵ�������������ִ��ʱ���÷���һ����Ϊ�գ�ִ����Ϻ���ᵽԭ��ֵ
	 * 
	 * @return ��������Ĵ�������������ִ��ʱ���÷���һ����Ϊ�գ�ִ����Ϻ���ᵽԭ��ֵ
	 */
	public final TMethod getMethod() {
		return this.method;
	}

	/**
	 * ���캯��
	 */
	public Task() {
		this.state = TaskState.PREPARING;
	}

	/**
	 * �ڲ�������
	 * 
	 * @author Jeff Tang
	 * 
	 */
	public static final class _Accessor {
		public final void setTaskState(Task<?> task, TaskState state) {
			task.state = state;
		}

		public final <TMethod extends Enum<TMethod>, TTask extends Task<TMethod>> TMethod setTaskMethod(
		        TTask task, TMethod method) {
			TMethod old = task.method;
			task.method = method;
			return old;
		}

		public final List<Task<?>> getSubTasks(Task<?> task) {
			return task.subTasks;
		}

		private _Accessor() {
			// Nothing
		}

		public static _Accessor get() {
			if (Reflection.getCallerClass(2) != Utils.class) {
				throw new SecurityException();
			}
			return accessor;
		}
	}

	// //////////////////////////////////////////////////////////////////////////
	// ////////////////////
	// //////////////////�������ڲ�����/////////////////////////////////////////////////
	// ////////////////
	// //////////////////////////////////////////////////////////////////////////
	// ////////////////////
	/**
	 * ������
	 */
	TMethod method;
	/**
	 * ��������
	 */
	private Task<?> parent;
	/**
	 * ����״̬
	 */
	private TaskState state;
	/**
	 * ������
	 */
	private SafeItrList<Task<?>> subTasks;
	final static _Accessor accessor = new _Accessor();
}