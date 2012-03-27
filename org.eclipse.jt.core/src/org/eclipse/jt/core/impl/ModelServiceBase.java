package org.eclipse.jt.core.impl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jt.core.Context;
import org.eclipse.jt.core.None;
import org.eclipse.jt.core.def.model.ModelActionDefine;
import org.eclipse.jt.core.def.model.ModelConstraintDefine;
import org.eclipse.jt.core.def.model.ModelConstructorDefine;
import org.eclipse.jt.core.def.model.ModelDeclarator;
import org.eclipse.jt.core.def.model.ModelDeclare;
import org.eclipse.jt.core.def.model.ModelInvokeDefine;
import org.eclipse.jt.core.def.model.ModelInvokeStage;
import org.eclipse.jt.core.def.model.ModelObjSourceDefine;
import org.eclipse.jt.core.def.model.ModelPropAccessDefine;
import org.eclipse.jt.core.def.model.ModelPropertyDefine;
import org.eclipse.jt.core.misc.ExceptionCatcher;
import org.eclipse.jt.core.misc.ObjectBuilder;
import org.eclipse.jt.core.misc.TypeArgFinder;
import org.eclipse.jt.core.service.Publish.Mode;
import org.eclipse.jt.core.type.DataType;
import org.eclipse.jt.core.type.GUID;
import org.eclipse.jt.core.type.TypeFactory;


/**
 * ģ�͹�����
 * 
 * @author Jeff Tang
 * 
 * @param <TMO>
 *            ģ��ʵ���������
 * @param <TDeclarer>
 *            ģ��������
 */
public abstract class ModelServiceBase<TMO> extends ServiceBase<Context> {

	@SuppressWarnings("unchecked")
	public ModelServiceBase(String title) {
		super(title);
		this.moClass = (Class<TMO>) TypeArgFinder.get(this.getClass(),
		        ModelServiceBase.class, 0);
	}

	public Class<TMO> getMOClass() {
		return this.moClass;
	}

	/**
	 * ������
	 * 
	 * @author Jeff Tang
	 * 
	 * @param <TModelDeclaror>
	 */
	protected abstract class ModelServiceBinder<TModelDeclaror extends ModelDeclarator<TMO>> {
		ModelServiceBinder<?> next;
		final Class<TModelDeclaror> declarorClass;

		@SuppressWarnings("unchecked")
		public ModelServiceBinder() {
			this.declarorClass = (Class) TypeArgFinder.get(this.getClass(),
			        ModelServiceBinder.class, 0);
		}

		protected final void internalBind(
		        ModelPropAccessDefine propAccessDefine, Class<?> accessorClass) {
			if (propAccessDefine == null || accessorClass == null) {
				throw new NullPointerException();
			}
			ModelPropertyAccessor accessor = ModelServiceBase.this.propertyAccessors
			        .get(accessorClass);
			if (accessor == null) {
				throw new IllegalArgumentException("�Ҳ���" + accessorClass
				        + "��Ӧ�����Է�����");
			}
			((ModelPropAccessDefineImpl) propAccessDefine)
			        .internalSetAccessor(accessor);
		}

		protected final void internalBind(ModelPropertyDefine property,
		        Class<?> accessorClass) {
			if (property == null || accessorClass == null) {
				throw new NullPointerException();
			}
			ModelPropertyAccessor accessor = ModelServiceBase.this.propertyAccessors
			        .get(accessorClass);
			if (accessor == null) {
				throw new IllegalArgumentException("�Ҳ���" + accessorClass
				        + "��Ӧ�����Է�����");
			}
			ModelPropertyDefineImpl prop = (ModelPropertyDefineImpl) property;
			prop.getGetterInfo().internalSetAccessor(accessor);
			prop.getSetterInfo().internalSetAccessor(accessor);
		}

		protected final void internalBind(ModelConstructorDefine constructor,
		        Class<?> constructorClass) {
			if (constructor == null || constructorClass == null) {
				throw new NullPointerException();
			}
			ModelConstructor<?> handler = ModelServiceBase.this.constructors
			        .get(constructorClass);
			if (handler == null) {
				throw new IllegalArgumentException("�Ҳ���" + constructorClass
				        + "��Ӧ�Ĺ�����");
			}
			((ModelConstructorDefineImpl) constructor)
			        .internalSetConstructor(handler);
		}

