package org.eclipse.jt.core.resource;

import org.eclipse.jt.core.Context;
import org.eclipse.jt.core.Login;
import org.eclipse.jt.core.None;
import org.eclipse.jt.core.auth.Operation;
import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.impl.ResourceServiceBase;
import org.eclipse.jt.core.impl.ServiceBase;
import org.eclipse.jt.core.invoke.Event;
import org.eclipse.jt.core.invoke.SimpleTask;
import org.eclipse.jt.core.invoke.Task;
import org.eclipse.jt.core.spi.monitor.PerformanceIndexDeclare;
import org.eclipse.jt.core.spi.monitor.PerformanceMonitorStartResult;
import org.eclipse.jt.core.spi.monitor.PerformanceValueCollector;
import org.eclipse.jt.core.spi.monitor.PerformanceIndexDefine.CommandDefine;
import org.eclipse.jt.core.type.GUID;


/**
 * 资源管理器
 * 
 * @author Jeff Tang
 * 
 * @param <TFacade>
 *            资源外观，即资源实现提供的只读接口
 * @param <TModifier>
 *            资源修改器，既可以用来修改资源的接口或者类型，大部分时候使用资源的实现类型
 * @param <TKeysHolder>
 *            资源键源，既可以从中得到资源的键的值的接口或者类型，大部分时候使用资源的实现类型
 * @param <TResourceMetaData>
 *            资源原数据，需要原数据的资源管理器需要指定，否则指定为Object
 */
