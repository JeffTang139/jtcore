package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.model.ModelScriptEngine;
import org.eclipse.jt.core.misc.SXElement;
import org.eclipse.jt.core.spi.publish.BundleToken;

public class PublishedModelScriptEngineGetherer extends
        PublishedElementGatherer<PublishedModelScriptEngine> {

	@Override
	final protected PublishedModelScriptEngine parseElement(SXElement element,
	        BundleToken bundle) throws Throwable {
		return new PublishedModelScriptEngine(bundle.loadClass(element
		        .getAttribute(PublishedElementGatherer.xml_attr_class),
		        ModelScriptEngine.class));
	}

	@Override
	final void afterGatherElement(PublishedModelScriptEngine pe,
	        ResolveHelper helper) {
		helper.regStartupEntry(PublishedModelScriptEngine.create, pe);
	}
}