		protected final void internalBind(ModelActionDefine action,
		        Class<?> handlerClass) {
			if (action == null || handlerClass == null) {
				throw new NullPointerException();
			}
			ModelActionHandler<?> handler = ModelServiceBase.this.actionHandlers
			        .get(handlerClass);
			if (handler == null) {
				throw new IllegalArgumentException("�Ҳ���" + handlerClass
				        + "��Ӧ�Ķ���������");
			}
			((ModelActionDefineImpl) action).internalSetHandler(handler);
		}

		protected final void internalBind(ModelConstraintDefine constraint,
		        Class<?> constraintClass) {
			if (constraint == null || constraintClass == null) {
				throw new NullPointerException();
			}
			ModelConstraintChecker checker = ModelServiceBase.this.constraintCheckers
			        .get(constraintClass);
			if (checker == null) {
				throw new IllegalArgumentException("�Ҳ���" + constraintClass
				        + "��Ӧ��Լ�������");
			}
			((ModelConstraintDefineImpl) constraint)
			        .internalSetChecker(checker);
		}

		protected final void internalBind(ModelObjSourceDefine source,
		        Class<?> providerClass) {
			if (source == null || providerClass == null) {
				throw new NullPointerException();
			}
			ModelObjProvider<?> provider = ModelServiceBase.this.objProviders
			        .get(providerClass);
			if (provider == null) {
				throw new IllegalArgumentException("�Ҳ���" + providerClass
				        + "��Ӧ��ģ��ʵ���ṩ��");
			}
			((ModelObjSourceDefineImpl) source).internalSetProvider(provider);
		}

		protected abstract void doBind(TModelDeclaror declaror);

		protected void doBind(TModelDeclaror declaror, ModelDeclare modelDeclare) {
			this.doBind(declaror);
		}
	}

	private ModelServiceBinder<?> bindlers;

	// ////////////////////////////////////////////////////////
	// ////////// ģ�Ͷ���
	// ////////////////////////////////////////////////////////
	static abstract class ModelInvokee {

		abstract ModelServiceBase<?> getService();

	}

	/**
	 * ģ�Ͷ�����Ӧ��
	 * 
	 * @author Jeff Tang
	 * 
	 */
	public abstract class ModelActionHandler<TAO> extends ModelInvokee {
		/**
		 * ���ظö�����Ӧ���Ƿ����
		 * 
		 * @param context
		 *            ����������
		 * @param mo
		 *            ģ��ʵ������
		 * @return �����Ƿ����
		 */
		protected boolean isValid(Context context, TMO mo,
		        ModelActionDefine action) {
			return true;
		}

		/**
		 * ��Ӧģ�Ͷ���
		 * 
		 * @param context
		 *            ������
		 * @param mo
		 *            ģ��ʵ������
		 * @param ao
		 *            ����ʵ������
		 * @param notifier
		 *            ����ʧЧ֪ͨ��
		 * @param action
		 *            ģ�Ͷ�������
		 * @param trigger
		 *            �����Ķ��ĵ��ã������ֱ�Ӵ�������Ϊnull
		 * @param triggerAO
		 *            �������õĲ��������޲�����<code>stage==ModelInvokeStage.CHANGED</code>
		 *            ʱ��ֵΪnull
		 * @param value
		 *            ������������������ʱ���������õ�ֵ
		 * @param stage
		 *            �����Ľ׶�
		 */
		protected abstract void doAction(Context context, TMO mo, TAO ao,
		        ModelActionDefine action, ModelInvokeDefine trigger,
		        Object triggerAO, Object value, ModelInvokeStage stage)
		        throws Throwable;

		// ///////////////////////////////////////
		// ////�ڲ�����
		// ///////////////////////////////////////
		final Class<TAO> aoClass;

		public Class<TAO> getAOClass() {
			return this.aoClass;
		}

		@SuppressWarnings("unchecked")
		protected ModelActionHandler() {
			this.aoClass = (Class<TAO>) TypeArgFinder.get(this.getClass(),
			        ModelActionHandler.class, 0);
		}

		@Override
		final ModelServiceBase<?> getService() {
			return ModelServiceBase.this;
		}
	}

