package org.eclipse.jt.core.impl;

import java.util.ArrayList;

import org.eclipse.jt.core.Context;
import org.eclipse.jt.core.None;
import org.eclipse.jt.core.service.Publish;


/**
 * 类引用代理器，包括页面类等
 * 
 * @author Jeff Tang
 * 
 * @param <TClass>
 *            元素类型
 */
final class NamedFactoryElementBroker<TFactory extends NamedFactoryElementGather<?, ?>>
		extends ServiceInvokeeBase<TFactory, Context, String, None, None> {
	private final NamedFactoryElementMap map = new NamedFactoryElementMap();
	private final Space space;
	private final TFactory facotry;

	NamedFactoryElementBroker(TFactory facotry, Space space,
			NamedFactoryElement meta) {
		if (facotry == null || space == null || meta == null) {
			throw new NullPointerException();
		}
		this.publishMode = Publish.Mode.PROTECTED;
		this.space = space;
		this.facotry = facotry;
		this.map.put(meta);
	}

	@Override
	final Space getSpace() {
		return this.space;
	}

	final NamedFactoryElement putElementMeta(NamedFactoryElement meta) {
		if (meta == null) {
			throw new NullPointerException();
		}
		return this.map.put(meta);
	}

	final NamedFactoryElement findNamedFactoryElement(String name) {
		if (name == null) {
			throw new NullPointerException();
		}
		return this.map.get(name);
	}

	final <TElementMeta extends NamedFactoryElement> ArrayList<TElementMeta> fetchNamedFactoryElements(
			ArrayList<TElementMeta> metas) {
		return this.map.fillElements(metas);
	}

	@SuppressWarnings("unchecked")
	@Override
	final NamedFactoryElementBroker<TFactory> upperMatchBroker() {
		Space space = this.space;
		if (space.site == space) {
			return null;// 到达站点了
		}
		return (NamedFactoryElementBroker<TFactory>) space.space
				.findInvokeeBase(this.facotry.getClass(), String.class, null,
						null, MASK_ELEMENT_META, InvokeeQueryMode.IN_SITE);
	}

	@Override
	final TFactory provide(Context context, String key1) throws Throwable {
		return this.facotry;
	}

	@Override
	final ServiceBase<Context> getService() {
		throw new UnsupportedOperationException();
	}

	@Override
	final Class<?> getTargetClass() {
		return this.facotry.getClass();
	}

	@Override
	final boolean match(Class<?> key1Class, Class<?> key2Class,
			Class<?> key3Class, int mask) {
		return mask == MASK_ELEMENT_META && key1Class == String.class
				&& key2Class == null;
	}
}