public abstract class ResourceService<TFacade, TImpl extends TFacade, TKeysHolder>
		extends ResourceServiceBase<TFacade, TImpl, TKeysHolder> {

	/**
	 * 初始化资源，添加资源
	 * 
	 * @param context
	 *            上下文
	 * @param initializer
	 *            资源初始器
	 */
	@Override
	protected void initResources(Context context,
			ResourceInserter<TFacade, TImpl, TKeysHolder> initializer)
			throws Throwable {
	}

	@Override
	public final String getTitle() {
		return super.getTitle();
	}

	/**
	 * 构造方法，提供给子类指定服务的标题所用<br>
	 * 例如：
	 * 
	 * <pre>
	 * class DeptResourceService extends ResourceService {
	 * 	DeptResourceService() {
	 * 		super(&quot;部门&quot;);
	 * 	}
	 * }
	 * </pre>
	 * 
	 * @param title
	 *            指定资源服务的标题，作为框架监控和管理这些服务时该服务易读的标识。
	 */
	protected ResourceService(String title) {
		super(title, ResourceKind.SINGLETON_IN_SITE);
	}

	/**
	 * 构造方法，提供给子类指定服务的标题所用<br>
	 * 例如：
	 * 
	 * <pre>
	 * class DeptResourceService extends ResourceService {
	 * 	DeptResourceService() {
	 * 		super(&quot;部门&quot;, ResourceKind.SINGLETON_IN_GLOBAL);
	 * 	}
	 * }
	 * </pre>
	 * 
	 * @param title
	 *            指定该资源默认类别的标题，作为框架监控和管理这些服务时该服务易读的标识。
	 * @param kind
	 *            资源类别
	 */
	protected ResourceService(String title, ResourceKind kind) {
		super(title, kind);
	}

	/**
	 * 注册资源类别
	 * 
	 * @param category
	 *            类别，可以为None.NONE,表示默认的类别
	 * @param title
	 *            指定注册资源类别的标题，作为框架监控和管理这些服务时该服务易读的标识。
	 */
	@Override
	protected final void registerCategory(Object category, String title) {
		super.registerCategory(category, title);
	}

	/**
	 * 注销资源类别
	 * 
	 * @param category
	 *            资源类别
	 */
	@Override
	protected final void unRegisterCategory(Object category) {
		super.unRegisterCategory(category);
	}

	/**
	 * 该方法用于在访问授权资源前，对资源上下文环境进行调整。 通过该方法进行的部分非事务性操作将不能进行回滚，需慎重使用。
	 */
	@Override
	protected void beforeAccessAuthorityResource(Context context) {

	}

	/**
	 * 该方法用于在访问授权资源后，对资源上下文环境进行调整。 通过该方法进行的部分非事务性操作将不能进行回滚，需慎重使用。
	 */
	@Override
	protected void endAccessAuthorityResource(Context context) {

	}

	/**
	 * 操作映射接口，用以指定参考资源之间的权限影响时操作的映射关系
	 * 
	 * @author Jeff Tang
	 * 
	 * @param <TResourceFacade>
	 *            被验证资源的外观
	 * @param <TMapToResourceFacade>
	 *            影响权限的资源地外观
	 */
	public interface OperationMap<TResourceFacade, TMapToResourceFacade> {
		public void map(
				Enum<? extends Operation<? super TResourceFacade>> operation,
				Enum<? extends Operation<? super TMapToResourceFacade>> mapToOperation);
	}

	/**
	 * 资源参考定义，定义本资源参考（引用）另一种资源
	 * 
	 * @author Jeff Tang
	 * 
	 * @param <TRefFacade>
	 *            资源类型
	 */
	protected abstract class ResourceReference<TRefFacade> extends
			ResourceServiceBase.ResourceReference<TRefFacade, TFacade> {

		/**
		 * 设置引用者与被引用者之间的权限映射关系，<br>
		 * 通过该借口设置映射关系表示被引用者的权限设置将影响引用者的权限验证
		 */
		@Override
		protected void authMapOperation(
				OperationMap<TFacade, TRefFacade> operationMap) {
		}

		/**
		 * 没有指定权限影响的构造方法
		 */
		public ResourceReference() {
			super(null, ResourceService.this);
		}
	}

	/**
	 * 资源本参考定义，定义本资源被另一种资源参考（引用）
	 * 
	 * @author Jeff Tang
	 * @param <TReferredByFacade>
	 *            引用本资源的资源的外观类型
	 */
	protected abstract class ReferredByResource<TReferredByFacade> extends
			ResourceServiceBase.ResourceReference<TFacade, TReferredByFacade> {

		/**
		 * 设置引用者与被引用者之间的权限映射关系，<br>
		 * 通过该借口设置映射关系表示被引用者的权限设置将影响引用者的权限验证
		 */
		@Override
		protected void authMapOperation(
				OperationMap<TReferredByFacade, TFacade> operationMap) {
		}

		/**
		 * 没有指定权限影响的构造方法
		 */
		public ReferredByResource() {
			super(ResourceService.this, null);
		}
	}

	/**
	 * 事件监听器
	 */
	protected abstract class EventListener<TEvent extends Event>
			extends
			ServiceBase<ResourceContext<TFacade, TImpl, TKeysHolder>>.EventListener<TEvent> {
		/**
		 * 构造函数
		 * 
		 * 默认的执行优先级
		 */
		protected EventListener() {
			super(0f);
		}

		/**
		 * 构造函数
		 * 
		 * @param priority
		 *            执行优先级，越小的越先执行
		 */
		protected EventListener(float priority) {
			super(priority);
		}
	}

	/**
	 * 任务处理基类。<br>
	 * 开发人员在Service的子类中实现任务处理类时需要实现一个无参数的构造函数，否则系统无法正确使用该任务处理类
	 * 建议将公用代码部分写在Service中，而任务处理类只负责实现任务具体的方法处理<br>
	 * 强烈建议：除非十分必要否则最好不要为一个任务的所有处理方法只定义一个处理类。<br>
	 */
	protected abstract class TaskMethodHandler<TTask extends Task<TMethod>, TMethod extends Enum<TMethod>>
			extends
			ServiceBase<ResourceContext<TFacade, TImpl, TKeysHolder>>.TaskMethodHandler<TTask, TMethod> {
		protected TaskMethodHandler(TMethod first, TMethod... otherMethods) {
			super(first, otherMethods);
		}
	}

	/**
	 * 简单任务处理器基类
	 */
	protected abstract class SimpleTaskMethodHandler<TTask extends SimpleTask>
			extends
			ServiceBase<ResourceContext<TFacade, TImpl, TKeysHolder>>.TaskMethodHandler<TTask, None> {

		protected SimpleTaskMethodHandler() {
			super(None.NONE, null);
		}
	}

	/**
	 * 单例结果提供器
	 */
	protected abstract class ResultProvider<TResult>
			extends
			ServiceBase<ResourceContext<TFacade, TImpl, TKeysHolder>>.ResultProvider<TResult> {
		// nothing to do.
	}

	/**
	 * 单键结果提供器
	 */
	protected abstract class OneKeyResultProvider<TResult, TKey>
			extends
			ServiceBase<ResourceContext<TFacade, TImpl, TKeysHolder>>.OneKeyResultProvider<TResult, TKey> {
		// nothing to do.
	}

	/**
	 * 双键结果提供器
	 */
	protected abstract class TwoKeyResultProvider<TResult, TKey1, TKey2>
			extends
			ServiceBase<ResourceContext<TFacade, TImpl, TKeysHolder>>.TwoKeyResultProvider<TResult, TKey1, TKey2> {
		// nothing to do.
	}

	/**
	 * 三键结果提供器
	 */
	protected abstract class ThreeKeyResultProvider<TResult, TKey1, TKey2, TKey3>
			extends
			ServiceBase<ResourceContext<TFacade, TImpl, TKeysHolder>>.ThreeKeyResultProvider<TResult, TKey1, TKey2, TKey3> {
		// nothing to do.
	}

	/**
	 * 结果提供器，用于返回一个结果集
	 */
	protected abstract class ResultListProvider<TResult>
			extends
			ServiceBase<ResourceContext<TFacade, TImpl, TKeysHolder>>.ResultListProvider<TResult> {
		// nothing to do.
	}

	/**
	 * 单键结果提供器，用于根据指定的条件返回一个结果集
	 */
	protected abstract class OneKeyResultListProvider<TResult, TKey>
			extends
			ServiceBase<ResourceContext<TFacade, TImpl, TKeysHolder>>.OneKeyResultListProvider<TResult, TKey> {
		// nothing to do.
	}

	/**
	 * 双键结果提供器，用于根据指定的条件返回一个结果集
	 */
	protected abstract class TwoKeyResultListProvider<TResult, TKey1, TKey2>
			extends
			ServiceBase<ResourceContext<TFacade, TImpl, TKeysHolder>>.TwoKeyResultListProvider<TResult, TKey1, TKey2> {
		// nothing to do.
	}

	/**
	 * 三键结果提供器，用于根据指定的条件返回一个结果集
	 */
	protected abstract class ThreeKeyResultListProvider<TResult, TKey1, TKey2, TKey3>
			extends
			ServiceBase<ResourceContext<TFacade, TImpl, TKeysHolder>>.ThreeKeyResultListProvider<TResult, TKey1, TKey2, TKey3> {
		// nothing to do.
	}

	/**
	 * 树结构提供器，用于返回一个树结构
	 */
	protected abstract class TreeNodeProvider<TResult> extends
			ServiceBase<Context>.TreeNodeProvider<TResult> {
		// nothing to do.
	}

	/**
	 * 单键树结构提供器，用于根据指定的条件返回一个树结构
	 */
	protected abstract class OneKeyTreeNodeProvider<TResult, TKey> extends
			ServiceBase<Context>.OneKeyTreeNodeProvider<TResult, TKey> {
		// nothing to do.
	}

	/**
	 * 双键树结构提供器，用于根据指定的条件返回一个树结构
	 */
	protected abstract class TwoKeyTreeNodeProvider<TResult, TKey1, TKey2>
			extends
			ServiceBase<Context>.TwoKeyTreeNodeProvider<TResult, TKey1, TKey2> {
		// nothing to do.
	}

	/**
	 * 三键树结构提供器，用于根据指定的条件返回一个树结构
	 */
	protected abstract class ThreeKeyTreeNodeProvider<TResult, TKey1, TKey2, TKey3>
			extends
			ServiceBase<Context>.ThreeKeyTreeNodeProvider<TResult, TKey1, TKey2, TKey3> {
		// nothing to do.
	}

	/**
	 * 添加资源时，如果具有相同键值的资源已经存在，则应该采取的策略。
	 * 
	 * @author Jeff Tang
	 * @version 1.0
	 */
	public enum WhenExists {
		/**
		 * 抛出异常。
		 */
		EXCEPTION,

		/**
		 * 替换已经存在的资源（缺省时采用的策略）。
		 */
		REPLACE,

		/**
		 * 忽略，即不添加。
		 */
		IGNORE,
	}

	protected abstract class SingletonResourceProvider
			extends
			ResourceServiceBase<TFacade, TImpl, TKeysHolder>.SingletonResourceProvider {
	}

	protected abstract class OneKeyResourceProvider<TKey>
			extends
			ResourceServiceBase<TFacade, TImpl, TKeysHolder>.OneKeyResourceProvider<TKey> {
	}

	protected abstract class TwoKeyResourceProvider<TKey1, TKey2>
			extends
			ResourceServiceBase<TFacade, TImpl, TKeysHolder>.TwoKeyResourceProvider<TKey1, TKey2> {
	}

	protected abstract class ThreeKeyResourceProvider<TKey1, TKey2, TKey3>
			extends
			ResourceServiceBase<TFacade, TImpl, TKeysHolder>.ThreeKeyResourceProvider<TKey1, TKey2, TKey3> {
	}

	/**
	 * 监控指标提供器
	 * 
	 * @author Jeff Tang
	 * 
	 */
	protected abstract class PerformanceProvider<TPerformanceValueContainer extends PerformanceValueCollector<?>>
			extends
			ServiceBase<ResourceContext<TFacade, TImpl, TKeysHolder>>.PerformanceProvider<TPerformanceValueContainer> {
		/**
		 * 监控器启动方法，含Context版本。默认返回false，表示保持。<br>
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
		@Override
		protected boolean startMonitor(
				ResourceContext<TFacade, TImpl, TKeysHolder> context,
				TPerformanceValueContainer valueCollector) {
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
		@Override
		protected PerformanceMonitorStartResult startMonitor(Login login,
				TPerformanceValueContainer valueCollector) {
			return this.update(login, valueCollector) ? PerformanceMonitorStartResult.KEEP
					: PerformanceMonitorStartResult.NEED_CONTEXT;
		}

		/**
		 * 低效地提供监控数据，因为需要创建Context
		 * 
		 * @param context
		 *            上下文，如果时会话级监控指标则上下文对应的Login则是被监控会话
		 * @param valueCollector
		 *            监控指标收集器
		 */
		@Override
		protected void update(
				ResourceContext<TFacade, TImpl, TKeysHolder> context,
				TPerformanceValueContainer valueCollector) {
		}

		/**
		 * 不带Context的更新器，适用于不依赖Context的高效监控指标
		 * 
		 * @param valueCollector
		 *            监控指标收集器
		 * @return 返回false表示调用不成功，需要调用带Context 版本的update
		 */
		@Override
		protected boolean update(Login login,
				TPerformanceValueContainer valueCollector) {
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
		@Override
		protected boolean doCommand(
				ResourceContext<TFacade, TImpl, TKeysHolder> context,
				CommandDefine command, boolean testOrExecute) {
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
		@Override
		protected void stopMonitor(
				ResourceContext<TFacade, TImpl, TKeysHolder> context,
				TPerformanceValueContainer valueCollector) {
		}

		/**
		 * 不带Context的结束器，适用于不依赖Context的高效监控指标
		 * 
		 * @param valueCollector
		 *            监控指标收集器
		 * @return 返回false表示调用不成功，需要调用带Context 版本的stopMonitor
		 */
		@Override
		protected boolean stopMonitor(Login login,
				TPerformanceValueContainer valueCollector) {
			return true;
		}

		/**
		 * 获得监控指标定义
		 */
		protected final PerformanceIndexDeclare getDeclare() {
			return this.declare;
		}

		protected PerformanceProvider(String name) {
			super(name);
		}
	}

	/**
	 * 测试用例执行器
	 * 
	 * @author Jeff Tang
	 * 
	 */
	protected abstract class CaseTester extends ServiceBase<Context>.CaseTester {
		/**
		 * 构造方法
		 * 
		 * @param order
		 *            测试用例的顺序号,便于执行时确定先后顺序
		 */
		public CaseTester(String code) {
			super(code);
		}
	}

	/**
	 * 标明该资源需要权限授权的资源提供器<br>
	 * 该提供器本身作为GUID键的提供器，<br>
	 * 并且通过指定操作枚举范型参数来指定该资源的权限操作
	 */
	protected abstract class AuthorizableResourceProvider<TOperationEnum extends Enum<? extends Operation<? super TFacade>>>
			extends
			ResourceServiceBase<TFacade, TImpl, TKeysHolder>.AuthorizableResourceProvider<TOperationEnum> {
		/**
		 * 返回某个资源的标题用于权限设置使用
		 */
		@Override
		protected abstract String getResourceTitle(TImpl resource,
				TKeysHolder keys);

		/**
		 * 根据资源键源返回当前提供器对应的键
		 * 
		 * @param keys
		 *            资源键源
		 * @return 返回当前提供器对应的键
		 */
		@Override
		protected abstract GUID getKey1(TKeysHolder keys);

		/**
		 * 构造方法<br>
		 * 使用类名自动生成defaultCategoryID
		 */
		public AuthorizableResourceProvider() {
			super(null, false);
		}

		/**
		 * 构造方法<br>
		 * 使用类名自动生成defaultCategoryID
		 * 
		 * @param looseAuthPolicy
		 *            是否应用宽松权限控制策略（默认没有权限）
		 */
		public AuthorizableResourceProvider(boolean looseAuthPolicy) {
			super(null, looseAuthPolicy);
		}

		/**
		 * 构造方法<br>
		 * 指定defaultCategoryID
		 * 
		 * @param defaultCategoryID
		 *            默认的资源类别ID,不可以为null和Empty
		 */
		public AuthorizableResourceProvider(GUID defaultCategoryID) {
			this(defaultCategoryID, false);
		}

		/**
		 * 构造方法<br>
		 * 指定defaultCategoryID
		 * 
		 * @param defaultCategoryID
		 *            默认的资源类别ID,不可以为null和Empty
		 * @param looseAuthPolicy
		 *            是否应用宽松权限控制策略（默认没有权限）
		 */
		public AuthorizableResourceProvider(GUID defaultCategoryID,
				boolean looseAuthPolicy) {
			super(defaultCategoryID, looseAuthPolicy);
			if (defaultCategoryID == null) {
				throw new NullArgumentException("defaultCategoryID");
			}
		}

	}

}