	// ////////////////////////////////////////////////////////
	// ////////// ģ�͹�����
	// ////////////////////////////////////////////////////////
	/**
	 * ģ�͹���������
	 * 
	 * @author Jeff Tang
	 * 
	 */
	public abstract class ModelConstructor<TAO> extends ModelInvokee {
		/**
		 * ��Ӧģ�Ͷ���
		 * 
		 * @param context
		 *            ������
		 * @param mo
		 *            ģ��ʵ������
		 * @param ao
		 *            ����ʵ������
		 * @param notifier
		 *            ����ʧЧ֪ͨ��
		 * @param constructor
		 *            ģ�͹���������
		 */
		protected abstract void doCreate(Context context, TMO mo, TAO ao,
		        ModelConstructorDefine constructor) throws Throwable;

		/**
		 * ��ȡ�ù������Ĳ���
		 * 
		 * @param context
		 *            ���°���
		 * @param mo
		 *            ģ��ʵ������
		 * @param ao
		 *            ����ʵ������
		 * @param constructor
		 *            ģ�͹���������
		 */
		protected void doExtract(Context context, TMO mo, TAO ao,
		        ModelConstructorDefine constructor) throws Throwable {
			throw new UnsupportedOperationException();
		}

		// ///////////////////////////////////////
		// ////�ڲ�����
		// ///////////////////////////////////////
		final Class<TAO> aoClass;

		public Class<TAO> getAOClass() {
			return this.aoClass;
		}

		@SuppressWarnings("unchecked")
		protected ModelConstructor() {
			this.aoClass = (Class<TAO>) TypeArgFinder.get(this.getClass(),
			        ModelActionHandler.class, 0);

		}

		@Override
		final ModelServiceBase<?> getService() {
			return ModelServiceBase.this;
		}
	}

	// ////////////////////////////////////////////////////////
	// ////////// ģ������
	// ////////////////////////////////////////////////////////
	/**
	 * ģ�����Է�����
	 * 
	 * @author Jeff Tang
	 * 
	 * @param <TAO>
	 *            ���Բ�������
	 */
	public abstract class ModelPropertyAccessor extends ModelInvokee {
		protected void doSetBoolean(Context context, TMO mo, boolean value,
		        ModelPropertyDefine property) throws Throwable {
			throw new UnsupportedOperationException();
		}

		protected boolean doGetBoolean(Context context, TMO mo,
		        ModelPropertyDefine property) throws Throwable {
			throw new UnsupportedOperationException();
		}

		protected void doSetByte(Context context, TMO mo, byte value,
		        ModelPropertyDefine property) throws Throwable {
			throw new UnsupportedOperationException();
		}

		protected byte doGetByte(Context context, TMO mo,
		        ModelPropertyDefine property) throws Throwable {
			throw new UnsupportedOperationException();
		}

		protected void doSetShort(Context context, TMO mo, short value,
		        ModelPropertyDefine property) throws Throwable {
			throw new UnsupportedOperationException();
		}

		protected short doGetShort(Context context, TMO mo,
		        ModelPropertyDefine property) throws Throwable {
			throw new UnsupportedOperationException();
		}

		protected void doSetInt(Context context, TMO mo, int value,
		        ModelPropertyDefine property) throws Throwable {
			throw new UnsupportedOperationException();
		}

		protected int doGetInt(Context context, TMO mo,
		        ModelPropertyDefine property) throws Throwable {
			throw new UnsupportedOperationException();
		}

		protected void doSetLong(Context context, TMO mo, long value,
		        ModelPropertyDefine property) throws Throwable {
			throw new UnsupportedOperationException();
		}

		protected long doGetLong(Context context, TMO mo,
		        ModelPropertyDefine property) throws Throwable {
			throw new UnsupportedOperationException();
		}

		protected void doSetDate(Context context, TMO mo, long value,
		        ModelPropertyDefine property) throws Throwable {
			throw new UnsupportedOperationException();
		}

		protected long doGetDate(Context context, TMO mo,
		        ModelPropertyDefine property) throws Throwable {
			throw new UnsupportedOperationException();
		}

		protected void doSetDouble(Context context, TMO mo, double value,
		        ModelPropertyDefine property) throws Throwable {
			throw new UnsupportedOperationException();
		}

		protected double doGetDouble(Context context, TMO mo,
		        ModelPropertyDefine property) throws Throwable {
			throw new UnsupportedOperationException();
		}

		protected void doSetFloat(Context context, TMO mo, float value,
		        ModelPropertyDefine property) throws Throwable {
			throw new UnsupportedOperationException();
		}

