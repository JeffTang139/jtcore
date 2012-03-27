package org.eclipse.jt.core.impl;

import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.eclipse.jt.core.Context;
import org.eclipse.jt.core.def.DNASqlType;
import org.eclipse.jt.core.def.NamedDefine;
import org.eclipse.jt.core.impl.ModelServiceBase.ModelInvokee;
import org.eclipse.jt.core.impl.ModelServiceBase.ModelInvokeeFiller;
import org.eclipse.jt.core.invoke.Event;
import org.eclipse.jt.core.misc.ExceptionCatcher;
import org.eclipse.jt.core.misc.HashUtil;
import org.eclipse.jt.core.misc.IteratorFilter;
import org.eclipse.jt.core.service.Publish;
import org.eclipse.jt.core.spi.def.DefineKind;
import org.eclipse.jt.core.spi.publish.SpaceToken;
import org.eclipse.jt.core.testing.CaseTesterInstance;


/**
 * �ռ����
 * 
 * @author Jeff Tang
 * 
 */
class Space extends SpaceNode implements SpaceToken {
	@Override
	void doDispose(ContextImpl<?, ?, ?> context) {
		for (Space cld = this.firstChild; cld != null; cld = cld.next) {
			cld.doDispose(context);
		}
		if (this.invokeeTableSize > 0) {
			// ���ҿռ��еķ�������֮
			for (ServiceInvokeeEntry e : this.invokeeTable) {
				while (e != null) {
					if (ServiceBase.class.isAssignableFrom(e.targetClass)) {
						ServiceInvokeeBase<?, ?, ?, ?, ?> sib = e.first;
						while (sib != null) {
							if (sib instanceof SpaceElementBroker<?>) {
								((ServiceBase<?>) ((SpaceElementBroker<?>) sib).element)
										.doDispose(context);
							}
							sib = sib.next;
						}
					}
					e = e.next;
				}
			}
		}
	}

	/**
	 * ����
	 */
	final String name;
	/**
	 * ���ݿ�������Ϣ,Ϊnull��ʾû�����ݿ�����
	 */
	DataSourceRef dataSourceRef;

	/**
	 * ���Ի�ȡ��ǰ�ڵ������Դ
	 */
	@Override
	final DataSourceRef tryGetDataSourceRef() {
		return this.dataSourceRef;
	}

	@Override
	public String toString() {
		return this.name + " (space)";
	}

	public final String getName() {
		return this.name;
	}

	public final Space getParent() {
		return this.space;
	}

	public final Space getSibling() {
		return this.next;
	}

	public final Space getFirstChild() {
		return this.firstChild;
	}

	public final Site getSite() {
		return this.site;
	}

	/**
	 * �ֵܿռ�
	 */
	private final Space next;
	/**
	 * �ӿռ�
	 */
	private Space firstChild;

	/**
	 * ����ƥ�����ģ�� +
	 * 
	 * @param spacePath
	 *            �ռ�·��
	 * @param offset
	 *            �ռ�����ƫ����
	 * @param len
	 *            �ռ����ĳ���
	 * @return ����ƥ���ģ��
	 */
	final Space findSub(String spacePath, int offset, int len) {
		for (Space space = this.firstChild; space != null; space = space.next) {
			if (space.name.length() == len
					&& spacePath.regionMatches(offset, space.name, 0, len)) {
				return space;
			}
		}
		return null;
	}

	final void regDeclarator(DeclaratorBase declarator,
			Publish.Mode publishMode, ExceptionCatcher catcher) {
		SpaceElementBroker<DeclaratorBase> broker = new SpaceElementBroker<DeclaratorBase>(
				declarator, publishMode);
		this.regInvokee(declarator.getClass(), broker, catcher);
	}

