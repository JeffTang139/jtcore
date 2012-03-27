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
 * ģ�����
 * 
 * @author Jeff Tang
 * 
 */
public abstract class Service extends ServiceBase<Context> {

	/**
	 * ��÷���ı���
	 */
	@Override
	public final String getTitle() {
		return super.getTitle();
	}

	/**
	 * ���췽�����ṩ������ָ������ı�������<br>
	 * ���磺
	 * 
	 * <pre>
	 * class MyService extends Service {
	 * 	MyService() {
	 * 		super(&quot;�ҵķ���&quot;);
	 * 	}
	 * }
	 * </pre>
	 * 
	 * @param title
	 *            ָ������ı��⣬��Ϊ��ܼ�غ͹�����Щ����ʱ�÷����׶��ı�ʶ��
	 */
	protected Service(String title) {
		super(title);
	}

	/**
	 * �¼�������
	 */
	protected abstract class EventListener<TEvent extends Event> extends
			ServiceBase<Context>.EventListener<TEvent> {
		/**
		 * ���캯��
		 * 
		 * Ĭ�ϵ�ִ�����ȼ�
		 */
		protected EventListener() {
			super(0f);
		}

		/**
		 * ���캯��
		 * 
		 * @param priority
		 *            ִ�����ȼ���ԽС��Խ��ִ��
		 */
		protected EventListener(float priority) {
			super(priority);
		}
	}

	/**
	 * ��������ࡣ<br>
	 * ������Ա��Service��������ʵ����������ʱ��Ҫʵ��һ���޲����Ĺ��캯��������ϵͳ�޷���ȷʹ�ø���������
	 * ���齫���ô��벿��д��Service�У�����������ֻ����ʵ���������ķ�������<br>
	 * ǿ�ҽ��飺����ʮ�ֱ�Ҫ������ò�ҪΪһ����������д�����ֻ����һ�������ࡣ<br>
	 */
	protected abstract class TaskMethodHandler<TTask extends Task<TMethod>, TMethod extends Enum<TMethod>>
			extends ServiceBase<Context>.TaskMethodHandler<TTask, TMethod> {
		protected TaskMethodHandler(TMethod first, TMethod... otherMethods) {
			super(first, otherMethods);
		}
	}

	/**
	 * ��������������
	 */
	protected abstract class SimpleTaskMethodHandler<TTask extends SimpleTask>
			extends ServiceBase<Context>.TaskMethodHandler<TTask, None> {
		protected SimpleTaskMethodHandler() {
			super(None.NONE, null);
		}
	}

	/**
	 * ��������ṩ��
	 */
	protected abstract class ResultProvider<TResult> extends
			ServiceBase<Context>.ResultProvider<TResult> {
		// nothing to do.
	}

	/**
	 * ��������ṩ��
	 */
	protected abstract class OneKeyResultProvider<TResult, TKey> extends
			ServiceBase<Context>.OneKeyResultProvider<TResult, TKey> {
		// nothing to do.
	}

	/**
	 * ˫������ṩ��
	 */
	protected abstract class TwoKeyResultProvider<TResult, TKey1, TKey2>
			extends
			ServiceBase<Context>.TwoKeyResultProvider<TResult, TKey1, TKey2> {
		// nothing to do.
	}

	/**
	 * ��������ṩ��
	 */
	protected abstract class ThreeKeyResultProvider<TResult, TKey1, TKey2, TKey3>
			extends
			ServiceBase<Context>.ThreeKeyResultProvider<TResult, TKey1, TKey2, TKey3> {
		// nothing to do.
	}

	/**
	 * ��������б���ʽ���ṩ�������ڷ���һ���������
	 */
	protected abstract class ResultListProvider<TResult> extends
			ServiceBase<Context>.ResultListProvider<TResult> {
		// nothing to do.
	}

	/**
	 * ������������б���ʽ���ṩ�������ڸ���ָ������������һ���������
	 */
	protected abstract class OneKeyResultListProvider<TResult, TKey> extends
			ServiceBase<Context>.OneKeyResultListProvider<TResult, TKey> {
		// nothing to do.
	}

	/**
	 * ˫����������б���ʽ���ṩ�������ڸ���ָ������������һ���������
	 */
	protected abstract class TwoKeyResultListProvider<TResult, TKey1, TKey2>
			extends
			ServiceBase<Context>.TwoKeyResultListProvider<TResult, TKey1, TKey2> {
		// nothing to do.
	}

