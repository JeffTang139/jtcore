package org.eclipse.jt.core.impl;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jt.core.Context;


public abstract class NamedFactoryElementGather<TElement, TElementMeta extends NamedFactoryElement>
        extends PublishedElementGatherer<TElementMeta> {
	protected abstract TElement doNewElement(Context context,
	        TElementMeta meta, Object... adArgs);

	public TElement newElement(Context context, String alias, Object... adArgs) {
		final ContextImpl<?, ?, ?> ctx = ContextImpl.toContext(context);
		final TElementMeta meta = ctx.occorAt.findNamedFactoryElement(this,
		        alias);
		if (meta == null) {
			throw new IllegalArgumentException("找不到名为\"" + alias + "\"的元素");
		}
		return ctx.newElement(context, this, meta, adArgs);
	}

	public TElementMeta findElementMeta(Context context, String alias) {
		return ContextImpl.toContext(context).occorAt.findNamedFactoryElement(
		        this, alias);
	}

	public List<TElement> newAllElement(Context context, Object... adArgs) {
		final ContextImpl<?, ?, ?> ctx = ContextImpl.toContext(context);
		ArrayList<TElementMeta> metas = ctx.occorAt.fillNamedFactoryElements(
		        this, null);
		if (metas == null) {
			return new ArrayList<TElement>(0);
		}
		ArrayList<TElement> r = new ArrayList<TElement>();
		for (TElementMeta meta : metas) {
			r.add(ctx.newElement(context, this, meta, adArgs));
		}
		return r;
	}

	public List<TElementMeta> getAllElementMeta(Context context) {
		final ContextImpl<?, ?, ?> c = ContextImpl.toContext(context);
		ArrayList<TElementMeta> metas = c.occorAt.fillNamedFactoryElements(
		        this, null);
		if (metas == null) {
			metas = new ArrayList<TElementMeta>(0);
		}
		return metas;
	}

	@Override
	final void afterGatherElement(TElementMeta pe, ResolveHelper helper) {
		pe.space.regNamedFactoryElement(this, pe, pe.publishMode,
		        helper.catcher);
	}

	protected TElement newElement(Class<TElement> elementClass,
	        Context context, Object[] adArgs) {
		final ContextImpl<?, ?, ?> c = ContextImpl.toContext(context);
		return c.occorAt.newObjectInNode(elementClass, context, adArgs);
	}
}