		protected float doGetFloat(Context context, TMO mo,
		        ModelPropertyDefine property) throws Throwable {
			throw new UnsupportedOperationException();
		}

		protected void doSetString(Context context, TMO mo, String value,
		        ModelPropertyDefine property) throws Throwable {
			throw new UnsupportedOperationException();
		}

		protected String doGetString(Context context, TMO mo,
		        ModelPropertyDefine property) throws Throwable {
			throw new UnsupportedOperationException();
		}

		protected void doSetGUID(Context context, TMO mo, GUID value,
		        ModelPropertyDefine property) throws Throwable {
			throw new UnsupportedOperationException();
		}

		protected GUID doGetGUID(Context context, TMO mo,
		        ModelPropertyDefine property) throws Throwable {
			throw new UnsupportedOperationException();
		}

		protected void doSetBytes(Context context, TMO mo, byte[] value,
		        ModelPropertyDefine property) throws Throwable {
			throw new UnsupportedOperationException();
		}

		protected byte[] doGetBytes(Context context, TMO mo,
		        ModelPropertyDefine property) throws Throwable {
			throw new UnsupportedOperationException();
		}

		protected void doSetObject(Context context, TMO mo, Object value,
		        ModelPropertyDefine property) throws Throwable {
			throw new UnsupportedOperationException();
		}

		protected Object doGetObject(Context context, TMO mo,
		        ModelPropertyDefine property) throws Throwable {
			throw new UnsupportedOperationException();
		}

		// ///////////////////////////////////////////////////
		// /////// ��ȡ��
		// ///////////////////////////////////////////////////
		/**
		 * ����ģ��ʵ����ǰ�������Ƿ����Ч
		 * 
		 * @param context
		 *            ������
		 * @param mo
		 *            ģ��ʵ������
		 * @param property
		 *            ģ�����Զ���
		 * @return ����ģ��ʵ����ǰ�������Ƿ����Ч
		 */
		protected boolean isGetterValid(Context context, TMO mo,
		        ModelPropertyDefine property) {
			return true;
		}

		/**
		 * ����ģ��ʵ����ǰ�������Ƿ�д��Ч
		 * 
		 * @param context
		 *            ������
		 * @param mo
		 *            ģ��ʵ������
		 * @param property
		 *            ģ�����Զ���
		 * @return ����ģ��ʵ����ǰ�������Ƿ�д��Ч
		 */
		protected boolean isSetterValid(Context context, TMO mo,
		        ModelPropertyDefine property) {
			return true;
		}

		// ///////////////////////////////////////
		// ////�ڲ�����
		// ///////////////////////////////////////
		final DataType[] setSupports;
		final DataType[] getSupports;

		public ModelPropertyAccessor() {
			List<DataType> setSupports = null;
			List<DataType> getSupports = null;
			for (Class<?> clazz = this.getClass(); clazz != ModelPropertyAccessor.class; clazz = clazz
			        .getSuperclass()) {
				for (Method method : clazz.getDeclaredMethods()) {
					String name = method.getName();
					if (name.startsWith("doSet")) {
						DataType type = TypeFactory.rootTypeOf(name
						        .substring(5));
						if (type != UnknownType.TYPE) {
							if (setSupports == null) {
								setSupports = new ArrayList<DataType>();
							}
							if (!setSupports.contains(type)) {
								setSupports.add(type);
							}
						}
					} else if (name.startsWith("doGet")) {
						DataType type = TypeFactory.rootTypeOf(name
						        .substring(5));
						if (type != UnknownType.TYPE) {
							if (getSupports == null) {
								getSupports = new ArrayList<DataType>();
							}
							if (!getSupports.contains(type)) {
								getSupports.add(type);
							}
						}
					}
				}
			}
			if (setSupports != null) {
				this.setSupports = setSupports.toArray(new DataType[setSupports
				        .size()]);
			} else {
				this.setSupports = DataTypeBase.emptyArray;
			}
			if (getSupports != null) {
				this.getSupports = getSupports.toArray(new DataType[getSupports
				        .size()]);
			} else {
				this.getSupports = DataTypeBase.emptyArray;
			}
		}

		final boolean canSet(DataType type) {
			for (DataType sType : this.setSupports) {
				if (sType == type || sType == type.getRootType()) {
					return true;
				}
			}
			return false;
		}

		final boolean canGet(DataType type) {
			for (DataType sType : this.setSupports) {
				if (sType == type || sType == type.getRootType()) {
					return true;
				}
			}
			return false;
		}

