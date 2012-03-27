package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.misc.SXElement;
import org.eclipse.jt.core.spi.publish.BundleToken;

/**
 * 服务发布收集器
 * 
 * @author Jeff Tang
 * 
 */
final class PublishedServiceGatherer extends
        PublishedElementGatherer<PublishedService> {
	@Override
	final void afterGatherElement(PublishedService pe, ResolveHelper helper) {
		helper.regStartupEntry(PublishedService.create, pe);
	}

	@Override
	protected PublishedService parseElement(SXElement element,
	        BundleToken bundle) throws Throwable {
		return new PublishedService(bundle, element);
	}
}
