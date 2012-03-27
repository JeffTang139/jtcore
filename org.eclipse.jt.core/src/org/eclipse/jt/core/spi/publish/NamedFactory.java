package org.eclipse.jt.core.spi.publish;

import java.util.List;

import org.eclipse.jt.core.Context;
import org.eclipse.jt.core.impl.NamedFactoryElementGather;
import org.eclipse.jt.core.misc.SXElement;


/**
 * 元素工厂
 * 
 * @author Jeff Tang
 * 
 * @param <TElement>
 *            元素类型
 * @param <TElementMeta>
 *            元素元数据
 */
public abstract class NamedFactory<TElement, TElementMeta extends NamedFactoryElement>
        extends NamedFactoryElementGather<TElement, TElementMeta> {

	@Override
	public final TElement newElement(Context context, String alias,
	        Object... adArgs) {
		return super.newElement(context, alias, adArgs);
	}

	@Override
	public final List<TElement> newAllElement(Context context, Object... adArgs) {
		return super.newAllElement(context, adArgs);
	}

	@Override
	public final List<TElementMeta> getAllElementMeta(Context context) {
		return super.getAllElementMeta(context);
	}

	@Override
	public final TElementMeta findElementMeta(Context context, String alias) {
		return super.findElementMeta(context, alias);
	}

	@Override
	protected abstract TElement doNewElement(Context context,
	        TElementMeta meta, Object... adArgs);

	@Override
	protected abstract TElementMeta parseElement(SXElement element,
	        BundleToken bundle) throws Throwable;

	@Override
	protected final TElement newElement(Class<TElement> elementClass,
	        Context context, Object[] adArgs) {
		return super.newElement(elementClass, context, adArgs);
	}
}