		@Override
		final ModelServiceBase<?> getService() {
			return ModelServiceBase.this;
		}
	}

	// ////////////////////////////////////////////////////////
	// ////////// ģ��Լ��
	// ////////////////////////////////////////////////////////

	/**
	 * ģ�͵�Լ�������
	 * 
	 * @author Jeff Tang
	 * 
	 */
	public abstract class ModelConstraintChecker extends ModelInvokee {
		/**
		 * ���Լ��
		 * 
		 * @param context
		 *            ������
		 * @param mo
		 *            ģ��ʵ������
		 * @param constraint
		 *            Լ������
		 * @param trigger
		 *            ����Լ���ĵ���
		 * @param triggerAO
		 *            �������õĲ��������޲�����<code>stage==ModelInvokeStage.CHANGED</code>
		 *            ʱ��ֵΪ<code>None</code>
		 * @param value
		 *            ��������Ϊ����������ʱ�����õ�����ֵ
		 * @param stage
		 *            �����Ľ׶�
		 */
		protected abstract void doCheck(Context context, TMO mo,
		        ModelConstraintDefine constraint, ModelInvokeDefine trigger,
		        Object triggerAO, Object value, ModelInvokeStage stage)
		        throws Throwable;

		// ///////////////////////////////////////
		// ////�ڲ�����
		// ///////////////////////////////////////

		@Override
		final ModelServiceBase<?> getService() {
			return ModelServiceBase.this;
		}
	}

	// ////////////////////////////////////////////////////////
	// ////////// ģ��ʵ��Դ
	// ////////////////////////////////////////////////////////

	public abstract class ModelObjProvider<TAO> extends ModelInvokee {
		/**
		 * Ҫ���ṩ���ṩ���صĸ���������֧�ַ�ҳ����֧�ַ�ҳʱ����С�������
		 * 
		 * @param context
		 *            ������
		 * @param ao
		 *            ����ʵ��
		 * @param source
		 *            ʵ��Դ����
		 * @return ����ĳ�����¿��Է��ص�ʵ�����������С�����ʾ��֧��
		 * @throws Throwable
		 */
		protected int moCountOf(Context context, TAO ao,
		        ModelObjSourceDefine source) throws Throwable {
			return -1;
		}

		/**
		 * ���ģ��ʵ���б�
		 * 
		 * @param context
		 *            ������
		 * @param ao
		 *            ����ʵ��
		 * @param source
		 *            ʵ��Դ����
		 * @param mos
		 *            �ȴ�����ʵ���б�
		 */
		protected abstract void provide(Context context, TAO ao,
		        ModelObjSourceDefine source, List<? extends TMO> mos,
		        int offset, int count, ObjectBuilder<TMO> moFactory)
		        throws Throwable;

		@SuppressWarnings("unchecked")
		public ModelObjProvider() {
			this.aoClass = (Class<TAO>) TypeArgFinder.get(this.getClass(),
			        ModelObjProvider.class, 0);
		}

		// ///////////////////////////////////////
		final Class<TAO> aoClass;

		public Class<TAO> getAOClass() {
			return this.aoClass;
		}

		@Override
		final ModelServiceBase<?> getService() {
			return ModelServiceBase.this;
		}
	}

	// ////////////////////////////////////////////////////////////////////////
	// ////// ģ�ʹ�������ָ��
	// ////////////////////////////////////////////////////////////////////////
	final Class<TMO> moClass;
	final Map<Class<?>, ModelActionHandler<?>> actionHandlers = new HashMap<Class<?>, ModelActionHandler<?>>(
	        0);
	final Map<Class<?>, ModelPropertyAccessor> propertyAccessors = new HashMap<Class<?>, ModelPropertyAccessor>(
	        0);
	final Map<Class<?>, ModelConstructor<?>> constructors = new HashMap<Class<?>, ModelConstructor<?>>(
	        0);
	final Map<Class<?>, ModelConstraintChecker> constraintCheckers = new HashMap<Class<?>, ModelConstraintChecker>(
	        0);
	final Map<Class<?>, ModelObjProvider<?>> objProviders = new HashMap<Class<?>, ModelObjProvider<?>>(
	        0);

	static abstract class ModelInvokeeFiller {
		@SuppressWarnings("unchecked")
		public abstract void fill(ModelServiceBase<?> modelService, List list,
		        Class<?> aoClass);

