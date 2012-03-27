package org.eclipse.jt.core.model;

import org.eclipse.jt.core.Context;
import org.eclipse.jt.core.None;
import org.eclipse.jt.core.def.model.ModelActionDefine;
import org.eclipse.jt.core.def.model.ModelConstraintDefine;
import org.eclipse.jt.core.def.model.ModelConstructorDefine;
import org.eclipse.jt.core.def.model.ModelDeclarator;
import org.eclipse.jt.core.def.model.ModelObjSourceDefine;
import org.eclipse.jt.core.def.model.ModelPropAccessDefine;
import org.eclipse.jt.core.def.model.ModelPropertyDefine;
import org.eclipse.jt.core.impl.ModelServiceBase;
import org.eclipse.jt.core.impl.ServiceBase;
import org.eclipse.jt.core.invoke.Event;
import org.eclipse.jt.core.invoke.SimpleTask;
import org.eclipse.jt.core.invoke.Task;

/**
 * ģ�͹�����������
 * 
 * @author Jeff Tang
 * 
 * @param <TMO>
 *            ģ��ʵ���������
 * @param <TDeclarer>
 *            ģ��������
 */
public abstract class ModelService<TMO> extends ModelServiceBase<TMO> {
	// @Override
	// public final SpaceToken getSite() {
	// return super.getSite();
	// }
	//
	// @Override
	// public final SpaceToken getSpace() {
	// return super.getSpace();
	// }
	@Override
	public final Class<TMO> getMOClass() {
		return super.getMOClass();
	}

	public abstract class ModelActionHandler<TAO> extends
	        ModelServiceBase<TMO>.ModelActionHandler<TAO> {
		@Override
		public final Class<TAO> getAOClass() {
			return super.getAOClass();
		}
	}

	public abstract class ModelObjProvider<TAO> extends
	        ModelServiceBase<TMO>.ModelObjProvider<TAO> {
		@Override
		public final Class<TAO> getAOClass() {
			return super.getAOClass();
		}
	}

	public abstract class ModelConstructor<TAO> extends
	        ModelServiceBase<TMO>.ModelConstructor<TAO> {
		@Override
		public final Class<TAO> getAOClass() {
			return super.getAOClass();
		}
	}

	public abstract class ModelConstraintChecker extends
	        ModelServiceBase<TMO>.ModelConstraintChecker {

	}

	public abstract class ModelPropertyAccessor extends
	        ModelServiceBase<TMO>.ModelPropertyAccessor {

	}

	protected abstract class ModelServiceBinder<TModelDeclaror extends ModelDeclarator<TMO>>
	        extends ModelServiceBase<TMO>.ModelServiceBinder<TModelDeclaror> {
		/**
		 * ��ģ�ͷ�������Է�������ģ�����Զ����ĳ������������ <br>
		 * ����:<br>
		 * <code>binder.bind(declaror.somePropertyDefine.getSetterInfo(),SomeModelPropertyAccessor.class);</code>
		 * <br>
		 * <code>binder.bind(declaror.somePropertyDefine.getGetterInfo(),SomeModelPropertyAccessor.class);</code>
		 * <br>
		 * 
		 * @param propertyAccessor
		 *            ���Է���������
		 * @param accessorClass
		 *            ģ�ͷ������Է�������
		 */
		protected final void bind(ModelPropAccessDefine propAccessDefine,
		        Class<? extends ModelPropertyAccessor> accessorClass) {
			super.internalBind(propAccessDefine, accessorClass);
		}

		/**
		 * ��ģ�ͷ�������Է�������ģ�����Զ����ϣ��൱�ڽ���д���������� <br>
		 * ����:<br>
		 * <code>binder.bind(declaror.somePropertyDefine,SomeModelPropertyAccessor.class);</code>
		 * 
		 * @param propertyAccessor
		 *            ���Է���������
		 * @param accessorClass
		 *            ģ�ͷ������Է�������
		 */
		protected final void bind(ModelPropertyDefine property,
		        Class<? extends ModelPropertyAccessor> accessorClass) {
			super.internalBind(property, accessorClass);
		}

		protected final void bind(ModelConstructorDefine constructor,
		        Class<? extends ModelConstructor<?>> constructorClass) {
			super.internalBind(constructor, constructorClass);
		}

		protected final void bind(ModelActionDefine action,
		        Class<? extends ModelActionHandler<?>> handlerClass) {
			super.internalBind(action, handlerClass);
		}

		protected final void bind(ModelConstraintDefine constraint,
		        Class<? extends ModelConstraintChecker> constraintClass) {
			super.internalBind(constraint, constraintClass);
		}

		protected final void bind(ModelObjSourceDefine source,
		        Class<? extends ModelConstraintChecker> constraintClass) {
			super.internalBind(source, constraintClass);
		}

		@Override
		protected abstract void doBind(TModelDeclaror declaror);
	}

	public ModelService(String title) {
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
	 * ����ṩ�������ڷ���һ�������
	 */
	protected abstract class ResultListProvider<TResult> extends
	        ServiceBase<Context>.ResultListProvider<TResult> {
		// nothing to do.
	}

	/**
	 * ��������ṩ�������ڸ���ָ������������һ�������
	 */
	protected abstract class OneKeyResultListProvider<TResult, TKey> extends
	        ServiceBase<Context>.OneKeyResultListProvider<TResult, TKey> {
		// nothing to do.
	}

	/**
	 * ˫������ṩ�������ڸ���ָ������������һ�������
	 */
	protected abstract class TwoKeyResultListProvider<TResult, TKey1, TKey2>
	        extends
	        ServiceBase<Context>.TwoKeyResultListProvider<TResult, TKey1, TKey2> {
		// nothing to do.
	}

	/**
	 * ��������ṩ�������ڸ���ָ������������һ�������
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
	 * ��������ִ����
	 * 
	 * @author Jeff Tang
	 * 
	 */
	protected abstract class CaseTester extends ServiceBase<Context>.CaseTester {
		/**
		 * ���췽��
		 * 
		 * @param orders
		 *            ����������˳�����,����ִ��ʱȷ���Ⱥ�˳��
		 */
		public CaseTester(String code) {
			super(code);
		}
	}
}
