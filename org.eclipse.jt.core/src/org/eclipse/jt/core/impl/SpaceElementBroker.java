package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.Context;
import org.eclipse.jt.core.None;
import org.eclipse.jt.core.misc.ExceptionCatcher;
import org.eclipse.jt.core.service.Publish.Mode;

/**
 * 空间元素代理器，按照类型索引，包括各种服务的实例以及各种定义器的实例
 * 
 * @author Jeff Tang
 * 
 * @param <TElement>
 *            元素类型
 */
final class SpaceElementBroker<TElement> extends
		ServiceInvokeeBase<TElement, Context, None, None, None> {
	final TElement element;

	SpaceElementBroker(TElement element, Mode publishMode) {
		if (element == null || publishMode == null) {
			throw new NullPointerException();
		}
		this.element = element;
		this.publishMode = publishMode;
	}

	@Override
	final void afterRegInvokeeToSpace(ServiceInvokeeEntry to, Space space,
			ExceptionCatcher catcher) {
		if (this.element instanceof DeclaratorBase) {
			DeclaratorBase declarator = (DeclaratorBase) this.element;
			NamedDefineImpl define = (NamedDefineImpl) declarator.getDefine();
			for (Class<?> intfClass : declarator.getDefineIntfRegClasses()) {
				space.regNamedDefineToSpace(intfClass, define, catcher);
			}
		}
	}

	@Override
	final TElement provide(Context context) throws Throwable {
		return this.element;
	}

	@Override
	final ServiceBase<Context> getService() {
		throw new UnsupportedOperationException();
	}

	final Object getElement() {
		return this.element;
	}

	@Override
	final Class<?> getTargetClass() {
		return this.element.getClass();
	}

	@Override
	final boolean match(Class<?> key1Class, Class<?> key2Class,
			Class<?> key3Class, int mask) {
		return mask == MASK_ELEMENT;
	}
}