		public abstract Map<Class<?>, ? extends ModelInvokee> getMap(
		        ModelServiceBase<?> modelService);

		public abstract boolean matchFillModelInvokee(Object key1, Object key2,
		        Object key3);

		final Class<?> baseClass;

		@SuppressWarnings("unchecked")
		public final void regModelInvokee(ModelServiceBase<?> modelService,
		        Class<?> invokeeClass) {
			((Map) this.getMap(modelService)).put(invokeeClass, modelService
			        .newObjectInNode(invokeeClass, null, null));
		}

		ModelInvokeeFiller(Class<?> baseClass) {
			this.baseClass = baseClass;
		}
	}

	private static final ModelInvokeeFiller actionHandlerFiller = new ModelInvokeeFiller(
	        ModelServiceBase.ModelActionHandler.class) {
		@Override
		@SuppressWarnings("unchecked")
		public void fill(ModelServiceBase<?> modelService, List list,
		        Class<?> aoClass) {
			if (!modelService.actionHandlers.isEmpty()) {
				for (ModelServiceBase<?>.ModelActionHandler<?> h : modelService.actionHandlers
				        .values()) {
					if (aoClass == null || h.aoClass == None.class
					        || h.aoClass.isAssignableFrom(aoClass)) {
						list.add(h);
					}
				}
			}
		}

		@Override
		public boolean matchFillModelInvokee(Object key1, Object key2,
		        Object key3) {
			if (key1 == null) {
				return key2 == null && key3 == null;
			} else if (key2 == null && key1 instanceof Class<?>) {
				return key3 == null;
			} else if (key3 == null && key1 instanceof Class<?>
			        && key2 instanceof Class<?>) {
				return true;
			} else {
				return false;
			}
		}

		@Override
		public Map<Class<?>, ? extends ModelInvokee> getMap(
		        ModelServiceBase<?> modelService) {
			return modelService.actionHandlers;
		}
	};
	private static final ModelInvokeeFiller propertyAccessorFiller = new ModelInvokeeFiller(
	        ModelServiceBase.ModelPropertyAccessor.class) {
		@Override
		@SuppressWarnings("unchecked")
		public void fill(ModelServiceBase<?> modelService, List list,
		        Class<?> aoClass) {
			if (!modelService.propertyAccessors.isEmpty()) {
				for (ModelInvokee p : modelService.propertyAccessors.values()) {
					list.add(p);
				}
			}
		}

		@Override
		public boolean matchFillModelInvokee(Object key1, Object key2,
		        Object key3) {
			if (key1 == null) {
				return key2 == null && key3 == null;
			} else if (key2 == null && key1 instanceof Class<?>) {
				return key3 == null;
			} else {
				return false;
			}
		}

		@Override
		public Map<Class<?>, ? extends ModelInvokee> getMap(
		        ModelServiceBase<?> modelService) {
			return modelService.propertyAccessors;
		}
	};
	private static final ModelInvokeeFiller constructorFiller = new ModelInvokeeFiller(
	        ModelServiceBase.ModelConstructor.class) {
		@Override
		@SuppressWarnings("unchecked")
		public void fill(ModelServiceBase<?> modelService, List list,
		        Class<?> aoClass) {
			if (!modelService.constructors.isEmpty()) {
				for (ModelServiceBase<?>.ModelConstructor<?> c : modelService.constructors
				        .values()) {
					if (aoClass == null || c.aoClass == None.class
					        || c.aoClass.isAssignableFrom(aoClass)) {
						list.add(c);
					}
				}
			}
		}

		@Override
		public boolean matchFillModelInvokee(Object key1, Object key2,
		        Object key3) {
			if (key1 == null) {
				return key2 == null && key3 == null;
			} else if (key2 == null && key1 instanceof Class<?>) {
				return key3 == null;
			} else if (key3 == null && key1 instanceof Class<?>
			        && key2 instanceof Class<?>) {
				return true;
			} else {
				return false;
			}
		}

		@Override
		public Map<Class<?>, ? extends ModelInvokee> getMap(
		        ModelServiceBase<?> modelService) {
			return modelService.constructors;
		}
	};
	private static final ModelInvokeeFiller constraintCheckerFiller = new ModelInvokeeFiller(
	        ModelServiceBase.ModelConstraintChecker.class) {
		@Override
		@SuppressWarnings("unchecked")
		public void fill(ModelServiceBase<?> modelService, List list,
		        Class<?> aoClass) {
			if (!modelService.constraintCheckers.isEmpty()) {
				for (ModelServiceBase<?>.ModelConstraintChecker c : modelService.constraintCheckers
				        .values()) {
					list.add(c);
				}
			}
		}

		@Override
		public boolean matchFillModelInvokee(Object key1, Object key2,
		        Object key3) {
			if (key1 == null) {
				return key2 == null && key3 == null;
			} else if (key2 == null && key1 instanceof Class<?>) {
				return key3 == null;
			} else {
				return false;
			}
		}

		@Override
		public Map<Class<?>, ? extends ModelInvokee> getMap(
		        ModelServiceBase<?> modelService) {
			return modelService.constraintCheckers;
		}
	};
	private static final ModelInvokeeFiller objProviderFiller = new ModelInvokeeFiller(
	        ModelServiceBase.ModelObjProvider.class) {
		@Override
		@SuppressWarnings("unchecked")
		public void fill(ModelServiceBase<?> modelService, List list,
		        Class<?> aoClass) {
			if (!modelService.objProviders.isEmpty()) {
				for (ModelServiceBase<?>.ModelObjProvider<?> h : modelService.objProviders
				        .values()) {
					if (aoClass == null || h.aoClass == None.class
					        || h.aoClass.isAssignableFrom(aoClass)) {
						list.add(h);
					}
				}
			}
		}

		@Override
		public boolean matchFillModelInvokee(Object key1, Object key2,
		        Object key3) {
			if (key1 == null) {
				return key2 == null && key3 == null;
			} else if (key2 == null && key1 instanceof Class<?>) {
				return key3 == null;
			} else if (key3 == null && key1 instanceof Class<?>
			        && key2 instanceof Class<?>) {
				return true;
			} else {
				return false;
			}
		}

		@Override
		public Map<Class<?>, ? extends ModelInvokee> getMap(
		        ModelServiceBase<?> modelService) {
			return modelService.objProviders;
		}
	};

