package org.eclipse.jt.core.spi.publish;

import org.eclipse.jt.core.misc.SXElement;

public abstract class PublishedElementGatherer<TPublishedElement extends PublishedElement>
        extends
        org.eclipse.jt.core.impl.PublishedElementGatherer<TPublishedElement> {
	@Override
	protected abstract TPublishedElement parseElement(SXElement element,
	        BundleToken bundle) throws Throwable;
}
