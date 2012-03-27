package org.eclipse.jt.core.impl;

import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.eclipse.jt.core.Context;
import org.eclipse.jt.core.Login;
import org.eclipse.jt.core.None;
import org.eclipse.jt.core.TreeNode;
import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.invoke.Event;
import org.eclipse.jt.core.invoke.Task;
import org.eclipse.jt.core.misc.ExceptionCatcher;
import org.eclipse.jt.core.misc.TypeArgFinder;
import org.eclipse.jt.core.model.ModelService;
import org.eclipse.jt.core.resource.ResourceService;
import org.eclipse.jt.core.service.NativeDeclaratorResolver;
import org.eclipse.jt.core.service.Publish;
import org.eclipse.jt.core.service.Service;
import org.eclipse.jt.core.service.UsingDeclarator;
import org.eclipse.jt.core.spi.monitor.PerformanceBoolValueCollector;
import org.eclipse.jt.core.spi.monitor.PerformanceDoubleValueCollector;
import org.eclipse.jt.core.spi.monitor.PerformanceLongValueCollector;
import org.eclipse.jt.core.spi.monitor.PerformanceMonitorStartResult;
import org.eclipse.jt.core.spi.monitor.PerformanceObjValueCollector;
import org.eclipse.jt.core.spi.monitor.PerformanceSequenceValueCollector;
import org.eclipse.jt.core.spi.monitor.PerformanceValueCollector;
import org.eclipse.jt.core.spi.monitor.PerformanceIndexDefine.CommandDefine;
import org.eclipse.jt.core.spi.publish.Bundleable;
import org.eclipse.jt.core.testing.CaseTesterInstance;
import org.eclipse.jt.core.testing.TestContext;
import org.eclipse.jt.core.type.DataType;
import org.eclipse.jt.core.type.ObjectDataType;
import org.eclipse.jt.core.type.TypeFactory;


/**
 * 空间和模块的基类
 * 
 * @author Jeff Tang
 * 
 * @param <TParent>
 */
