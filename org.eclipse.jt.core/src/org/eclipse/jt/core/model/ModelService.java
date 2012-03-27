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
 * 模型管理器基础类
 * 
 * @author Jeff Tang
 * 
 * @param <TMO>
 *            模型实体对象类型
 * @param <TDeclarer>
 *            模型声明器
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
		 * 绑定模型服务的属性访问器到模型属性定义的某访问器定义上 <br>
		 * 例如:<br>
		 * <code>binder.bind(declaror.somePropertyDefine.getSetterInfo(),SomeModelPropertyAccessor.class);</code>
		 * <br>
		 * <code>binder.bind(declaror.somePropertyDefine.getGetterInfo(),SomeModelPropertyAccessor.class);</code>
		 * <br>
		 * 
		 * @param propertyAccessor
		 *            属性访问器定义
		 * @param accessorClass
		 *            模型服务属性访问器类
		 */
		protected final void bind(ModelPropAccessDefine propAccessDefine,
		        Class<? extends ModelPropertyAccessor> accessorClass) {
			super.internalBind(propAccessDefine, accessorClass);
		}

		/**
		 * 绑定模型服务的属性访问器到模型属性定义上，相当于将读写方法都绑定了 <br>
		 * 例如:<br>
		 * <code>binder.bind(declaror.somePropertyDefine,SomeModelPropertyAccessor.class);</code>
		 * 
		 * @param propertyAccessor
		 *            属性访问器定义
		 * @param accessorClass
		 *            模型服务属性访问器类
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
	 * 结果提供器，用于返回一个结果集
	 */
	protected abstract class ResultListProvider<TResult> extends
	        ServiceBase<Context>.ResultListProvider<TResult> {
		// nothing to do.
	}

	/**
	 * 单键结果提供器，用于根据指定的条件返回一个结果集
	 */
	protected abstract class OneKeyResultListProvider<TResult, TKey> extends
	        ServiceBase<Context>.OneKeyResultListProvider<TResult, TKey> {
		// nothing to do.
	}

	/**
	 * 双键结果提供器，用于根据指定的条件返回一个结果集
	 */
	protected abstract class TwoKeyResultListProvider<TResult, TKey1, TKey2>
	        extends
	        ServiceBase<Context>.TwoKeyResultListProvider<TResult, TKey1, TKey2> {
		// nothing to do.
	}

	/**
	 * 三键结果提供器，用于根据指定的条件返回一个结果集
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
	 * 测试用例执行器
	 * 
	 * @author Jeff Tang
	 * 
	 */
	protected abstract class CaseTester extends ServiceBase<Context>.CaseTester {
		/**
		 * 构造方法
		 * 
		 * @param orders
		 *            测试用例的顺序码段,便于执行时确定先后顺序
		 */
		public CaseTester(String code) {
			super(code);
		}
	}
}