	final void regService(ServiceBase<?> service, Publish.Mode publishMode,
			ExceptionCatcher catcher) {
		this.regInvokee(service.getClass(),
				new SpaceElementBroker<ServiceBase<?>>(service, publishMode),
				catcher);
		if (service instanceof ResourceServiceBase<?, ?, ?>) {
			final ResourceServiceBase<?, ?, ?> rs = (ResourceServiceBase<?, ?, ?>) service;
			this.regInvokee(rs.facadeClass, new ResourceServiceBroker(rs,
					publishMode), catcher);
		}
	}

	@SuppressWarnings("unchecked")
	final void regNamedDefineToSpace(Class<?> defineIntfClass,
			NamedDefineImpl define, ExceptionCatcher catcher) {
		final NamedDefineBroker broker = (NamedDefineBroker) this
				.findInvokeeBase(defineIntfClass, String.class, null, null,
						ServiceInvokeeBase.MASK_DEFINE,
						InvokeeQueryMode.IN_SPACE);
		if (broker != null) {
			NamedDefineImpl exists = broker.putDefine(define);
			if (exists != null) {
				catcher.catchException(new IllegalArgumentException("�����Ķ��屻ע��:"
						+ exists + " -> " + define), define);
			}
		} else {
			this.regInvokeeToSpace(defineIntfClass,
					new NamedDefineBroker<NamedDefineImpl>(defineIntfClass,
							this, define), catcher);
		}
	}

