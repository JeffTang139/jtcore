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
 * �ռ��ģ��Ļ���
 * 
 * @author Jeff Tang
 * 
 * @param <TParent>
 */
public abstract class ServiceBase<TContext extends Context> extends SpaceNode
		implements Bundleable {

	/**
	 * ����Bundle;
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
	 * ��ʼ���������������ظ÷�����ʼ��ģ��
	 * 
	 * @param context
	 */
	protected void init(Context context) throws Throwable {
	}

	/**
	 * �ͷŷ������������ظ÷����ͷ�ģ��
	 * 
	 * @param context
	 *            �����Ķ���
	 * @throws Throwable
	 */
	protected void dispose(Context context) throws Throwable {
	}

	/**
	 * �������������
	 * 
	 * @param context
	 *            ������
	 * @throws Throwable
	 */
	protected void resolveNativeDeclarator(Context context,
			NativeDeclaratorResolver resolver) throws Throwable {
	}

	/**
	 * �����ʼ��ʱ��Ҫ�õ��ĵ���
	 * 
	 * @param using
	 *            ��������������
	 */
	protected void initUsing(UsingDeclarator using) {

	}

	/**
	 * �¼�������
	 * 
	 * @author Jeff Tang
	 * 
	 * @param <TEvent>
	 *            �������¼�
	 */
	protected abstract class EventListener<TEvent extends Event> extends
			ServiceInvokeeBase<TEvent, TContext, None, None, None> {
		/**
		 * 
		 */
		protected final float priority;

		/**
		 * �¼�����ʱ����
		 * 
		 * @param event
		 *            �¼�����
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
		 * ��ȡ�������
		 * 
		 * @param typeArgFinder
		 *            ģ����Ϣ
		 * @return ���ؿմ������ע��
		 */
		@Override
		final Class<?> getTargetClass() {
			return this.eventClass;
		}
	}

	/**
	 * ���ָ���ṩ��
	 * 
	 * @author Jeff Tang
	 * 
	 */
	protected abstract class PerformanceProvider<TPerformanceValueContainer extends PerformanceValueCollector<?>> {
		/**
		 * �����������������Context�汾��<br>
		 * 1. ����false��ʾ��Ҫϵͳ����valueCollector�������ڵ���update������
		 * �ý����������Ҫ����״̬��������ֹͣ��رȽ�������Դ�ļ��ָ�ꡣ<br>
		 * 2. ����true��ʾ����Ҫϵͳ����valueCollector�͵��ú���������
		 * �ý����������߷ǳ����Լ��ָ��Ͳ���Ҫ���ּ��״̬�ļ��ָ��ļ��Ч��
		 * 
		 * @param context
		 *            �����ģ����ʱ�Ự�����ָ���������Ķ�Ӧ��Login���Ǳ���ػỰ
		 * @param valueCollector
		 *            ���ָ���ռ������ռ����Ͽ������á��������������Ա���һЩ����ض���
		 * @return ����false��ʾ��Ҫϵͳ����valueCollector�������ڵ���update����
		 */
		protected boolean startMonitor(TContext context,
				TPerformanceValueContainer valueCollector) throws Throwable {
			this.update(context, valueCollector);
			return false;
		}

		/**
		 * ����������������μ�{@code PerformanceMonitorStartResult}��˵����
		 * 
		 * @param valueCollector
		 *            ���ָ���ռ������ռ����Ͽ������á��������������Ա���һЩ����ض���
		 * @return �μ�{@code PerformanceMonitorStartResult}��˵��
		 */
		protected PerformanceMonitorStartResult startMonitor(Login login,
				TPerformanceValueContainer valueCollector) throws Throwable {
			this.update(login, valueCollector);
			return PerformanceMonitorStartResult.KEEP;
		}

		/**
		 * ��Ч���ṩ������ݣ���Ϊ��Ҫ����Context
		 * 
		 * @param context
		 *            �����ģ����ʱ�Ự�����ָ���������Ķ�Ӧ��Login���Ǳ���ػỰ
		 * @param valueCollector
		 *            ���ָ���ռ���
		 */
		protected void update(TContext context,
				TPerformanceValueContainer valueCollector) throws Throwable {
		}

		/**
		 * ����Context�ĸ������������ڲ�����Context�ĸ�Ч���ָ��
		 * 
		 * @param valueCollector
		 *            ���ָ���ռ���
		 * @return ����false��ʾ���ò��ɹ�����Ҫ���ô�Context �汾��update
		 */
		protected boolean update(Login login,
				TPerformanceValueContainer valueCollector) throws Throwable {
			return true;
		}

		/**
		 * ִ������
		 * 
		 * @param context
		 *            ������
		 * @param command
		 *            �����
		 * @param testOrExecute
		 *            ָʾ�ǲ����������Ч�Ի�����ʵ��ִ������
		 * @return <li>���ط��ز�����Ч����Ч(testOrExecute==true)<li>
		 *         ��ִ�гɹ���ʧ��(testOrExecute==false )
		 */
		protected boolean doCommand(TContext context, CommandDefine command,
				boolean testOrExecute) throws Throwable {
			return false;
		}

		/**
		 * �������ܼ��
		 * 
		 * @param context
		 *            �����ģ����ʱ�Ự�����ָ���������Ķ�Ӧ��Login���Ǳ���ػỰ
		 * @param valueCollector
		 *            ���ָ���ռ���
		 */
		protected void stopMonitor(TContext context,
				TPerformanceValueContainer valueCollector) throws Throwable {
		}

		/**
		 * ����Context�Ľ������������ڲ�����Context�ĸ�Ч���ָ��
		 * 
		 * @param valueCollector
		 *            ���ָ���ռ���
		 * @return ����false��ʾ���ò��ɹ�����Ҫ���ô�Context �汾��stopMonitor
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
				throw new UnsupportedOperationException("��֧�ֵļ�����ͣ�"
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
				throw new UnsupportedOperationException("���ܼ��ָ���ṩ��ָ���ռ������Ͷ�������");
			}
			this.declare = new PerformanceIndexDefineImpl(name, dataType,
					isSequence);
		}
	}

	// //////////////////////////////////////////////////
	// //////// ������
	// ////////////////////////////////////////////////
	static final int MAX_TASK_METHODS = 25;
	// ��ʾ�������һ���ķ���
	final static int METHODS_MASK = 1 << MAX_TASK_METHODS;
	static final int TASK_METHODS_MASK = -1 >> 32 - MAX_TASK_METHODS;

	private final static int getMethodMask(Enum<?> method) {
		if (method != null) {
			int ordinal = method.ordinal();
			if (ordinal > MAX_TASK_METHODS) {
				throw new IllegalArgumentException("����֧�ֳ���" + MAX_TASK_METHODS
						+ "�ִ�����");
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
			throw new IllegalArgumentException("û����Ч����������");
		}
		if (c > 1) {
			mm |= METHODS_MASK;
		}
		return mm;
	}

	/**
	 * ��������ࡣ<br>
	 * ������Ա��Service��������ʵ����������ʱ��Ҫʵ��һ���޲����Ĺ��캯��������ϵͳ�޷���ȷʹ�ø���������
	 * ���齫���ô��벿��д��Service�У�����������ֻ����ʵ���������ķ�������<br>
	 * ǿ�ҽ��飺����ʮ�ֱ�Ҫ������ò�ҪΪһ����������д�����ֻ����һ�������ࡣ<br>
	 * 
	 * @author Jeff Tang
	 * 
	 * @param <TTask>
	 *            ��������
	 * @param <TMethod>
	 *            ����ķ�������
	 */
	protected abstract class TaskMethodHandler<TTask extends Task<TMethod>, TMethod extends Enum<TMethod>>
			extends ServiceInvokeeBase<TTask, TContext, None, None, None> {
		/**
		 * ׼�����񣬸�����һ����������ǰ�Ļ��ᡣ <br>
		 * ÿ�ε���Handleǰ����ø÷���������������ڸ÷�������������� <br>
		 * ����������������ڸ�����Ĵ������׼���ʹ����������׼���ʹ���Ҳ��ѭ��ͬ�Ļ���<br>
		 * ��prepare ����handle�з�������Service����ֱ�ӷ��ʣ�����ʹ��XXXService.this.YYY
		 * 
		 * @param context
		 *            ������
		 * @param task
		 *            ����
		 * @param method
		 *            ����Ĵ�����
		 */
		@Override
		protected void prepare(TContext context, TTask task) throws Throwable {
			// Ĭ�ϲ������κ���
		}

		/**
		 * �������񣬵�ĳ���������������󣬾ͽ���÷����� ��������Ҫʵ�ָ÷���������ɶԱ�����Ĵ��� ��prepare
		 * ����handle�з�������Service����ֱ�ӷ��ʣ�����ʹ��XXXService.this.YYY
		 * 
		 * @param context
		 *            ������
		 * @param task
		 *            ����
		 * @param method
		 *            ����Ĵ�����
		 */
		@Override
		protected abstract void handle(TContext context, TTask task)
				throws Throwable;

		/**
		 * ���캯��<br>
		 * 
		 * @param first
		 *            ��һ����Ҫ����ķ���
		 * @param otherMethods
		 *            ������Ҫ����ķ���
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
		 * ��ȡ�������
		 * 
		 * @param typeArgFinder
		 *            ģ����Ϣ
		 * @return ���ؿմ������ע��
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
		 * ������ʼ
		 * 
		 * @param context
		 *            ������
		 * @return ����������״̬����
		 */
		protected abstract TBatchState beginBatch(TContext context)
				throws Throwable;

		/**
		 * ׼�����񣬸�����һ����������ǰ�Ļ��ᡣ <br>
		 * ÿ�ε���Handleǰ����ø÷���������������ڸ÷�������������� <br>
		 * ����������������ڸ�����Ĵ������׼���ʹ����������׼���ʹ���Ҳ��ѭ��ͬ�Ļ���<br>
		 * ��prepare ����handle�з�������Service����ֱ�ӷ��ʣ�����ʹ��XXXService.this.YYY
		 * 
		 * @param context
		 *            ������
		 * @param task
		 *            ����
		 * @param method
		 *            ����Ĵ�����
		 */
		@Override
		protected void prepare(TContext context, TTask task) throws Throwable {
			// Ĭ�ϲ������κ���
		}

		/**
		 * ��������������֮һ
		 * 
		 * @param context
		 *            ������
		 * @param task
		 *            �����������
		 * @param batchState
		 *            ������״̬����
		 */
		protected abstract boolean handle(TContext context, TTask task,
				TBatchState batchState) throws Throwable;

		/**
		 * ��������������������Դ
		 * 
		 * @param context
		 * @param batchState
		 *            ������״̬����
		 */
		protected void endBatch(TContext context, TBatchState batchState) {
		}

		/**
		 * ���캯��<br>
		 * 
		 * @param first
		 *            ��һ����Ҫ����ķ���
		 * @param otherMethods
		 *            ������Ҫ����ķ���
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
		 * ��ȡ�������
		 * 
		 * @param typeArgFinder
		 *            ģ����Ϣ
		 * @return ���ؿմ������ע��
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
	 * ����ṩ��
	 * 
	 * @author Jeff Tang
	 * 
	 * @param <TResult>
	 *            �������
	 */
	protected abstract class ResultProvider<TResult> extends
			ServiceInvokeeBase<TResult, TContext, None, None, None> {
		/**
		 * ����������һ�ķ�������
		 * 
		 * @param context
		 *            ������
		 * @return ���ؽ��
		 * @throws Throwable
		 *             �����쳣
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
		 * ��ȡ�������
		 * 
		 * @param typeArgFinder
		 *            ģ����Ϣ
		 * @return ���ؿմ������ע��
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
	 * ����ṩ��
	 * 
	 * @author Jeff Tang
	 * 
	 * @param <TResult>
	 *            �������
	 * @param <TKey>
	 *            ��ѯƾ��
	 */
	protected abstract class OneKeyResultProvider<TResult, TKey> extends
			ServiceInvokeeBase<TResult, TContext, TKey, None, None> {
		/**
		 * ����������һ�ķ�������
		 * 
		 * @param context
		 *            ������
		 * @param key
		 *            ��ѯƾ��
		 * @return ���ؽ��
		 * @throws Throwable
		 *             �����쳣
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
		 * ��ȡ�������
		 * 
		 * @param typeArgFinder
		 *            ģ����Ϣ
		 * @return ���ؿմ������ע��
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
	 * ����ṩ��
	 * 
	 * @author Jeff Tang
	 * 
	 * @param <TResult>
	 *            �������
	 * @param <TKey1>
	 *            ��ѯƾ��1
	 * @param <TKey2>
	 *            ��ѯƾ��2
	 */
	protected abstract class TwoKeyResultProvider<TResult, TKey1, TKey2>
			extends ServiceInvokeeBase<TResult, TContext, TKey1, TKey2, None> {
		/**
		 * ����������һ�ķ�������
		 * 
		 * @param context
		 *            ������
		 * @param key1
		 *            ��ѯƾ��1
		 * @param key2
		 *            ��ѯƾ��2
		 * @return ���ؽ��
		 * @throws Throwable
		 *             �����쳣
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
		 * ��ȡ�������
		 * 
		 * @param typeArgFinder
		 *            ģ����Ϣ
		 * @return ���ؿմ������ע��
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
	 * ����ṩ��
	 * 
	 * @author Jeff Tang
	 * 
	 * @param <TResult>
	 *            �������
	 * @param <TKey1>
	 *            ��ѯƾ��1
	 * @param <TKey2>
	 *            ��ѯƾ��2
	 */
	protected abstract class ThreeKeyResultProvider<TResult, TKey1, TKey2, TKey3>
			extends ServiceInvokeeBase<TResult, TContext, TKey1, TKey2, TKey3> {
		/**
		 * ����������һ�ķ�������
		 * 
		 * @param context
		 *            ������
		 * @param key1
		 *            ��ѯƾ��1
		 * @param key2
		 *            ��ѯƾ��2
		 * @param key3
		 *            ��ѯƾ��3
		 * @return ���ؽ��
		 * @throws Throwable
		 *             �����쳣
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
		 * ��ȡ�������
		 * 
		 * @param typeArgFinder
		 *            ģ����Ϣ
		 * @return ���ؿմ������ע��
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
	 * ����ṩ�������ڸ���ĳ��������һ�������
	 * 
	 * @author Jeff Tang
	 * 
	 * @param <TResult>
	 *            �������
	 */
	protected abstract class ResultListProvider<TResult> extends
			ServiceInvokeeBase<TResult, TContext, None, None, None> {
		/**
		 * ����ĳ����������һ�������
		 * 
		 * @param context
		 *            ������
		 * @param resultList
		 *            �����
		 * @throws Throwable
		 *             ��������쳣
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
		 * ��ȡ�������
		 * 
		 * @param typeArgFinder
		 *            ģ����Ϣ
		 * @return ���ؿմ������ע��
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
	 * ����ṩ�������ڸ���ĳ��������һ�������
	 * 
	 * @author Jeff Tang
	 * 
	 * @param <TResult>
	 *            �������
	 * @param <TKey>
	 *            ��ѯƾ��
	 */
	protected abstract class OneKeyResultListProvider<TResult, TKey> extends
			ServiceInvokeeBase<TResult, TContext, TKey, None, None> {
		/**
		 * ����ĳ����������һ�������
		 * 
		 * @param context
		 *            ������
		 * @param key
		 *            ��ѯƾ��
		 * @param resultList
		 *            �����
		 * @throws Throwable
		 *             ��������쳣
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
		 * ��ȡ�������
		 * 
		 * @param typeArgFinder
		 *            ģ����Ϣ
		 * @return ���ؿմ������ע��
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
	 * ����ṩ�������ڸ���ĳ��������һ�������
	 * 
	 * @author Jeff Tang
	 * 
	 * @param <TResult>
	 *            �������
	 * @param <TKey>
	 *            ��ѯƾ��
	 */
	protected abstract class TwoKeyResultListProvider<TResult, TKey1, TKey2>
			extends ServiceInvokeeBase<TResult, TContext, TKey1, TKey2, None> {
		/**
		 * ����ĳ����������һ�������
		 * 
		 * @param context
		 *            ������
		 * @param key
		 *            ��ѯƾ��
		 * @param resultList
		 *            �����
		 * @throws Throwable
		 *             ��������쳣
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
		 * ��ȡ�������
		 * 
		 * @param typeArgFinder
		 *            ģ����Ϣ
		 * @return ���ؿմ������ע��
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
	 * ����ṩ�������ڸ���ĳ��������һ�������
	 * 
	 * @author Jeff Tang
	 * 
	 * @param <TResult>
	 *            �������
	 * @param <TKey>
	 *            ��ѯƾ��
	 */
	protected abstract class ThreeKeyResultListProvider<TResult, TKey1, TKey2, TKey3>
			extends ServiceInvokeeBase<TResult, TContext, TKey1, TKey2, TKey3> {
		/**
		 * ����ĳ����������һ�������
		 * 
		 * @param context
		 *            ������
		 * @param key
		 *            ��ѯƾ��
		 * @param resultList
		 *            �����
		 * @throws Throwable
		 *             ��������쳣
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
		 * ��ȡ�������
		 * 
		 * @param typeArgFinder
		 *            ģ����Ϣ
		 * @return ���ؿմ������ע��
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
	 * ����ṩ�������ڸ���ĳ��������һ�������
	 * 
	 * @author Jeff Tang
	 * 
	 * @param <TResult>
	 *            �������
	 */
	protected abstract class TreeNodeProvider<TResult> extends
			ServiceInvokeeBase<TResult, TContext, None, None, None> {
		/**
		 * ����ĳ����������һ�������
		 * 
		 * @param context
		 *            ������
		 * @param resultList
		 *            �����
		 * @throws Throwable
		 *             ��������쳣
		 * @return resultTreeNode���������ṹ�еľ��Լ���
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
		 * ��ȡ�������
		 * 
		 * @param typeArgFinder
		 *            ģ����Ϣ
		 * @return ���ؿմ������ע��
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
	 * ����ṩ�������ڸ���ĳ��������һ�������
	 * 
	 * @author Jeff Tang
	 * 
	 * @param <TResult>
	 *            �������
	 * @param <TKey>
	 *            ��ѯƾ��
	 */
	protected abstract class OneKeyTreeNodeProvider<TResult, TKey> extends
			ServiceInvokeeBase<TResult, TContext, TKey, None, None> {
		/**
		 * ����ĳ����������һ�������
		 * 
		 * @param context
		 *            ������
		 * @param key
		 *            ��ѯƾ��
		 * @param resultList
		 *            �����
		 * @throws Throwable
		 *             ��������쳣
		 * @return resultTreeNode���������ṹ�еľ��Լ���
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
		 * ��ȡ�������
		 * 
		 * @param typeArgFinder
		 *            ģ����Ϣ
		 * @return ���ؿմ������ע��
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
	 * ����ṩ�������ڸ���ĳ��������һ�������
	 * 
	 * @author Jeff Tang
	 * 
	 * @param <TResult>
	 *            �������
	 * @param <TKey>
	 *            ��ѯƾ��
	 */
	protected abstract class TwoKeyTreeNodeProvider<TResult, TKey1, TKey2>
			extends ServiceInvokeeBase<TResult, TContext, TKey1, TKey2, None> {
		/**
		 * ����ĳ����������һ�������
		 * 
		 * @param context
		 *            ������
		 * @param key
		 *            ��ѯƾ��
		 * @param resultList
		 *            �����
		 * @throws Throwable
		 *             ��������쳣
		 * @return resultTreeNode���������ṹ�еľ��Լ���
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
		 * ��ȡ�������
		 * 
		 * @param typeArgFinder
		 *            ģ����Ϣ
		 * @return ���ؿմ������ע��
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
	 * ����ṩ�������ڸ���ĳ��������һ�������
	 * 
	 * @author Jeff Tang
	 * 
	 * @param <TResult>
	 *            �������
	 * @param <TKey>
	 *            ��ѯƾ��
	 */
	protected abstract class ThreeKeyTreeNodeProvider<TResult, TKey1, TKey2, TKey3>
			extends ServiceInvokeeBase<TResult, TContext, TKey1, TKey2, TKey3> {
		/**
		 * ����ĳ����������һ�������
		 * 
		 * @param context
		 *            ������
		 * @param key
		 *            ��ѯƾ��
		 * @param resultList
		 *            �����
		 * @throws Throwable
		 *             ��������쳣
		 * @return resultTreeNode���������ṹ�еľ��Լ���
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
		 * ��ȡ�������
		 * 
		 * @param typeArgFinder
		 *            ģ����Ϣ
		 * @return ���ؿմ������ע��
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
	 * ��������ִ����
	 * 
	 * @author Jeff Tang
	 * 
	 */
	protected abstract class CaseTester implements CaseTesterInstance {
		/**
		 * �������ش˷����ṩ�ò�������������
		 */
		public String getDescription() {
			return "";
		}

		/**
		 * �������ش˷����������������
		 */
		public String getName() {
			return this.getClass().getName();
		}

		/**
		 * ��������ı���
		 */
		public final String getCode() {
			return this.code;
		}

		/**
		 * ��ȡ���ݿ����Ӷ���<br>
		 * �����Թرոö��󣬶���ʹ�øö��󴴽�������JDBC����Ҫ��ʱ�رպ��ͷţ�<br>
		 * ϵͳ������رպ��ͷ���ض���
		 * 
		 * @throws SQLException
		 */
		protected Connection getDBConnection(Context context)
				throws SQLException {
			return ContextImpl.toContext(context).getDBAdapter()
					.testGetConnection();
		}

		/**
		 * ��������
		 * 
		 * @param context
		 *            ������
		 * @param testContext
		 *            ����������
		 * @param category
		 *            �������
		 */
		protected abstract void testCase(TContext context,
				TestContext testContext) throws Throwable;

		public CaseTester(String code) {
			this.code = code;
		}

		// /////////////////////////////////////////
		// ////// �ڲ�����
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
	 * ģ���״̬
	 */
	protected enum ServiceState {
		/**
		 * ����״̬
		 */
		CREATING,
		/**
		 * ����ע��
		 */
		REGISTERING,
		/**
		 * ע��ʧ��
		 */
		REGISTERERROR,
		/**
		 * ע�����
		 */
		REGISTERED,
		/**
		 * ��ʼ״̬
		 */
		INITIALIZING,
		/**
		 * ��ʼ������
		 */
		INITIALIZEERROR,
		/**
		 * ��ʼ�����
		 */
		INITIALIZED,
		/**
		 * ���ڼ���
		 */
		ACTIVING,
		/**
		 * ����ʱ����
		 */
		ACTIVEERROR,
		/**
		 * ��Ч״̬
		 */
		ACTIVED,
		/**
		 * ��ʼ����
		 */
		DISPOSING,
		/**
		 * �������
		 */
		DISPOSED,
	}

	/**
	 * ���ģ���״̬
	 * 
	 * @return ����״̬
	 */
	protected final ServiceState getState() {
		return this.state;
	}

	/**
	 * �����������еĵ�ǰģ����Ϣ����Դ��������Ҫ���أ��Ը��µ�ǰ��Դ������
	 */
	@Override
	SpaceNode updateContextSpace(ContextImpl<?, ?, ?> context) {
		SpaceNode occorAt = context.occorAt;
		context.occorAt = this.space;
		context.occorAtResourceService = null;
		return occorAt;
	}

	// ////////////////////////////////////////////////////////////////////////
	// ///////������ע�����
	// ////////////////////////////////////////////////////////////////////////
	/**
	 * ����ע����
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
	 * ���ø÷���ע���ģ���µ����е�����
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
	// ////��ʼ�����
	// //////////////////////////////////////////////////
	/**
	 * ģ���״̬
	 */
	ServiceState state = ServiceState.CREATING;

	/**
	 * ���Գ�ʼ��
	 * 
	 * @param context
	 *            ������
	 * @throws Throwable
	 *             �׳��쳣
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
			throw new IllegalStateException("���������״̬����");
		}
		return false;
	}

	/**
	 * ��������
	 * 
	 * @param context
	 *            ������
	 * @throws Throwable
	 *             �׳��쳣
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
	 * ���Լ�����Դ����ļ�·����Ϣ
	 */
	boolean tryBuildResourceKeyPathInfos(
			List<ResourceServiceBase<?, ?, ?>> servicesCache) {
		return false;
	}

	boolean tryBuildResourceRefAuthInfo(OperationMapImpl<?, ?> mapCache) {
		return false;
	}

	/**
	 * ����������Դ����ĸ���Դ
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
