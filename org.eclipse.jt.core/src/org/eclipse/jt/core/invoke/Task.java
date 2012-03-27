package org.eclipse.jt.core.invoke;

import java.util.List;

import org.eclipse.jt.core.None;
import org.eclipse.jt.core.def.obja.StructClass;
import org.eclipse.jt.core.impl.Utils;
import org.eclipse.jt.core.misc.SafeItrList;

import sun.reflect.Reflection;


/**
 * 任务类的基类
 * 
 * @author Jeff Tang
 * 
 * @param <TMethod>
 */
@StructClass
public abstract class Task<TMethod extends Enum<TMethod>> {
	/**
	 * 追加子任务
	 * 
	 * @param task
	 *            任务
	 * @param method
	 *            任务的执行方法
	 */
	public final <TSubTaskMethod extends Enum<TSubTaskMethod>> void addSubTask(
	        Task<TSubTaskMethod> task, TSubTaskMethod method) {
		if (task == null || method == null) {
			throw new NullPointerException();
		}
		if (task.parent != null) {
			throw new IllegalArgumentException("任务已经作为子项加入到某任务中");
		}
		if (this.subTasks == null) {
			this.subTasks = new SafeItrList<Task<?>>();
		}
		task.parent = this;
		task.method = method;
		this.subTasks.add(task);
	}

	/**
	 * 添加一个子组
	 * 
	 * @return 返回添加的子组
	 */
	public final TaskGroup addNewGroup() {
		TaskGroup group = new TaskGroup();
		this.addSubTask(group, None.NONE);
		return group;
	}

	/**
	 * 获得任务的状态
	 * 
	 * @return 返回任务的状态
	 */
	public final TaskState getState() {
		return this.state;
	}

	/**
	 * 返回任务的处理方法或，任务在执行时，该方法一定不为空，执行完毕后则会到原来值
	 * 
	 * @return 返回任务的处理方法或，任务在执行时，该方法一定不为空，执行完毕后则会到原来值
	 */
	public final TMethod getMethod() {
		return this.method;
	}

	/**
	 * 构造函数
	 */
	public Task() {
		this.state = TaskState.PREPARING;
	}

	/**
	 * 内部访问器
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
	// //////////////////以下是内部方法/////////////////////////////////////////////////
	// ////////////////
	// //////////////////////////////////////////////////////////////////////////
	// ////////////////////
	/**
	 * 处理方法
	 */
	TMethod method;
	/**
	 * 父极任务
	 */
	private Task<?> parent;
	/**
	 * 任务状态
	 */
	private TaskState state;
	/**
	 * 子任务
	 */
	private SafeItrList<Task<?>> subTasks;
	final static _Accessor accessor = new _Accessor();
}