	/**
	 * ������������б���ʽ���ṩ�������ڸ���ָ������������һ�������
	 */
	protected abstract class ThreeKeyResultListProvider<TResult, TKey1, TKey2, TKey3>
			extends
			ServiceBase<Context>.ThreeKeyResultListProvider<TResult, TKey1, TKey2, TKey3> {
		// nothing to do.
	}

	/**
	 * ���ṹ�ṩ�������ڷ���һ�����ṹ
	 */
	protected abstract class TreeNodeProvider<TResult> extends
			ServiceBase<Context>.TreeNodeProvider<TResult> {
		// nothing to do.
	}

	/**
	 * �������ṹ�ṩ�������ڸ���ָ������������һ�����ṹ
	 */
	protected abstract class OneKeyTreeNodeProvider<TResult, TKey> extends
			ServiceBase<Context>.OneKeyTreeNodeProvider<TResult, TKey> {
		// nothing to do.
	}

	/**
	 * ˫�����ṹ�ṩ�������ڸ���ָ������������һ�����ṹ
	 */
	protected abstract class TwoKeyTreeNodeProvider<TResult, TKey1, TKey2>
			extends
			ServiceBase<Context>.TwoKeyTreeNodeProvider<TResult, TKey1, TKey2> {
		// nothing to do.
	}

	/**
	 * �������ṹ�ṩ�������ڸ���ָ������������һ�����ṹ
	 */
	protected abstract class ThreeKeyTreeNodeProvider<TResult, TKey1, TKey2, TKey3>
			extends
			ServiceBase<Context>.ThreeKeyTreeNodeProvider<TResult, TKey1, TKey2, TKey3> {
		// nothing to do.
	}

	/**
	 * ���ָ���ṩ��
	 * 
	 * @author Jeff Tang
	 * 
	 */
	protected abstract class PerformanceProvider<TPerformanceValueContainer extends PerformanceValueCollector<?>>
			extends
			ServiceBase<Context>.PerformanceProvider<TPerformanceValueContainer> {
		/**
		 * �����������������Context�汾��Ĭ�Ϸ���false����ʾ���֡�<br>
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
		@Override
		protected boolean startMonitor(Context context,
				TPerformanceValueContainer valueCollector) {
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
		@Override
		protected PerformanceMonitorStartResult startMonitor(Login login,
				TPerformanceValueContainer valueCollector) {
			return this.update(login, valueCollector) ? PerformanceMonitorStartResult.KEEP
					: PerformanceMonitorStartResult.NEED_CONTEXT;
		}

		/**
		 * ��Ч���ṩ������ݣ���Ϊ��Ҫ����Context
		 * 
		 * @param context
		 *            �����ģ����ʱ�Ự�����ָ���������Ķ�Ӧ��Login���Ǳ���ػỰ
		 * @param valueCollector
		 *            ���ָ���ռ���
		 */
		@Override
		protected void update(Context context,
				TPerformanceValueContainer valueCollector) {
		}

		/**
		 * ����Context�ĸ������������ڲ�����Context�ĸ�Ч���ָ��
		 * 
		 * @param valueCollector
		 *            ���ָ���ռ���
		 * @return ����false��ʾ���ò��ɹ�����Ҫ���ô�Context �汾��update
		 */
		@Override
		protected boolean update(Login login,
				TPerformanceValueContainer valueCollector) {
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
		@Override
		protected boolean doCommand(Context context, CommandDefine command,
				boolean testOrExecute) {
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
		@Override
		protected void stopMonitor(Context context,
				TPerformanceValueContainer valueCollector) {
		}

		/**
		 * ����Context�Ľ������������ڲ�����Context�ĸ�Ч���ָ��
		 * 
		 * @param valueCollector
		 *            ���ָ���ռ���
		 * @return ����false��ʾ���ò��ɹ�����Ҫ���ô�Context �汾��stopMonitor
		 */
		@Override
		protected boolean stopMonitor(Login login,
				TPerformanceValueContainer valueCollector) {
			return true;
		}

		/**
		 * ��ü��ָ�궨��
		 */
		protected final PerformanceIndexDeclare getDeclare() {
			return this.declare;
		}

		protected PerformanceProvider(String name) {
			super(name);
		}
	}

	/**
	 * ��������ִ����
	 * 
	 * @author Jeff Tang
	 * 
	 */
	protected abstract class CaseTester extends ServiceBase<Context>.CaseTester {
		/**
		 * ���췽��
		 * 
		 * @param order
		 *            ����������˳���,����ִ��ʱȷ���Ⱥ�˳��
		 */
		public CaseTester(String code) {
			super(code);
		}
	}
}
