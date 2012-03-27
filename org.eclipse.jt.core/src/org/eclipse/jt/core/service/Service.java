package org.eclipse.jt.core.service;

import org.eclipse.jt.core.Context;
import org.eclipse.jt.core.Login;
import org.eclipse.jt.core.None;
import org.eclipse.jt.core.impl.ServiceBase;
import org.eclipse.jt.core.invoke.Event;
import org.eclipse.jt.core.invoke.SimpleTask;
import org.eclipse.jt.core.invoke.Task;
import org.eclipse.jt.core.spi.monitor.PerformanceIndexDeclare;
import org.eclipse.jt.core.spi.monitor.PerformanceMonitorStartResult;
import org.eclipse.jt.core.spi.monitor.PerformanceValueCollector;
import org.eclipse.jt.core.spi.monitor.PerformanceIndexDefine.CommandDefine;

/**
 * 模块基类
 * 
 * @author Jeff Tang
 * 
 */
public abstract class Service extends ServiceBase<Context> {

	/**
	 * 获得服务的标题
	 */
	@Override
	public final String getTitle() {
		return super.getTitle();
	}

	/**
	 * 构造方法，提供给子类指定服务的标题所用<br>
	 * 例如：
	 * 
	 * <pre>
	 * class MyService extends Service {
	 * 	MyService() {
	 * 		super(&quot;我的服务&quot;);
	 * 	}
	 * }
	 * </pre>
	 * 
	 * @param title
	 *            指定服务的标题，作为框架监控和管理这些服务时该服务易读的标识。
	 */
	protected Service(String title) {
		super(title);
	}

	/**
	 * 事件监听器
	 */
	protected abstract class EventListener<TEvent extends Event> extends
			ServiceBase<Context>.EventListener<TEvent> {
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
			extends ServiceBase<Context>.TaskMethodHandler<TTask, TMethod> {
		protected TaskMethodHandler(TMethod first, TMethod... otherMethods) {
			super(first, otherMethods);
		}
	}

	/**
	 * 简单任务处理器基类
	 */
	protected abstract class SimpleTaskMethodHandler<TTask extends SimpleTask>
			extends ServiceBase<Context>.TaskMethodHandler<TTask, None> {
		protected SimpleTaskMethodHandler() {
			super(None.NONE, null);
		}
	}

	/**
	 * 单例结果提供器
	 */
	protected abstract class ResultProvider<TResult> extends
			ServiceBase<Context>.ResultProvider<TResult> {
		// nothing to do.
	}

	/**
	 * 单键结果提供器
	 */
	protected abstract class OneKeyResultProvider<TResult, TKey> extends
			ServiceBase<Context>.OneKeyResultProvider<TResult, TKey> {
		// nothing to do.
	}

	/**
	 * 双键结果提供器
	 */
	protected abstract class TwoKeyResultProvider<TResult, TKey1, TKey2>
			extends
			ServiceBase<Context>.TwoKeyResultProvider<TResult, TKey1, TKey2> {
		// nothing to do.
	}

	/**
	 * 三键结果提供器
	 */
	protected abstract class ThreeKeyResultProvider<TResult, TKey1, TKey2, TKey3>
			extends
			ServiceBase<Context>.ThreeKeyResultProvider<TResult, TKey1, TKey2, TKey3> {
		// nothing to do.
	}

	/**
	 * 结果集（列表形式）提供器，用于返回一个结果集。
	 */
	protected abstract class ResultListProvider<TResult> extends
			ServiceBase<Context>.ResultListProvider<TResult> {
		// nothing to do.
	}

	/**
	 * 单键结果集（列表形式）提供器，用于根据指定的条件返回一个结果集。
	 */
	protected abstract class OneKeyResultListProvider<TResult, TKey> extends
			ServiceBase<Context>.OneKeyResultListProvider<TResult, TKey> {
		// nothing to do.
	}

	/**
	 * 双键结果集（列表形式）提供器，用于根据指定的条件返回一个结果集。
	 */
	protected abstract class TwoKeyResultListProvider<TResult, TKey1, TKey2>
			extends
			ServiceBase<Context>.TwoKeyResultListProvider<TResult, TKey1, TKey2> {
		// nothing to do.
	}

	/**
	 * 三键结果集（列表形式）提供器，用于根据指定的条件返回一个结果集
	 */
	protected abstract class ThreeKeyResultListProvider<TResult, TKey1, TKey2, TKey3>
			extends
			ServiceBase<Context>.ThreeKeyResultListProvider<TResult, TKey1, TKey2, TKey3> {
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
	 * 监控指标提供器
	 * 
	 * @author Jeff Tang
	 * 
	 */
	protected abstract class PerformanceProvider<TPerformanceValueContainer extends PerformanceValueCollector<?>>
			extends
			ServiceBase<Context>.PerformanceProvider<TPerformanceValueContainer> {
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
		protected boolean startMonitor(Context context,
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
		protected void update(Context context,
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
		protected boolean doCommand(Context context, CommandDefine command,
				boolean testOrExecute) {
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
		protected void stopMonitor(Context context,
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
}