	private static ModelInvokeeFiller[] modelInvokeeFilters = {
	        actionHandlerFiller, propertyAccessorFiller, constructorFiller,
	        constraintCheckerFiller, objProviderFiller };

	final static ModelInvokeeFiller findModelInvokeeFiller(Class<?> invokeeClass) {
		for (ModelInvokeeFiller filter : modelInvokeeFilters) {
			if (filter.baseClass.isAssignableFrom(invokeeClass)) {
				return filter;
			}
		}
		return null;
	}

	final static ModelInvokeeFiller getModelInvokeeFiller(Class<?> invokeeClass) {
		ModelInvokeeFiller filter = findModelInvokeeFiller(invokeeClass);
		if (filter == null) {
			throw new UnsupportedOperationException();
		}
		return filter;
	}

	@Override
	final boolean tryRegDeclaredClasses(Class<?> serviceClass,
	        Class<?> declaredClass, Mode servicePublishMode,
	        ExceptionCatcher catcher) {
		if (super.tryRegDeclaredClasses(serviceClass, declaredClass,
		        servicePublishMode, catcher)) {
			return true;
		}
		ModelInvokeeFiller modelInvokeeFiller = findModelInvokeeFiller(declaredClass);
		if (modelInvokeeFiller != null) {
			modelInvokeeFiller.regModelInvokee(this, declaredClass);
			return true;
		}
		if (ModelServiceBinder.class.isAssignableFrom(declaredClass)) {
			ModelServiceBinder<?> binder = (ModelServiceBinder<?>) this
			        .newObjectInNode(declaredClass, null, null);
			binder.next = this.bindlers;
			this.bindlers = binder;
			return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	final void afterRegInvokees(Mode servicePublishMode,
	        ExceptionCatcher catcher) {
		super.afterRegInvokees(servicePublishMode, catcher);
		for (ModelServiceBinder<?> binder = this.bindlers; binder != null; binder = binder.next) {
			ModelDeclarator<TMO> declaror = this
			        .findElement(binder.declarorClass);
			// TODO REG to ModelDeclarorClass
			if (declaror != null) {
				try {
					((ModelServiceBinder) binder).doBind(declaror,
					        (ModelDeclare) declaror.getDefine());
				} catch (Throwable e) {
					catcher.catchException(e, binder);
				}
			}
		}
	}
}