	@SuppressWarnings("unchecked")
	final NamedDefineImpl unRegNamedDefineFromSpace(Class<?> defineIntfClass,
			String name) {
		final NamedDefineBroker broker = (NamedDefineBroker) this
				.findInvokeeBase(defineIntfClass, String.class, null, null,
						ServiceInvokeeBase.MASK_DEFINE,
						InvokeeQueryMode.IN_SPACE);
		if (broker != null) {
			return broker.removeDefine(name);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	final void regNamedFactoryElement(NamedFactoryElementGather<?, ?> factory,
			NamedFactoryElement meta, Publish.Mode publishMode,
			ExceptionCatcher catcher) {
		if (this.publishMode == null || publishMode == Publish.Mode.PROTECTED) {
			Class<?> factoryClass = factory.getClass();
			final NamedFactoryElementBroker<?> broker = (NamedFactoryElementBroker<?>) this
					.findInvokeeBase(factoryClass, String.class, null, null,
							ServiceInvokeeBase.MASK_ELEMENT_META,
							InvokeeQueryMode.IN_SPACE);
			if (broker != null) {
				NamedFactoryElement old = broker.putElementMeta(meta);
				if (old != null) {
					catcher.catchException(new IllegalArgumentException(
							"�����ظ���Ԫ�ر�ע��:" + meta + " -> " + old), meta);
				}
			} else {
				this.regInvokeeToSpace(factoryClass,
						new NamedFactoryElementBroker(factory, this, meta),
						catcher);
			}
		} else {
			if (publishMode == Publish.Mode.DEFAULT) {
				publishMode = this.publishMode;
			}
			this.space.regNamedFactoryElement(factory, meta, publishMode,
					catcher);
		}
	}

	final boolean regDeclareScript(String declareName, DNASqlType type,
			URL url, Publish.Mode publishMode, ExceptionCatcher catcher) {
		if (this.publishMode == null || publishMode == Publish.Mode.PROTECTED) {
			final DeclareScriptBroker broker = (DeclareScriptBroker) this
					.findInvokeeBase(URL.class, String.class, DNASqlType.class,
							null, ServiceInvokeeBase.MASK_DECLARE_SCRIPT,
							InvokeeQueryMode.IN_SPACE);
			if (broker != null) {
				URL old = broker.putDeclareScriptURL(declareName, type, url);
				if (old != null) {
					broker.putDeclareScriptURL(declareName, type, old);
					catcher.catchException(new IllegalArgumentException(
							"�����������ű���ע��(" + declareName + "):\r\n-> " + url
									+ "\r\n-> " + old), url);
					return false;
				}
			} else {
				this.regInvokeeToSpace(URL.class, new DeclareScriptBroker(this,
						declareName, type, url), catcher);
			}
			return true;
		} else {
			if (publishMode == Publish.Mode.DEFAULT) {
				publishMode = this.publishMode;
			}
			return this.space.regDeclareScript(declareName, type, url,
					publishMode, catcher);
		}
	}

	final class InvokeeIterator implements
			Iterator<ServiceInvokeeBase<?, ?, ?, ?, ?>> {
		public InvokeeIterator() {
			this.space = this.root = Space.this;
			this.doNext();
		}

		private Space space;
		private final Space root;
		private int index;
		private ServiceInvokeeEntry sie;
		private ServiceInvokeeBase<?, ?, ?, ?, ?> next;

		private final void doNext() {
			if (this.next != null && (this.next = this.next.next) != null) {
				return;
			}
			do {
				if (this.sie != null) {
					this.sie = this.sie.next;
				}
				while (this.sie == null) {
					while (this.space.invokeeTable == null
							|| this.index >= this.space.invokeeTable.length) {
						Space nextSpace = this.space.firstChild;
						if (nextSpace == null) {
							if (this.space == this.root) {
								return;
							}
							nextSpace = this.space.next;
							while (nextSpace == null) {
								Space parent = this.space = this.space.space;
								if (parent == null || parent == this.root) {
									return;
								}
								nextSpace = parent.next;
							}
						}
						this.space = nextSpace;
						this.index = 0;
					}
					this.sie = this.space.invokeeTable[this.index++];
				}
			} while ((this.next = this.sie.first) == null);
		}

		public final boolean hasNext() {
			return this.next != null;
		}

		public final ServiceInvokeeBase<?, ?, ?, ?, ?> next() {
			ServiceInvokeeBase<?, ?, ?, ?, ?> curr = this.next;
			if (curr == null) {
				throw new NoSuchElementException();
			}
			this.doNext();
			return curr;
		}

		public final void remove() {
			throw new UnsupportedOperationException();
		}
	}

	private final Publish.Mode publishMode;

	/**
	 * ����Լ���վ���򷵻��Լ������򷵻�null
	 * 
	 * @return ����Լ���վ���򷵻��Լ������򷵻�null
	 */
	Site asSite() {
		return null;
	}

	Space(Space parent, String name) {
		if (name == null || name.length() == 0) {
			throw new NullPointerException();
		}
		DataSourceRef dbConnInfo = null;
		this.name = name;
		Site asSite = this.asSite();
		if (asSite != null) {
			this.publishMode = null;
			this.site = asSite;
		} else {
			this.publishMode = Publish.Mode.DEFAULT;
			if (parent != null) {
				this.site = parent.site;
				dbConnInfo = parent.dataSourceRef;
			}
		}
		this.space = parent;
		if (parent != null) {
			this.next = parent.firstChild;
			parent.firstChild = this;
		} else {
			this.next = null;
		}
		this.dataSourceRef = dbConnInfo;
	}

	/**
	 * վ���õĹ��캯��
	 * 
	 * @param parentSite
	 *            ��վ��
	 * @param element
	 */
	Space(Site parentSite, String name) {
		if (name == null || name.length() == 0) {
			name = "?";
		}
		this.name = name;
		this.publishMode = null;
		this.site = this.asSite();
		this.space = parentSite;
		if (parentSite != null) {
			this.next = this.space.firstChild;
			this.space.firstChild = this;
		} else {
			this.next = null;
		}
	}

	/**
	 * �����ĵ�������
	 */
	private ServiceInvokeeEntry[] invokeeTable;
	/**
	 * �����ĵ���������
	 */
	private int invokeeTableSize;

	// ////////////////////////////////////////////////////////////////////////
	// ///////�������������
	// ////////////////////////////////////////////////////////////////////////
	@Override
	final EventListenerChain collectEvent(Class<?> eventClass,
			InvokeeQueryMode mode) {
		EventListenerChain chain = null;
		do {
			int hash = HashUtil.hash(eventClass);
			for (Space space = this; space != null; space = space.space) {
				if (space.invokeeTableSize > 0) {
					for (ServiceInvokeeEntry e = space.invokeeTable[hash
							& (space.invokeeTable.length - 1)]; e != null; e = e.next) {
						if (e.targetClass == eventClass) {
							chain = mode.collectEvent(e, chain);
							continue;
						}
					}
				}
				switch (mode) {
				case IN_SPACE:
					return null;
				case FROM_OTHER_SITE:
					break;
				default:
					if (space.publishMode == null) {
						// ģ��ΪSite
						mode = InvokeeQueryMode.FROM_SUB_SITE;
					}
				}
			}
			eventClass = eventClass.getSuperclass();
		} while (Event.class.isAssignableFrom(eventClass));
		return chain;
	}

	@SuppressWarnings("unchecked")
	@Override
	final ServiceInvokeeBase findInvokeeBase(Class<?> objectClass,
			Class<?> key1Class, Class<?> key2Class, Class<?> key3Class,
			int mask, InvokeeQueryMode mode) {
		final int hash = HashUtil.hash(objectClass);
		Space space = this;
		do {
			if (space.invokeeTableSize > 0) {
				for (ServiceInvokeeEntry e = space.invokeeTable[hash
						& (space.invokeeTable.length - 1)]; e != null; e = e.next) {
					if (e.targetClass == objectClass) {
						ServiceInvokeeBase invokee = mode.findInvokeeBase(e,
								key1Class, key2Class, key3Class, mask);
						if (invokee != null) {
							return invokee;
						}
						break;
					}
				}
			}
			switch (mode) {
			case IN_SPACE:
				return null;
			case FROM_OTHER_SITE:
				break;
			default:
				if (space.publishMode == null) {
					// ģ��ΪSite
					mode = InvokeeQueryMode.FROM_SUB_SITE;
				}
			}
			space = space.space;
		} while (space != null);
		return null;
	}

	/**
	 * ��������������Ҳ������׳��쳣
	 */
	@SuppressWarnings("unchecked")
	@Override
	final ServiceInvokeeBase getTaskHandler(Class<?> taskClass,
			Enum<?> taskMethod, InvokeeQueryMode mode) {
		int mask = 1 << taskMethod.ordinal();
		if ((mask | ServiceBase.TASK_METHODS_MASK) != ServiceBase.TASK_METHODS_MASK) {
			throw new UnsupportedOperationException("����ķ�������");
		}
		ServiceInvokeeBase taskHandler = this.findInvokeeBase(taskClass, null,
				null, null, mask | ServiceInvokeeBase.MASK_TASK, mode);
		if (taskHandler == null) {
			throw new UnsupportedOperationException("�Ҳ�����������[" + taskClass
					+ '(' + taskMethod + ")]");
		}
		return taskHandler;
	}

	/**
	 * ��ý���б��ṩ�����Ҳ������׳��쳣
	 */
	@SuppressWarnings("unchecked")
	@Override
	final <TResult> ServiceInvokeeBase<TResult, Context, Object, Object, Object> findResultListProvider(
			Class<TResult> resultClass, Object key1, Object key2, Object key3,
			InvokeeQueryMode mode) {
		Class<?> key1Class;
		Class<?> key2Class;
		Class<?> key3Class;
		if (key1 == null) {
			key1Class = null;
			key2Class = null;
			key3Class = null;
		} else if (key2 == null) {
			key1Class = key1.getClass();
			key2Class = null;
			key3Class = null;
		} else if (key3 == null) {
			key1Class = key1.getClass();
			key2Class = key2.getClass();
			key3Class = null;
		} else {
			key1Class = key1.getClass();
			key2Class = key2.getClass();
			key3Class = key3.getClass();
		}
		return this.findInvokeeBase(resultClass, key1Class, key2Class,
				key3Class, ServiceInvokeeBase.MASK_LIST, mode);
	}

	/**
	 * ��ý���б��ṩ�����Ҳ������׳��쳣
	 */
	@SuppressWarnings("unchecked")
	@Override
	final <TResult> ServiceInvokeeBase<TResult, Context, Object, Object, Object> findResultProvider(
			Class<TResult> resultClass, Object key1, Object key2, Object key3,
			InvokeeQueryMode mode) {
		Class<?> key1Class;
		Class<?> key2Class;
		Class<?> key3Class;
		if (key1 == null) {
			key1Class = null;
			key2Class = null;
			key3Class = null;
		} else if (key2 == null) {
			key1Class = key1.getClass();
			key2Class = null;
			key3Class = null;
		} else if (key3 == null) {
			key1Class = key1.getClass();
			key2Class = key2.getClass();
			key3Class = null;
		} else {
			key1Class = key1.getClass();
			key2Class = key2.getClass();
			key3Class = key3.getClass();
		}
		return this.findInvokeeBase(resultClass, key1Class, key2Class,
				key3Class, ServiceInvokeeBase.MASK_RESULT, mode);
	}

	/**
	 * ��ý���б��ṩ�����Ҳ������׳��쳣
	 */
	@SuppressWarnings("unchecked")
	@Override
	final <TResult> ServiceInvokeeBase<TResult, Context, Object, Object, Object> findTreeNodeProvider(
			Class<TResult> elementClass, Object key1, Object key2, Object key3,
			InvokeeQueryMode mode) {
		Class<?> key1Class;
		Class<?> key2Class;
		Class<?> key3Class;
		if (key1 == null) {
			key1Class = null;
			key2Class = null;
			key3Class = null;
		} else if (key2 == null) {
			key1Class = key1.getClass();
			key2Class = null;
			key3Class = null;
		} else if (key3 == null) {
			key1Class = key1.getClass();
			key2Class = key2.getClass();
			key3Class = null;
		} else {
			key1Class = key1.getClass();
			key2Class = key2.getClass();
			key3Class = key3.getClass();
		}
		return this.findInvokeeBase(elementClass, key1Class, key2Class,
				key3Class, ServiceInvokeeBase.MASK_TREE, mode);
	}

	@SuppressWarnings("unchecked")
	@Override
	final ResourceServiceBase findResourceService(Class<?> facadeClass,
			InvokeeQueryMode mode) {
		ServiceInvokeeBase<ResourceServiceBase, Context, ?, ?, ?> invokeeBase = this
				.findInvokeeBase(facadeClass, null, null, null,
						ServiceInvokeeBase.MASK_RESOURCE, mode);
		if (invokeeBase != null) {
			return invokeeBase.getResourceService();
		}
		return null;
	}

	/**
	 * ע����ã�Site����Ҫ���ظ÷���
	 * 
	 * @param invokee
	 *            ����
	 * @param publishMode
	 *            ���õķ���ģʽ
	 */
	@SuppressWarnings("unchecked")
	final void regInvokee(Class<?> targetClass, ServiceInvokeeBase invokee,
			ExceptionCatcher catcher) {
		if (this.publishMode == null
				|| invokee.publishMode == Publish.Mode.PROTECTED) {
			this.regInvokeeToSpace(targetClass, invokee, catcher);
		} else {
			if (invokee.publishMode == Publish.Mode.DEFAULT) {
				invokee.publishMode = this.publishMode;
			}
			this.space.regInvokee(targetClass, invokee, catcher);
		}
	}

	/**
	 * ������ע�ᵽ��ģ��
	 * 
	 * @param invokee
	 *            ����
	 * @param isPrivate
	 *            �Ƿ���˽�е���
	 */
	@SuppressWarnings("unchecked")
	private final void regInvokeeToSpace(Class<?> targetClass,
			ServiceInvokeeBase invokee, ExceptionCatcher catcher) {
		if (this.invokeeTable == null) {
			this.invokeeTable = new ServiceInvokeeEntry[4];
		}
		int hash = HashUtil.hash(targetClass);
		int oldLen = this.invokeeTable.length;
		int index = hash & (oldLen - 1);
		ServiceInvokeeEntry firstEntry = this.invokeeTable[index];
		ServiceInvokeeEntry entry = firstEntry;
		while (entry != null && entry.targetClass != targetClass) {
			entry = entry.next;
		}
		if (entry == null) {
			entry = new ServiceInvokeeEntry(targetClass, hash, firstEntry,
					invokee);
			this.invokeeTable[index] = entry;
			if (++this.invokeeTableSize > oldLen * 0.75) {
				int newLen = oldLen * 2;
				ServiceInvokeeEntry[] newTable = new ServiceInvokeeEntry[newLen];
				for (int j = 0; j < oldLen; j++) {
					for (ServiceInvokeeEntry e = this.invokeeTable[j], next; e != null; e = next) {
						int i = e.hash & (newLen - 1);
						next = e.next;
						e.next = newTable[i];
						newTable[i] = e;
					}
				}
				this.invokeeTable = newTable;
			}
		} else {
			entry.put(invokee);
		}
		invokee.afterRegInvokeeToSpace(entry, this, catcher);
	}

	/**
	 * ���ģ�͵�����
	 */
	private final <TResult, TKey1, TKey2> boolean tryFillModelInvokees(
			List<TResult> list, Class<TResult> resultClass, Object key1,
			Object key2, Object key3) {
		ModelInvokeeFiller filler = ModelServiceBase
				.findModelInvokeeFiller(resultClass);
		if (filler == null) {
			return false;
		}
		Class<?> aoClass = (Class<?>) key1;
		Class<?> moClass = (Class<?>) key2;
		ModelServiceIterator mss = this.site.new ModelServiceIterator();
		while (mss.hasNext()) {
			ModelServiceBase<?> ms = mss.next();
			if (moClass == null || ms.moClass.isAssignableFrom(moClass)) {
				filler.fill(ms, list, aoClass);
			}
		}
		return true;
	}

	/**
	 * �������������
	 */
	private final void allocCaseTesters(List<ServiceBase<?>.CaseTester> list) {
		ServiceIterator mss = new ServiceIterator();
		while (mss.hasNext()) {
			ServiceBase<?> ms = mss.next();
			ms.allocCaseTesters(list);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	final <TResult, TKey1, TKey2, TKey3> boolean tryFillList(
			List<TResult> list, Class<TResult> resultClass, Object key1,
			Object key2, Object key3, InvokeeQueryMode mode) {
		if (mode == InvokeeQueryMode.FROM_OTHER_SITE) {
			return false;
		}
		if ((key1 == null || key2 == null && key1 instanceof DefineKind)
				&& NamedDefine.class.isAssignableFrom(resultClass)) {
			// TODO ��������ʱ������� if (key == DefineKind.DESIGN)
			return this.fillRuntimeDefines((Class<NamedDefine>) resultClass,
					(List<NamedDefine>) list);
		}
		// ���ģ�͵�����
		if (this.tryFillModelInvokees(list, resultClass, key1, key2, key3)) {
			return true;
		}
		if (key1 == null && resultClass == CaseTesterInstance.class) {
			((Space) this.site).allocCaseTesters((List) list);
			return true;
		}
		return false;
	}

	@Override
	final <TResult, TKey1, TKey2, TKey3> int tryFillTree(
			TreeNodeImpl<TResult> root, Class<TResult> resultClass,
			Object key1, Object key2, Object key3, InvokeeQueryMode mode) {
		if (mode == InvokeeQueryMode.FROM_OTHER_SITE) {
			return -1;
		}
		return -1;
	}

	private final ModelInvokee findModelInvokeeByName(Class<?> invokeeClass,
			String className) {
		ModelInvokeeFiller filler = ModelServiceBase
				.getModelInvokeeFiller(invokeeClass);
		ModelServiceIterator mss = this.site.new ModelServiceIterator();
		while (mss.hasNext()) {
			ModelServiceBase<?> ms = mss.next();
			Map<Class<?>, ? extends ModelInvokee> mp = filler.getMap(ms);
			if (!mp.isEmpty()) {
				for (Map.Entry<Class<?>, ? extends ModelInvokee> mi : mp
						.entrySet()) {
					Class<?> c = mi.getKey();
					if (c.getName().equals(className)
							&& invokeeClass.isAssignableFrom(c)) {
						return mi.getValue();
					}
				}
			}
		}
		return null;
	}

	private final ModelInvokee findModelInvokee(Class<?> invokeeClass) {
		ModelInvokeeFiller filler = ModelServiceBase
				.getModelInvokeeFiller(invokeeClass);
		ModelServiceIterator mss = this.site.new ModelServiceIterator();
		while (mss.hasNext()) {
			ModelServiceBase<?> ms = mss.next();
			ModelInvokee result = filler.getMap(ms).get(invokeeClass);
			if (result != null) {
				return result;
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	final <TResult, TKey1, TKey2, TKey3> TResult tryFindResult(
			Class<TResult> resultClass, Object key1, Object key2, Object key3,
			InvokeeQueryMode mode) {
		if (mode == InvokeeQueryMode.FROM_OTHER_SITE) {
			return null;
		}
		if (key1 == null) {
			if (resultClass == Space.class) {
				return (TResult) this;
			} else if (ModelInvokee.class.isAssignableFrom(resultClass)) {
				return (TResult) this.findModelInvokee(resultClass);
			}
			return this.findElement(resultClass);
		} else if (key1 instanceof String) {
			if (key2 == null) {
				if (NamedDefine.class.isAssignableFrom(resultClass)) {
					// �����Ʋ��Ҹ��ඨ��
					return (TResult) this.findNamedDefine(resultClass,
							(String) key1);
				} else if (resultClass == Class.class) {
					return (TResult) this.site.application
							.tryLoadClass((String) key1);
				} else if (ModelInvokee.class.isAssignableFrom(resultClass)) {
					return (TResult) this.findModelInvokeeByName(resultClass,
							(String) key1);
				}
			} else if (key3 == null && key2 instanceof String) {
				if (resultClass == Class.class) {
					return (TResult) this.site.application.tryLoadClass(
							(String) key1, (String) key2);
				}
			}
		}
		return null;
	}

	final class ModelServiceIterator
			extends
			IteratorFilter<ServiceInvokeeBase<?, ?, ?, ?, ?>, ModelServiceBase<?>> {

		public ModelServiceIterator() {
			super(new InvokeeIterator());
		}

		@Override
		protected ModelServiceBase<?> filter(
				ServiceInvokeeBase<?, ?, ?, ?, ?> in) {
			if (in instanceof SpaceElementBroker<?>) {
				final Object element = ((SpaceElementBroker<?>) in)
						.getElement();
				if (element instanceof ModelServiceBase<?>) {
					return (ModelServiceBase<?>) element;
				}
			}
			return null;
		}

	}

	final class ServiceIterator extends
			IteratorFilter<ServiceInvokeeBase<?, ?, ?, ?, ?>, ServiceBase<?>> {

		public ServiceIterator() {
			super(new InvokeeIterator());
		}

		@Override
		protected ServiceBase<?> filter(ServiceInvokeeBase<?, ?, ?, ?, ?> in) {
			if (in instanceof SpaceElementBroker<?>) {
				final Object element = ((SpaceElementBroker<?>) in)
						.getElement();
				if (element instanceof ServiceBase<?>) {
					return (ServiceBase<?>) element;
				}
			}
			return null;
		}
	}

}
