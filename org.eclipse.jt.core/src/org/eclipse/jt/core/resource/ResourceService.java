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
 * ��Դ������
 * 
 * @author Jeff Tang
 * 
 * @param <TFacade>
 *            ��Դ��ۣ�����Դʵ���ṩ��ֻ���ӿ�
 * @param <TModifier>
 *            ��Դ�޸������ȿ��������޸���Դ�Ľӿڻ������ͣ��󲿷�ʱ��ʹ����Դ��ʵ������
 * @param <TKeysHolder>
 *            ��Դ��Դ���ȿ��Դ��еõ���Դ�ļ���ֵ�Ľӿڻ������ͣ��󲿷�ʱ��ʹ����Դ��ʵ������
 * @param <TResourceMetaData>
 *            ��Դԭ���ݣ���Ҫԭ���ݵ���Դ��������Ҫָ��������ָ��ΪObject
 */
public abstract class ResourceService<TFacade, TImpl extends TFacade, TKeysHolder>
		extends ResourceServiceBase<TFacade, TImpl, TKeysHolder> {

	/**
	 * ��ʼ����Դ�������Դ
	 * 
	 * @param context
	 *            ������
	 * @param initializer
	 *            ��Դ��ʼ��
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
	 * ���췽�����ṩ������ָ������ı�������<br>
	 * ���磺
	 * 
	 * <pre>
	 * class DeptResourceService extends ResourceService {
	 * 	DeptResourceService() {
	 * 		super(&quot;����&quot;);
	 * 	}
	 * }
	 * </pre>
	 * 
	 * @param title
	 *            ָ����Դ����ı��⣬��Ϊ��ܼ�غ͹�����Щ����ʱ�÷����׶��ı�ʶ��
	 */
	protected ResourceService(String title) {
		super(title, ResourceKind.SINGLETON_IN_SITE);
	}

	/**
	 * ���췽�����ṩ������ָ������ı�������<br>
	 * ���磺
	 * 
	 * <pre>
	 * class DeptResourceService extends ResourceService {
	 * 	DeptResourceService() {
	 * 		super(&quot;����&quot;, ResourceKind.SINGLETON_IN_GLOBAL);
	 * 	}
	 * }
	 * </pre>
	 * 
	 * @param title
	 *            ָ������ԴĬ�����ı��⣬��Ϊ��ܼ�غ͹�����Щ����ʱ�÷����׶��ı�ʶ��
	 * @param kind
	 *            ��Դ���
	 */
	protected ResourceService(String title, ResourceKind kind) {
		super(title, kind);
	}

	/**
	 * ע����Դ���
	 * 
	 * @param category
	 *            ��𣬿���ΪNone.NONE,��ʾĬ�ϵ����
	 * @param title
	 *            ָ��ע����Դ���ı��⣬��Ϊ��ܼ�غ͹�����Щ����ʱ�÷����׶��ı�ʶ��
	 */
	@Override
	protected final void registerCategory(Object category, String title) {
		super.registerCategory(category, title);
	}

	/**
	 * ע����Դ���
	 * 
	 * @param category
	 *            ��Դ���
	 */
	@Override
	protected final void unRegisterCategory(Object category) {
		super.unRegisterCategory(category);
	}

	/**
	 * �÷��������ڷ�����Ȩ��Դǰ������Դ�����Ļ������е����� ͨ���÷������еĲ��ַ������Բ��������ܽ��лع���������ʹ�á�
	 */
	@Override
	protected void beforeAccessAuthorityResource(Context context) {

	}

	/**
	 * �÷��������ڷ�����Ȩ��Դ�󣬶���Դ�����Ļ������е����� ͨ���÷������еĲ��ַ������Բ��������ܽ��лع���������ʹ�á�
	 */
	@Override
	protected void endAccessAuthorityResource(Context context) {

	}

	/**
	 * ����ӳ��ӿڣ�����ָ���ο���Դ֮���Ȩ��Ӱ��ʱ������ӳ���ϵ
	 * 
	 * @author Jeff Tang
	 * 
	 * @param <TResourceFacade>
	 *            ����֤��Դ�����
	 * @param <TMapToResourceFacade>
	 *            Ӱ��Ȩ�޵���Դ�����
	 */
	public interface OperationMap<TResourceFacade, TMapToResourceFacade> {
		public void map(
				Enum<? extends Operation<? super TResourceFacade>> operation,
				Enum<? extends Operation<? super TMapToResourceFacade>> mapToOperation);
	}

	/**
	 * ��Դ�ο����壬���屾��Դ�ο������ã���һ����Դ
	 * 
	 * @author Jeff Tang
	 * 
	 * @param <TRefFacade>
	 *            ��Դ����
	 */
	protected abstract class ResourceReference<TRefFacade> extends
			ResourceServiceBase.ResourceReference<TRefFacade, TFacade> {

		/**
		 * �����������뱻������֮���Ȩ��ӳ���ϵ��<br>
		 * ͨ���ý������ӳ���ϵ��ʾ�������ߵ�Ȩ�����ý�Ӱ�������ߵ�Ȩ����֤
		 */
		@Override
		protected void authMapOperation(
				OperationMap<TFacade, TRefFacade> operationMap) {
		}

		/**
		 * û��ָ��Ȩ��Ӱ��Ĺ��췽��
		 */
		public ResourceReference() {
			super(null, ResourceService.this);
		}
	}

	/**
	 * ��Դ���ο����壬���屾��Դ����һ����Դ�ο������ã�
	 * 
	 * @author Jeff Tang
	 * @param <TReferredByFacade>
	 *            ���ñ���Դ����Դ���������
	 */
	protected abstract class ReferredByResource<TReferredByFacade> extends
			ResourceServiceBase.ResourceReference<TFacade, TReferredByFacade> {

		/**
		 * �����������뱻������֮���Ȩ��ӳ���ϵ��<br>
		 * ͨ���ý������ӳ���ϵ��ʾ�������ߵ�Ȩ�����ý�Ӱ�������ߵ�Ȩ����֤
		 */
		@Override
		protected void authMapOperation(
				OperationMap<TReferredByFacade, TFacade> operationMap) {
		}

		/**
		 * û��ָ��Ȩ��Ӱ��Ĺ��췽��
		 */
		public ReferredByResource() {
			super(ResourceService.this, null);
		}
	}

	/**
	 * �¼�������
	 */
	protected abstract class EventListener<TEvent extends Event>
			extends
			ServiceBase<ResourceContext<TFacade, TImpl, TKeysHolder>>.EventListener<TEvent> {
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
			extends
			ServiceBase<ResourceContext<TFacade, TImpl, TKeysHolder>>.TaskMethodHandler<TTask, TMethod> {
		protected TaskMethodHandler(TMethod first, TMethod... otherMethods) {
			super(first, otherMethods);
		}
	}

	/**
	 * ��������������
	 */
	protected abstract class SimpleTaskMethodHandler<TTask extends SimpleTask>
			extends
			ServiceBase<ResourceContext<TFacade, TImpl, TKeysHolder>>.TaskMethodHandler<TTask, None> {

		protected SimpleTaskMethodHandler() {
			super(None.NONE, null);
		}
	}

	/**
	 * ��������ṩ��
	 */
	protected abstract class ResultProvider<TResult>
			extends
			ServiceBase<ResourceContext<TFacade, TImpl, TKeysHolder>>.ResultProvider<TResult> {
		// nothing to do.
	}

	/**
	 * ��������ṩ��
	 */
	protected abstract class OneKeyResultProvider<TResult, TKey>
			extends
			ServiceBase<ResourceContext<TFacade, TImpl, TKeysHolder>>.OneKeyResultProvider<TResult, TKey> {
		// nothing to do.
	}

	/**
	 * ˫������ṩ��
	 */
	protected abstract class TwoKeyResultProvider<TResult, TKey1, TKey2>
			extends
			ServiceBase<ResourceContext<TFacade, TImpl, TKeysHolder>>.TwoKeyResultProvider<TResult, TKey1, TKey2> {
		// nothing to do.
	}

	/**
	 * ��������ṩ��
	 */
	protected abstract class ThreeKeyResultProvider<TResult, TKey1, TKey2, TKey3>
			extends
			ServiceBase<ResourceContext<TFacade, TImpl, TKeysHolder>>.ThreeKeyResultProvider<TResult, TKey1, TKey2, TKey3> {
		// nothing to do.
	}

	/**
	 * ����ṩ�������ڷ���һ�������
	 */
	protected abstract class ResultListProvider<TResult>
			extends
			ServiceBase<ResourceContext<TFacade, TImpl, TKeysHolder>>.ResultListProvider<TResult> {
		// nothing to do.
	}

	/**
	 * ��������ṩ�������ڸ���ָ������������һ�������
	 */
	protected abstract class OneKeyResultListProvider<TResult, TKey>
			extends
			ServiceBase<ResourceContext<TFacade, TImpl, TKeysHolder>>.OneKeyResultListProvider<TResult, TKey> {
		// nothing to do.
	}

	/**
	 * ˫������ṩ�������ڸ���ָ������������һ�������
	 */
	protected abstract class TwoKeyResultListProvider<TResult, TKey1, TKey2>
			extends
			ServiceBase<ResourceContext<TFacade, TImpl, TKeysHolder>>.TwoKeyResultListProvider<TResult, TKey1, TKey2> {
		// nothing to do.
	}

	/**
	 * ��������ṩ�������ڸ���ָ������������һ�������
	 */
	protected abstract class ThreeKeyResultListProvider<TResult, TKey1, TKey2, TKey3>
			extends
			ServiceBase<ResourceContext<TFacade, TImpl, TKeysHolder>>.ThreeKeyResultListProvider<TResult, TKey1, TKey2, TKey3> {
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
	 * �����Դʱ�����������ͬ��ֵ����Դ�Ѿ����ڣ���Ӧ�ò�ȡ�Ĳ��ԡ�
	 * 
	 * @author Jeff Tang
	 * @version 1.0
	 */
	public enum WhenExists {
		/**
		 * �׳��쳣��
		 */
		EXCEPTION,

		/**
		 * �滻�Ѿ����ڵ���Դ��ȱʡʱ���õĲ��ԣ���
		 */
		REPLACE,

		/**
		 * ���ԣ�������ӡ�
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
	 * ���ָ���ṩ��
	 * 
	 * @author Jeff Tang
	 * 
	 */
	protected abstract class PerformanceProvider<TPerformanceValueContainer extends PerformanceValueCollector<?>>
			extends
			ServiceBase<ResourceContext<TFacade, TImpl, TKeysHolder>>.PerformanceProvider<TPerformanceValueContainer> {
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
		protected boolean startMonitor(
				ResourceContext<TFacade, TImpl, TKeysHolder> context,
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
		protected void update(
				ResourceContext<TFacade, TImpl, TKeysHolder> context,
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
		protected boolean doCommand(
				ResourceContext<TFacade, TImpl, TKeysHolder> context,
				CommandDefine command, boolean testOrExecute) {
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
		protected void stopMonitor(
				ResourceContext<TFacade, TImpl, TKeysHolder> context,
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

	/**
	 * ��������Դ��ҪȨ����Ȩ����Դ�ṩ��<br>
	 * ���ṩ��������ΪGUID�����ṩ����<br>
	 * ����ͨ��ָ������ö�ٷ��Ͳ�����ָ������Դ��Ȩ�޲���
	 */
	protected abstract class AuthorizableResourceProvider<TOperationEnum extends Enum<? extends Operation<? super TFacade>>>
			extends
			ResourceServiceBase<TFacade, TImpl, TKeysHolder>.AuthorizableResourceProvider<TOperationEnum> {
		/**
		 * ����ĳ����Դ�ı�������Ȩ������ʹ��
		 */
		@Override
		protected abstract String getResourceTitle(TImpl resource,
				TKeysHolder keys);

		/**
		 * ������Դ��Դ���ص�ǰ�ṩ����Ӧ�ļ�
		 * 
		 * @param keys
		 *            ��Դ��Դ
		 * @return ���ص�ǰ�ṩ����Ӧ�ļ�
		 */
		@Override
		protected abstract GUID getKey1(TKeysHolder keys);

		/**
		 * ���췽��<br>
		 * ʹ�������Զ�����defaultCategoryID
		 */
		public AuthorizableResourceProvider() {
			super(null, false);
		}

		/**
		 * ���췽��<br>
		 * ʹ�������Զ�����defaultCategoryID
		 * 
		 * @param looseAuthPolicy
		 *            �Ƿ�Ӧ�ÿ���Ȩ�޿��Ʋ��ԣ�Ĭ��û��Ȩ�ޣ�
		 */
		public AuthorizableResourceProvider(boolean looseAuthPolicy) {
			super(null, looseAuthPolicy);
		}

		/**
		 * ���췽��<br>
		 * ָ��defaultCategoryID
		 * 
		 * @param defaultCategoryID
		 *            Ĭ�ϵ���Դ���ID,������Ϊnull��Empty
		 */
		public AuthorizableResourceProvider(GUID defaultCategoryID) {
			this(defaultCategoryID, false);
		}

		/**
		 * ���췽��<br>
		 * ָ��defaultCategoryID
		 * 
		 * @param defaultCategoryID
		 *            Ĭ�ϵ���Դ���ID,������Ϊnull��Empty
		 * @param looseAuthPolicy
		 *            �Ƿ�Ӧ�ÿ���Ȩ�޿��Ʋ��ԣ�Ĭ��û��Ȩ�ޣ�
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