public abstract class ServiceBase<TContext extends Context> extends SpaceNode
		implements Bundleable {

	/**
	 * 所属Bundle;
	 */
	BundleStub bundle;
	final String title;

	public String getTitle() {
		return this.title;
	}

	protected ServiceBase(String title) {
		if (title == null || title.length() == 0) {
			throw new NullArgumentException("title");
		}
		this.title = title;
	}

	public final BundleStub getBundle() {
		return this.bundle;
	}

	/**
	 * 初始化方法，子类重载该方法初始化模块
	 * 
	 * @param context
	 */
	protected void init(Context context) throws Throwable {
	}

	/**
	 * 释放方法，子类重载该方法释放模块
	 * 
	 * @param context
	 *            上下文对象
	 * @throws Throwable
	 */
	protected void dispose(Context context) throws Throwable {
	}

	/**
	 * 解决本地声明器
	 * 
	 * @param context
	 *            上下文
	 * @throws Throwable
	 */
	protected void resolveNativeDeclarator(Context context,
			NativeDeclaratorResolver resolver) throws Throwable {
	}

	/**
	 * 定义初始化时需要用到的调用
	 * 
	 * @param using
	 *            调用依赖声明器
	 */
	protected void initUsing(UsingDeclarator using) {

	}

	/**
	 * 事件监听器
	 * 
	 * @author Jeff Tang
	 * 
	 * @param <TEvent>
	 *            监听的事件
	 */
	protected abstract class EventListener<TEvent extends Event> extends
			ServiceInvokeeBase<TEvent, TContext, None, None, None> {
		/**
		 * 
		 */
		protected final float priority;

		/**
		 * 事件发生时调用
		 * 
		 * @param event
		 *            事件对象
		 */
		@Override
		protected abstract void occur(TContext context, TEvent event)
				throws Throwable;

		// ////////////////////////////
		final Class<?> eventClass;

		protected EventListener(float priority) {
			this.priority = priority;
			this.eventClass = TypeArgFinder.get(this.getClass(),
					EventListener.class, 0);
		}

		@Override
		final boolean match(Class<?> key1Class1, Class<?> key2Class2,
				Class<?> key3Class3, int mask) {
			return mask == MASK_EVENT;
		}

		@Override
		final ServiceBase<?> getService() {
			return ServiceBase.this;
		}

		/**
		 * 获取标底类型
		 * 
		 * @param typeArgFinder
		 *            模块信息
		 * @return 返回空代表忽略注册
		 */
		@Override
		final Class<?> getTargetClass() {
			return this.eventClass;
		}
	}

	/**
	 * 监控指标提供器
	 * 
	 * @author Jeff Tang
	 * 
	 */
	protected abstract class PerformanceProvider<TPerformanceValueContainer extends PerformanceValueCollector<?>> {
		/**
		 * 监控器启动方法，含Context版本。<br>
		 * 1. 返回false表示需要系统保持valueCollector，并定期调用update方法。
		 * 该结果适用于需要保持状态，或启动停止监控比较消耗资源的监控指标。<br>
		 * 2. 返回true表示不需要系统保持valueCollector和调用后续方法。
		 * 该结果适用于提高非持续性监控指标和不需要保持监控状态的监控指标的监控效率
		 * 
		 * @param context
		 *            上下文，如果时会话级监控指标则上下文对应的Login则是被监控会话
		 * @param valueCollector
		 *            监控指标收集器，收集器上可以设置“附件”可以用以保持一些被监控对象
		 * @return 返回false表示需要系统保持valueCollector，并定期调用update方法
		 */
		protected boolean startMonitor(TContext context,
				TPerformanceValueContainer valueCollector) throws Throwable {
			this.update(context, valueCollector);
			return false;
		}

		/**
		 * 监控器启动方法，参见{@code PerformanceMonitorStartResult}的说明。
		 * 
		 * @param valueCollector
		 *            监控指标收集器，收集器上可以设置“附件”可以用以保持一些被监控对象
		 * @return 参见{@code PerformanceMonitorStartResult}的说明
		 */
		protected PerformanceMonitorStartResult startMonitor(Login login,
				TPerformanceValueContainer valueCollector) throws Throwable {
			this.update(login, valueCollector);
			return PerformanceMonitorStartResult.KEEP;
		}

		/**
		 * 低效地提供监控数据，因为需要创建Context
		 * 
		 * @param context
		 *            上下文，如果时会话级监控指标则上下文对应的Login则是被监控会话
		 * @param valueCollector
		 *            监控指标收集器
		 */
		protected void update(TContext context,
				TPerformanceValueContainer valueCollector) throws Throwable {
		}

		/**
		 * 不带Context的更新器，适用于不依赖Context的高效监控指标
		 * 
		 * @param valueCollector
		 *            监控指标收集器
		 * @return 返回false表示调用不成功，需要调用带Context 版本的update
		 */
		protected boolean update(Login login,
				TPerformanceValueContainer valueCollector) throws Throwable {
			return true;
		}

		/**
		 * 执行命令
		 * 
		 * @param context
		 *            上下文
		 * @param command
		 *            命令定义
		 * @param testOrExecute
		 *            指示是测试命令的有效性还是真实的执行命令
		 * @return <li>返回返回测试有效或无效(testOrExecute==true)<li>
		 *         或执行成功或失败(testOrExecute==false )
		 */
		protected boolean doCommand(TContext context, CommandDefine command,
				boolean testOrExecute) throws Throwable {
			return false;
		}

		/**
		 * 结束性能监控
		 * 
		 * @param context
		 *            上下文，如果时会话级监控指标则上下文对应的Login则是被监控会话
		 * @param valueCollector
		 *            监控指标收集器
		 */
		protected void stopMonitor(TContext context,
				TPerformanceValueContainer valueCollector) throws Throwable {
		}

		/**
		 * 不带Context的结束器，适用于不依赖Context的高效监控指标
		 * 
		 * @param valueCollector
		 *            监控指标收集器
		 * @return 返回false表示调用不成功，需要调用带Context 版本的stopMonitor
		 */
		protected boolean stopMonitor(Login login,
				TPerformanceValueContainer valueCollector) throws Throwable {
			return true;
		}

		final ServiceBase<?> getService() {
			return ServiceBase.this;
		}

		protected final PerformanceIndexDefineImpl declare;

		@SuppressWarnings("unchecked")
		final PerformanceValueCollectorImpl<?> newCollector() {
			if (this.declare.isSequence()) {
				return new PerformanceSequenceValueCollectorImpl(this);
			} else if (this.declare.dataType == TypeFactory.LONG) {
				return new PerformanceLongValueCollectorImpl(this);
			} else if (this.declare.dataType == TypeFactory.DOUBLE) {
				return new PerformanceDoubleValueCollectorImpl(this);
			} else if (this.declare.dataType == TypeFactory.BOOLEAN) {
				return new PerformanceBoolValueCollectorImpl(this);
			} else if (this.declare.dataType instanceof ObjectDataType) {
				return new PerformanceObjValueCollectorImpl(this);
			} else {
				throw new UnsupportedOperationException("不支持的监控类型："
						+ this.declare.dataType);
			}
		}

		protected PerformanceProvider(String name) {
			DataType dataType;
			boolean isSequence = false;
			getDataType: {
				final ParameterizedType pt = TypeArgFinder
						.findParameterizedType(this.getClass(),
								PerformanceProvider.class, 0);
				if (pt != null) {
					final Type t = pt.getRawType();
					if (t == PerformanceLongValueCollector.class) {
						dataType = TypeFactory.LONG;
						break getDataType;
					} else if (t == PerformanceDoubleValueCollector.class) {
						dataType = TypeFactory.DOUBLE;
						break getDataType;
					} else if (t == PerformanceBoolValueCollector.class) {
						dataType = TypeFactory.BOOLEAN;
						break getDataType;
					} else {
						elseCollector: {

							if (t == PerformanceSequenceValueCollector.class) {
								isSequence = true;
							} else if (t != PerformanceObjValueCollector.class) {
								break elseCollector;
							}

							final Class<?> objClass = TypeArgFinder
									.tryToClass(pt.getActualTypeArguments()[1]);
							if (objClass != null) {
								dataType = DataTypeBase
										.dataTypeOfJavaClass(objClass);
								break getDataType;
							}
						}
					}
				}
				throw new UnsupportedOperationException("性能监控指标提供器指标收集器类型定义有误");
			}
			this.declare = new PerformanceIndexDefineImpl(name, dataType,
					isSequence);
		}
	}

	// //////////////////////////////////////////////////
	// //////// 任务处理
	// ////////////////////////////////////////////////
	static final int MAX_TASK_METHODS = 25;
	// 表示处理多于一个的方法
	final static int METHODS_MASK = 1 << MAX_TASK_METHODS;
	static final int TASK_METHODS_MASK = -1 >> 32 - MAX_TASK_METHODS;

	private final static int getMethodMask(Enum<?> method) {
		if (method != null) {
			int ordinal = method.ordinal();
			if (ordinal > MAX_TASK_METHODS) {
				throw new IllegalArgumentException("任务不支持超过" + MAX_TASK_METHODS
						+ "种处理方法");
			}
			return 1 << ordinal;
		}
		return 0;
	}

	static final int getMethodsMask(Enum<?> first, Enum<?>[] others) {
		int c = 0;
		int mm = getMethodMask(first);
		if (mm != 0) {
			c++;
		}
		if (others != null) {
			for (int i = 0; i < others.length; i++) {
				int m = getMethodMask(others[i]);
				if (m != 0) {
					mm |= m;
					c++;
				}
			}
		}
		if (c == 0) {
			throw new IllegalArgumentException("没有有效的任务处理方法");
		}
		if (c > 1) {
			mm |= METHODS_MASK;
		}
		return mm;
	}

	/**
	 * 任务处理基类。<br>
	 * 开发人员在Service的子类中实现任务处理类时需要实现一个无参数的构造函数，否则系统无法正确使用该任务处理类
	 * 建议将公用代码部分写在Service中，而任务处理类只负责实现任务具体的方法处理<br>
	 * 强烈建议：除非十分必要否则最好不要为一个任务的所有处理方法只定义一个处理类。<br>
	 * 
	 * @author Jeff Tang
	 * 
	 * @param <TTask>
	 *            任务类型
	 * @param <TMethod>
	 *            任务的方法类型
	 */
	protected abstract class TaskMethodHandler<TTask extends Task<TMethod>, TMethod extends Enum<TMethod>>
			extends ServiceInvokeeBase<TTask, TContext, None, None, None> {
		/**
		 * 准备任务，给任务一个子任务处理前的机会。 <br>
		 * 每次调用Handle前会调用该方法，处理类可以在该方法中添加子任务。 <br>
		 * 添加完的子任务会先于父任务的处理进行准备和处理，子任务的准备和处理也遵循相同的机制<br>
		 * 在prepare 或者handle中访问所述Service可以直接访问，或者使用XXXService.this.YYY
		 * 
		 * @param context
		 *            上下文
		 * @param task
		 *            任务
		 * @param method
		 *            任务的处理方法
		 */
		@Override
		protected void prepare(TContext context, TTask task) throws Throwable {
			// 默认不处理任何事
		}

		/**
		 * 处理任务，当某任务的子任务处理完后，就进入该方法。 处理器需要实现该方法，以完成对本任务的处理。 在prepare
		 * 或者handle中访问所述Service可以直接访问，或者使用XXXService.this.YYY
		 * 
		 * @param context
		 *            上下文
		 * @param task
		 *            任务
		 * @param method
		 *            任务的处理方法
		 */
		@Override
		protected abstract void handle(TContext context, TTask task)
				throws Throwable;

		/**
		 * 构造函数<br>
		 * 
		 * @param first
		 *            第一个需要处理的方法
		 * @param otherMethods
		 *            余下需要处理的方法
		 */
		protected TaskMethodHandler(TMethod first, TMethod[] otherMethods) {
			this.taskClass = TypeArgFinder.get(this.getClass(),
					TaskMethodHandler.class, 0);
			this.handleableMethodsMask = getMethodsMask(first, otherMethods);
		}

		// ///////////////////////////////////////////////////
		final int handleableMethodsMask;
		final Class<?> taskClass;

		@Override
		final boolean match(Class<?> key1Class1, Class<?> key2Class2,
				Class<?> key3Class3, int mask) {
			return (mask & MASKS_MASK) == MASK_TASK
					&& (mask & this.handleableMethodsMask) != 0;
		}

		/**
		 * 获取标底类型
		 * 
		 * @param typeArgFinder
		 *            模块信息
		 * @return 返回空代表忽略注册
		 */
		@Override
		final Class<?> getTargetClass() {
			return this.taskClass;
		}

		@Override
		final ServiceBase<?> getService() {
			return ServiceBase.this;
		}
	}

	protected abstract class BatchTaskMethodHandler<TTask extends Task<TMethod>, TMethod extends Enum<TMethod>, TBatchState>
			extends ServiceInvokeeBase<TTask, TContext, None, None, None> {
		/**
		 * 批处理开始
		 * 
		 * @param context
		 *            上下文
		 * @return 返回批处理状态对象
		 */
		protected abstract TBatchState beginBatch(TContext context)
				throws Throwable;

		/**
		 * 准备任务，给任务一个子任务处理前的机会。 <br>
		 * 每次调用Handle前会调用该方法，处理类可以在该方法中添加子任务。 <br>
		 * 添加完的子任务会先于父任务的处理进行准备和处理，子任务的准备和处理也遵循相同的机制<br>
		 * 在prepare 或者handle中访问所述Service可以直接访问，或者使用XXXService.this.YYY
		 * 
		 * @param context
		 *            上下文
		 * @param task
		 *            任务
		 * @param method
		 *            任务的处理方法
		 */
		@Override
		protected void prepare(TContext context, TTask task) throws Throwable {
			// 默认不处理任何事
		}

		/**
		 * 处理批处理其中之一
		 * 
		 * @param context
		 *            上下文
		 * @param task
		 *            待处理的任务
		 * @param batchState
		 *            批处理状态对象
		 */
		protected abstract boolean handle(TContext context, TTask task,
				TBatchState batchState) throws Throwable;

		/**
		 * 批处理结束，回收相关资源
		 * 
		 * @param context
		 * @param batchState
		 *            批处理状态对象
		 */
		protected void endBatch(TContext context, TBatchState batchState) {
		}

		/**
		 * 构造函数<br>
		 * 
		 * @param first
		 *            第一个需要处理的方法
		 * @param otherMethods
		 *            余下需要处理的方法
		 */
		protected BatchTaskMethodHandler(TMethod first, TMethod[] otherMethods) {
			this.taskClass = TypeArgFinder.get(this.getClass(),
					TaskMethodHandler.class, 0);
			this.handleableMethodsMask = getMethodsMask(first, otherMethods);
		}

		// ///////////////////////////////////////////////////
		final int handleableMethodsMask;
		final Class<?> taskClass;

		@Override
		final boolean match(Class<?> key1Class1, Class<?> key2Class2,
				Class<?> key3Class3, int mask) {
			return (mask & MASKS_MASK) == MASK_TASK
					&& (mask & this.handleableMethodsMask) != 0;
		}

		/**
		 * 获取标底类型
		 * 
		 * @param typeArgFinder
		 *            模块信息
		 * @return 返回空代表忽略注册
		 */
		@Override
		final Class<?> getTargetClass() {
			return this.taskClass;
		}

		@Override
		final ServiceBase<?> getService() {
			return ServiceBase.this;
		}
	}

	/**
	 * 结果提供器
	 * 
	 * @author Jeff Tang
	 * 
	 * @param <TResult>
	 *            结果类型
	 */
	protected abstract class ResultProvider<TResult> extends
			ServiceInvokeeBase<TResult, TContext, None, None, None> {
		/**
		 * 根据条件单一的返回数据
		 * 
		 * @param context
		 *            上下文
		 * @return 返回结果
		 * @throws Throwable
		 *             错误异常
		 */
		@Override
		protected abstract TResult provide(TContext context) throws Throwable;

		// ////////////////////////////
		final Class<?> resultClass;

		protected ResultProvider() {
			this.resultClass = TypeArgFinder.get(this.getClass(),
					ResultProvider.class, 0);
		}

		@Override
		final boolean match(Class<?> key1Class, Class<?> key2Class,
				Class<?> key3Class, int mask) {
			return mask == MASK_RESULT && key1Class == null;
		}

		/**
		 * 获取标底类型
		 * 
		 * @param typeArgFinder
		 *            模块信息
		 * @return 返回空代表忽略注册
		 */
		@Override
		final Class<?> getTargetClass() {
			return this.resultClass;
		}

		@Override
		final ServiceBase<?> getService() {
			return ServiceBase.this;
		}
	}

	/**
	 * 结果提供器
	 * 
	 * @author Jeff Tang
	 * 
	 * @param <TResult>
	 *            结果类型
	 * @param <TKey>
	 *            查询凭据
	 */
	protected abstract class OneKeyResultProvider<TResult, TKey> extends
			ServiceInvokeeBase<TResult, TContext, TKey, None, None> {
		/**
		 * 根据条件单一的返回数据
		 * 
		 * @param context
		 *            上下文
		 * @param key
		 *            查询凭据
		 * @return 返回结果
		 * @throws Throwable
		 *             错误异常
		 */
		@Override
		protected abstract TResult provide(TContext context, TKey key)
				throws Throwable;

		// ////////////////////////////
		final Class<?> resultClass;
		final Class<?> key1Class;

		protected OneKeyResultProvider() {
			Class<?>[] types = TypeArgFinder.get(this.getClass(),
					OneKeyResultProvider.class);
			this.resultClass = types[0];
			this.key1Class = types[1];
		}

		@Override
		final boolean match(Class<?> key1Class, Class<?> key2Class,
				Class<?> key3Class, int mask) {
			return mask == MASK_RESULT && this.key1Class == key1Class
					&& key2Class == null;
		}

		/**
		 * 获取标底类型
		 * 
		 * @param typeArgFinder
		 *            模块信息
		 * @return 返回空代表忽略注册
		 */
		@Override
		final Class<?> getTargetClass() {
			return this.resultClass;
		}

		@Override
		final ServiceBase<?> getService() {
			return ServiceBase.this;
		}

	}

	/**
	 * 结果提供器
	 * 
	 * @author Jeff Tang
	 * 
	 * @param <TResult>
	 *            结果类型
	 * @param <TKey1>
	 *            查询凭据1
	 * @param <TKey2>
	 *            查询凭据2
	 */
	protected abstract class TwoKeyResultProvider<TResult, TKey1, TKey2>
			extends ServiceInvokeeBase<TResult, TContext, TKey1, TKey2, None> {
		/**
		 * 根据条件单一的返回数据
		 * 
		 * @param context
		 *            上下文
		 * @param key1
		 *            查询凭据1
		 * @param key2
		 *            查询凭据2
		 * @return 返回结果
		 * @throws Throwable
		 *             错误异常
		 */
		@Override
		protected abstract TResult provide(TContext context, TKey1 key1,
				TKey2 key2) throws Throwable;

		// ////////////////////////////
		final Class<?> resultClass;
		final Class<?> key1Class;
		final Class<?> key2Class;

		protected TwoKeyResultProvider() {
			Class<?>[] types = TypeArgFinder.get(this.getClass(),
					TwoKeyResultProvider.class);
			this.resultClass = types[0];
			this.key1Class = types[1];
			this.key2Class = types[2];
		}

		@Override
		final boolean match(Class<?> key1Class, Class<?> key2Class,
				Class<?> key3Class, int mask) {
			return mask == MASK_RESULT && this.key1Class == key1Class
					&& this.key2Class == key2Class && key3Class == null;
		}

		/**
		 * 获取标底类型
		 * 
		 * @param typeArgFinder
		 *            模块信息
		 * @return 返回空代表忽略注册
		 */
		@Override
		final Class<?> getTargetClass() {
			return this.resultClass;
		}

		@Override
		final ServiceBase<?> getService() {
			return ServiceBase.this;
		}

	}

	/**
	 * 结果提供器
	 * 
	 * @author Jeff Tang
	 * 
	 * @param <TResult>
	 *            结果类型
	 * @param <TKey1>
	 *            查询凭据1
	 * @param <TKey2>
	 *            查询凭据2
	 */
	protected abstract class ThreeKeyResultProvider<TResult, TKey1, TKey2, TKey3>
			extends ServiceInvokeeBase<TResult, TContext, TKey1, TKey2, TKey3> {
		/**
		 * 根据条件单一的返回数据
		 * 
		 * @param context
		 *            上下文
		 * @param key1
		 *            查询凭据1
		 * @param key2
		 *            查询凭据2
		 * @param key3
		 *            查询凭据3
		 * @return 返回结果
		 * @throws Throwable
		 *             错误异常
		 */
		@Override
		protected abstract TResult provide(TContext context, TKey1 key1,
				TKey2 key2, TKey3 key3) throws Throwable;

		// ////////////////////////////
		final Class<?> resultClass;
		final Class<?> key1Class;
		final Class<?> key2Class;
		final Class<?> key3Class;

		protected ThreeKeyResultProvider() {
			Class<?>[] types = TypeArgFinder.get(this.getClass(),
					ThreeKeyResultProvider.class);
			this.resultClass = types[0];
			this.key1Class = types[1];
			this.key2Class = types[2];
			this.key3Class = types[3];

		}

		@Override
		final boolean match(Class<?> key1Class, Class<?> key2Class,
				Class<?> key3Class, int mask) {
			return mask == MASK_RESULT && this.key1Class == key1Class
					&& this.key2Class == key2Class
					&& this.key3Class == key3Class;
		}

		/**
		 * 获取标底类型
		 * 
		 * @param typeArgFinder
		 *            模块信息
		 * @return 返回空代表忽略注册
		 */
		@Override
		final Class<?> getTargetClass() {
			return this.resultClass;
		}

		@Override
		final ServiceBase<?> getService() {
			return ServiceBase.this;
		}

	}

	/**
	 * 结果提供器，用于根据某条件返回一个结果集
	 * 
	 * @author Jeff Tang
	 * 
	 * @param <TResult>
	 *            结果类型
	 */
	protected abstract class ResultListProvider<TResult> extends
			ServiceInvokeeBase<TResult, TContext, None, None, None> {
		/**
		 * 根据某个条件返回一个结果集
		 * 
		 * @param context
		 *            上下文
		 * @param resultList
		 *            结果集
		 * @throws Throwable
		 *             任务错误异常
		 */
		@Override
		protected abstract void provide(TContext context,
				List<TResult> resultList) throws Throwable;

		// ////////////////////////////
		final Class<?> resultClass;

		protected ResultListProvider() {
			this.resultClass = TypeArgFinder.get(this.getClass(),
					ResultListProvider.class, 0);

		}

		@Override
		final boolean match(Class<?> key1Class, Class<?> key2Class,
				Class<?> key3Class, int mask) {
			return mask == MASK_LIST && key1Class == null;
		}

		/**
		 * 获取标底类型
		 * 
		 * @param typeArgFinder
		 *            模块信息
		 * @return 返回空代表忽略注册
		 */
		@Override
		final Class<?> getTargetClass() {
			return this.resultClass;
		}

		@Override
		final ServiceBase<?> getService() {
			return ServiceBase.this;
		}

	}

	/**
	 * 结果提供器，用于根据某条件返回一个结果集
	 * 
	 * @author Jeff Tang
	 * 
	 * @param <TResult>
	 *            结果类型
	 * @param <TKey>
	 *            查询凭据
	 */
	protected abstract class OneKeyResultListProvider<TResult, TKey> extends
			ServiceInvokeeBase<TResult, TContext, TKey, None, None> {
		/**
		 * 根据某个条件返回一个结果集
		 * 
		 * @param context
		 *            上下文
		 * @param key
		 *            查询凭据
		 * @param resultList
		 *            结果集
		 * @throws Throwable
		 *             任务错误异常
		 */
		@Override
		protected abstract void provide(TContext context, TKey key,
				List<TResult> resultList) throws Throwable;

		// ////////////////////////////
		final Class<?> resultClass;
		final Class<?> key1Class;

		protected OneKeyResultListProvider() {
			Class<?>[] types = TypeArgFinder.get(this.getClass(),
					OneKeyResultListProvider.class);
			this.resultClass = types[0];
			this.key1Class = types[1];
		}

		@Override
		final boolean match(Class<?> key1Class, Class<?> key2Class,
				Class<?> key3Class, int mask) {
			return mask == MASK_LIST && this.key1Class == key1Class
					&& key2Class == null;
		}

		/**
		 * 获取标底类型
		 * 
		 * @param typeArgFinder
		 *            模块信息
		 * @return 返回空代表忽略注册
		 */
		@Override
		final Class<?> getTargetClass() {
			return this.resultClass;
		}

		@Override
		final ServiceBase<?> getService() {
			return ServiceBase.this;
		}
	}

	/**
	 * 结果提供器，用于根据某条件返回一个结果集
	 * 
	 * @author Jeff Tang
	 * 
	 * @param <TResult>
	 *            结果类型
	 * @param <TKey>
	 *            查询凭据
	 */
	protected abstract class TwoKeyResultListProvider<TResult, TKey1, TKey2>
			extends ServiceInvokeeBase<TResult, TContext, TKey1, TKey2, None> {
		/**
		 * 根据某个条件返回一个结果集
		 * 
		 * @param context
		 *            上下文
		 * @param key
		 *            查询凭据
		 * @param resultList
		 *            结果集
		 * @throws Throwable
		 *             任务错误异常
		 */
		@Override
		protected abstract void provide(TContext context, TKey1 key1,
				TKey2 key2, List<TResult> resultList) throws Throwable;

		// ////////////////////////////
		final Class<?> resultClass;
		final Class<?> key1Class;
		final Class<?> key2Class;

		protected TwoKeyResultListProvider() {
			Class<?>[] types = TypeArgFinder.get(this.getClass(),
					TwoKeyResultListProvider.class);
			this.resultClass = types[0];
			this.key1Class = types[1];
			this.key2Class = types[2];
		}

		@Override
		final boolean match(Class<?> key1Class, Class<?> key2Class,
				Class<?> key3Class, int mask) {
			return mask == MASK_LIST && this.key1Class == key1Class
					&& this.key2Class == key2Class && key3Class == null;
		}

		/**
		 * 获取标底类型
		 * 
		 * @param typeArgFinder
		 *            模块信息
		 * @return 返回空代表忽略注册
		 */
		@Override
		final Class<?> getTargetClass() {
			return this.resultClass;
		}

		@Override
		final ServiceBase<?> getService() {
			return ServiceBase.this;
		}

	}

	/**
	 * 结果提供器，用于根据某条件返回一个结果集
	 * 
	 * @author Jeff Tang
	 * 
	 * @param <TResult>
	 *            结果类型
	 * @param <TKey>
	 *            查询凭据
	 */
	protected abstract class ThreeKeyResultListProvider<TResult, TKey1, TKey2, TKey3>
			extends ServiceInvokeeBase<TResult, TContext, TKey1, TKey2, TKey3> {
		/**
		 * 根据某个条件返回一个结果集
		 * 
		 * @param context
		 *            上下文
		 * @param key
		 *            查询凭据
		 * @param resultList
		 *            结果集
		 * @throws Throwable
		 *             任务错误异常
		 */
		@Override
		protected abstract void provide(TContext context, TKey1 key1,
				TKey2 key2, TKey3 key3, List<TResult> resultList)
				throws Throwable;

		// ////////////////////////////
		final Class<?> resultClass;
		final Class<?> key1Class;
		final Class<?> key2Class;
		final Class<?> key3Class;

		protected ThreeKeyResultListProvider() {
			Class<?>[] types = TypeArgFinder.get(this.getClass(),
					ThreeKeyResultListProvider.class);
			this.resultClass = types[0];
			this.key1Class = types[1];
			this.key2Class = types[2];
			this.key3Class = types[3];
		}

		@Override
		final boolean match(Class<?> key1Class, Class<?> key2Class,
				Class<?> key3Class, int mask) {
			return mask == MASK_LIST && this.key1Class == key1Class
					&& this.key2Class == key2Class
					&& this.key3Class == key3Class;
		}

		/**
		 * 获取标底类型
		 * 
		 * @param typeArgFinder
		 *            模块信息
		 * @return 返回空代表忽略注册
		 */
		@Override
		final Class<?> getTargetClass() {
			return this.resultClass;
		}

		@Override
		final ServiceBase<?> getService() {
			return ServiceBase.this;
		}

	}

	/**
	 * 结果提供器，用于根据某条件返回一个结果集
	 * 
	 * @author Jeff Tang
	 * 
	 * @param <TResult>
	 *            结果类型
	 */
	protected abstract class TreeNodeProvider<TResult> extends
			ServiceInvokeeBase<TResult, TContext, None, None, None> {
		/**
		 * 根据某个条件返回一个结果集
		 * 
		 * @param context
		 *            上下文
		 * @param resultList
		 *            结果集
		 * @throws Throwable
		 *             任务错误异常
		 * @return resultTreeNode在整个树结构中的绝对级次
		 */
		@Override
		protected abstract int provide(TContext context,
				TreeNode<TResult> resultTreeNode) throws Throwable;

		// ////////////////////////////
		final Class<?> resultClass;

		protected TreeNodeProvider() {
			this.resultClass = TypeArgFinder.get(this.getClass(),
					TreeNodeProvider.class, 0);
		}

		@Override
		final boolean match(Class<?> key1Class, Class<?> key2Class,
				Class<?> key3Class, int mask) {
			return mask == MASK_TREE && key1Class == null;
		}

		/**
		 * 获取标底类型
		 * 
		 * @param typeArgFinder
		 *            模块信息
		 * @return 返回空代表忽略注册
		 */
		@Override
		final Class<?> getTargetClass() {
			return this.resultClass;
		}

		@Override
		final ServiceBase<?> getService() {
			return ServiceBase.this;
		}

	}

	/**
	 * 结果提供器，用于根据某条件返回一个结果集
	 * 
	 * @author Jeff Tang
	 * 
	 * @param <TResult>
	 *            结果类型
	 * @param <TKey>
	 *            查询凭据
	 */
	protected abstract class OneKeyTreeNodeProvider<TResult, TKey> extends
			ServiceInvokeeBase<TResult, TContext, TKey, None, None> {
		/**
		 * 根据某个条件返回一个结果集
		 * 
		 * @param context
		 *            上下文
		 * @param key
		 *            查询凭据
		 * @param resultList
		 *            结果集
		 * @throws Throwable
		 *             任务错误异常
		 * @return resultTreeNode在整个树结构中的绝对级次
		 */
		@Override
		protected abstract int provide(TContext context, TKey key,
				TreeNode<TResult> resultTreeNode) throws Throwable;

		// ////////////////////////////
		final Class<?> resultClass;
		final Class<?> key1Class;

		protected OneKeyTreeNodeProvider() {
			Class<?>[] types = TypeArgFinder.get(this.getClass(),
					OneKeyTreeNodeProvider.class);
			this.resultClass = types[0];
			this.key1Class = types[1];

		}

		@Override
		final boolean match(Class<?> key1Class, Class<?> key2Class,
				Class<?> key3Class, int mask) {
			return mask == MASK_TREE && this.key1Class == key1Class
					&& key2Class == null;
		}

		/**
		 * 获取标底类型
		 * 
		 * @param typeArgFinder
		 *            模块信息
		 * @return 返回空代表忽略注册
		 */
		@Override
		final Class<?> getTargetClass() {
			return this.resultClass;
		}

		@Override
		final ServiceBase<?> getService() {
			return ServiceBase.this;
		}

	}

	/**
	 * 结果提供器，用于根据某条件返回一个结果集
	 * 
	 * @author Jeff Tang
	 * 
	 * @param <TResult>
	 *            结果类型
	 * @param <TKey>
	 *            查询凭据
	 */
	protected abstract class TwoKeyTreeNodeProvider<TResult, TKey1, TKey2>
			extends ServiceInvokeeBase<TResult, TContext, TKey1, TKey2, None> {
		/**
		 * 根据某个条件返回一个结果集
		 * 
		 * @param context
		 *            上下文
		 * @param key
		 *            查询凭据
		 * @param resultList
		 *            结果集
		 * @throws Throwable
		 *             任务错误异常
		 * @return resultTreeNode在整个树结构中的绝对级次
		 */
		@Override
		protected abstract int provide(TContext context, TKey1 key1,
				TKey2 key2, TreeNode<TResult> resultTreeNode) throws Throwable;

		// ////////////////////////////
		final Class<?> resultClass;
		final Class<?> key1Class;
		final Class<?> key2Class;

		protected TwoKeyTreeNodeProvider() {
			Class<?>[] types = TypeArgFinder.get(this.getClass(),
					TwoKeyTreeNodeProvider.class);
			this.resultClass = types[0];
			this.key1Class = types[1];
			this.key2Class = types[2];
		}

		@Override
		final boolean match(Class<?> key1Class, Class<?> key2Class,
				Class<?> key3Class, int mask) {
			return mask == MASK_TREE && this.key1Class == key1Class
					&& this.key2Class == key2Class && key3Class == null;
		}

		/**
		 * 获取标底类型
		 * 
		 * @param typeArgFinder
		 *            模块信息
		 * @return 返回空代表忽略注册
		 */
		@Override
		final Class<?> getTargetClass() {
			return this.resultClass;
		}

		@Override
		final ServiceBase<?> getService() {
			return ServiceBase.this;
		}

	}

	/**
	 * 结果提供器，用于根据某条件返回一个结果集
	 * 
	 * @author Jeff Tang
	 * 
	 * @param <TResult>
	 *            结果类型
	 * @param <TKey>
	 *            查询凭据
	 */
	protected abstract class ThreeKeyTreeNodeProvider<TResult, TKey1, TKey2, TKey3>
			extends ServiceInvokeeBase<TResult, TContext, TKey1, TKey2, TKey3> {
		/**
		 * 根据某个条件返回一个结果集
		 * 
		 * @param context
		 *            上下文
		 * @param key
		 *            查询凭据
		 * @param resultList
		 *            结果集
		 * @throws Throwable
		 *             任务错误异常
		 * @return resultTreeNode在整个树结构中的绝对级次
		 */
		@Override
		protected abstract int provide(TContext context, TKey1 key1,
				TKey2 key2, TKey3 key3, TreeNode<TResult> resultTreeNode)
				throws Throwable;

		// ////////////////////////////
		final Class<?> resultClass;
		final Class<?> key1Class;
		final Class<?> key2Class;
		final Class<?> key3Class;

		protected ThreeKeyTreeNodeProvider() {
			Class<?>[] types = TypeArgFinder.get(this.getClass(),
					ThreeKeyTreeNodeProvider.class);
			this.resultClass = types[0];
			this.key1Class = types[1];
			this.key2Class = types[2];
			this.key3Class = types[3];

		}

		@Override
		final boolean match(Class<?> key1Class, Class<?> key2Class,
				Class<?> key3Class, int mask) {
			return mask == MASK_TREE && this.key1Class == key1Class
					&& this.key2Class == key2Class
					&& this.key3Class == key3Class;
		}

		/**
		 * 获取标底类型
		 * 
		 * @param typeArgFinder
		 *            模块信息
		 * @return 返回空代表忽略注册
		 */
		@Override
		final Class<?> getTargetClass() {
			return this.resultClass;
		}

		@Override
		final ServiceBase<?> getService() {
			return ServiceBase.this;
		}

	}

	/**
	 * 测试用例执行器
	 * 
	 * @author Jeff Tang
	 * 
	 */
	protected abstract class CaseTester implements CaseTesterInstance {
		/**
		 * 子类重载此方法提供用测试用例的描述
		 */
		public String getDescription() {
			return "";
		}

		/**
		 * 子类重载此方法获得用例的名称
		 */
		public String getName() {
			return this.getClass().getName();
		}

		/**
		 * 获得用例的编码
		 */
		public final String getCode() {
			return this.code;
		}

		/**
		 * 获取数据库连接对象，<br>
		 * 不可以关闭该对象，对于使用该对象创建的其他JDBC对象要及时关闭和释放，<br>
		 * 系统不负责关闭和释放相关对象。
		 * 
		 * @throws SQLException
		 */
		protected Connection getDBConnection(Context context)
				throws SQLException {
			return ContextImpl.toContext(context).getDBAdapter()
					.testGetConnection();
		}

		/**
		 * 测试用例
		 * 
		 * @param context
		 *            上下文
		 * @param testContext
		 *            测试向下文
		 * @param category
		 *            用例类别
		 */
		protected abstract void testCase(TContext context,
				TestContext testContext) throws Throwable;

		public CaseTester(String code) {
			this.code = code;
		}

		// /////////////////////////////////////////
		// ////// 内部方法
		// /////////////////////////////////////////
		public final void test(Context context, TestContext testContext)
				throws Throwable {
			ContextImpl<?, ?, ?> cntxt = ContextImpl.toContext(context);
			cntxt.testCase(testContext, this);
		}

		final String code;

		final ServiceBase<?> getService() {
			return ServiceBase.this;
		}
	}

	/**
	 * 模块的状态
	 */
	protected enum ServiceState {
		/**
		 * 创建状态
		 */
		CREATING,
		/**
		 * 正在注册
		 */
		REGISTERING,
		/**
		 * 注册失败
		 */
		REGISTERERROR,
		/**
		 * 注册完成
		 */
		REGISTERED,
		/**
		 * 初始状态
		 */
		INITIALIZING,
		/**
		 * 初始化出错
		 */
		INITIALIZEERROR,
		/**
		 * 初始化完成
		 */
		INITIALIZED,
		/**
		 * 正在激活
		 */
		ACTIVING,
		/**
		 * 激活时出错
		 */
		ACTIVEERROR,
		/**
		 * 有效状态
		 */
		ACTIVED,
		/**
		 * 开始销毁
		 */
		DISPOSING,
		/**
		 * 销毁完毕
		 */
		DISPOSED,
	}

	/**
	 * 获得模块的状态
	 * 
	 * @return 返回状态
	 */
	protected final ServiceState getState() {
		return this.state;
	}

	/**
	 * 更新上下文中的当前模块信息，资源管理器需要重载，以更新当前资源管理器
	 */
	@Override
	SpaceNode updateContextSpace(ContextImpl<?, ?, ?> context) {
		SpaceNode occorAt = context.occorAt;
		context.occorAt = this.space;
		context.occorAtResourceService = null;
		return occorAt;
	}

	// ////////////////////////////////////////////////////////////////////////
	// ///////调用器注册相关
	// ////////////////////////////////////////////////////////////////////////
	/**
	 * 子类注册用
	 */
	void afterRegInvokees(Publish.Mode servicePublishMode,
			ExceptionCatcher catcher) {
	}

	boolean tryRegDeclaredClasses(Class<?> serviceClass,
			Class<?> declaredClass, Publish.Mode servicePublishMode,
			ExceptionCatcher catcher) {
		if (ServiceInvokeeBase.class.isAssignableFrom(declaredClass)) {
			Publish.Mode publishMode = Publish.Mode
					.getMode(declaredClass, null);
			if (publishMode == null) {
				return false;
			} else if (publishMode == Publish.Mode.DEFAULT) {
				publishMode = servicePublishMode;
			}
			try {
				final ServiceInvokeeBase<?, ?, ?, ?, ?> invokee = (ServiceInvokeeBase<?, ?, ?, ?, ?>) this
						.newObjectInNode(declaredClass, null, null);
				invokee.publishMode = publishMode;
				final Class<?> targetClass = invokee.getTargetClass();
				if (targetClass != null) {
					this.space.regInvokee(targetClass, invokee, catcher);
					return true;
				}
			} catch (Throwable e) {
				catcher.catchException(e, this);
				this.state = ServiceState.REGISTERERROR;
			}
		} else if (PerformanceProvider.class.isAssignableFrom(declaredClass)) {
			try {
				final PerformanceProvider<?> pp = (PerformanceProvider<?>) this
						.newObjectInNode(declaredClass, null, null);
				this.site.performanceIndexManager.regPerformanceProvider(pp);
			} catch (Throwable e) {
				catcher.catchException(e, this);
			}
			return true;
		}
		return false;
	}

	final static Class<?>[] endServiceClasses = { ServiceBase.class,
			ModelService.class, Service.class, ResourceService.class };

	private static final boolean validServiceClass(Class<?> serviceClass) {
		for (Class<?> endServiceClass : endServiceClasses) {
			if (serviceClass == endServiceClass) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 调用该方法注册该模块下的所有调用器
	 */
	final void regInvokees(Publish.Mode servicePublishMode,
			ExceptionCatcher catcher) {
		if (this.state == ServiceState.CREATING) {
			this.state = ServiceState.REGISTERING;
		}
		for (Class<?> serviceClass = this.getClass(); ServiceBase
				.validServiceClass(serviceClass); serviceClass = serviceClass
				.getSuperclass()) {
			Class<?>[] declaredClasses = serviceClass.getDeclaredClasses();
			for (int i = 0; i < declaredClasses.length; i++) {
				Class<?> declaredClass = declaredClasses[i];
				if ((declaredClass.getModifiers() & Modifier.ABSTRACT) != 0) {
					continue;
				}
				this.tryRegDeclaredClasses(serviceClass, declaredClass,
						servicePublishMode, catcher);
			}
		}
		this.afterRegInvokees(servicePublishMode, catcher);
		if (this.state == ServiceState.REGISTERING) {
			this.state = ServiceState.REGISTERED;
		}
	}

	final void allocCaseTesters(List<ServiceBase<?>.CaseTester> list) {
		for (Class<?> serviceClass = this.getClass(); ServiceBase
				.validServiceClass(serviceClass); serviceClass = serviceClass
				.getSuperclass()) {
			Class<?>[] declaredClasses = serviceClass.getDeclaredClasses();
			for (int i = 0; i < declaredClasses.length; i++) {
				Class<?> declaredClass = declaredClasses[i];
				if ((declaredClass.getModifiers() & Modifier.ABSTRACT) != 0) {
					continue;
				}
				if (CaseTester.class.isAssignableFrom(declaredClass)) {
					try {
						CaseTester tester = (CaseTester) this.newObjectInNode(
								declaredClass, null, null);
						list.add(tester);
					} catch (Throwable e) {
					}
				}
			}
		}
	}

	// ///////////////////////////////////////////////////
	// ////初始化相关
	// //////////////////////////////////////////////////
	/**
	 * 模块的状态
	 */
	ServiceState state = ServiceState.CREATING;

	/**
	 * 尝试初始化
	 * 
	 * @param context
	 *            上下文
	 * @throws Throwable
	 *             抛出异常
	 */
	final boolean tryInit(ContextImpl<?, ?, ?> context) throws Throwable {
		if (this.state == ServiceState.REGISTERED) {
			this.state = ServiceState.INITIALIZING;
			try {
				context.initService(this);
				this.state = ServiceState.INITIALIZED;
			} catch (Throwable e) {
				this.state = ServiceState.INITIALIZEERROR;
				throw e;
			}
			return true;
		} else if (this.state == ServiceState.INITIALIZING) {
			throw new IllegalStateException("服务的启动状态错误");
		}
		return false;
	}

	/**
	 * 尝试销毁
	 * 
	 * @param context
	 *            上下文
	 * @throws Throwable
	 *             抛出异常
	 */
	@Override
	void doDispose(ContextImpl<?, ?, ?> context) {
		switch (this.state) {
		case DISPOSING:
		case DISPOSED:
			return;
		}
		this.state = ServiceState.DISPOSING;
		try {
			context.disposeService(this);
		} catch (Throwable e) {
			context.catcher.catchException(e, this);
		} finally {
			this.state = ServiceState.DISPOSED;
		}
	}

	/**
	 * 尝试计算资源服务的键路径信息
	 */
	boolean tryBuildResourceKeyPathInfos(
			List<ResourceServiceBase<?, ?, ?>> servicesCache) {
		return false;
	}

	boolean tryBuildResourceRefAuthInfo(OperationMapImpl<?, ?> mapCache) {
		return false;
	}

	/**
	 * 尝试设置资源服务的父资源
	 */
	boolean trySetOwnerResourceService(
			ResourceServiceBase<?, ?, ?> ownerResourceService) {
		return false;
	}

	@Override
	final DataSourceRef tryGetDataSourceRef() {
		return this.space.tryGetDataSourceRef();
	}
}
