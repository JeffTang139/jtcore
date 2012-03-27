package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.misc.SXElement;
import org.eclipse.jt.core.service.Publish;
import org.eclipse.jt.core.spi.publish.BundleToken;

/**
 * 发布的元素的收集器
 * 
 * @author Jeff Tang
 * 
 * @param <TElement>
 *            元素类型
 * @param <TPublishedElement>
 *            元素的发布信息
 */
public abstract class PublishedElementGatherer<TPublishedElement extends PublishedElement> {
	/**
	 * 下一个收集同组同元素的收集器
	 */
	PublishedElementGatherer<?> nextGatherer;

	protected abstract TPublishedElement parseElement(SXElement element,
	        BundleToken bundle) throws Throwable;

	final static String xml_attr_space = "space";
	final static String xml_attr_visibility = "visibility";

	void afterGatherElement(TPublishedElement pe, ResolveHelper helper) {
	}

	final boolean gatherElement(Site site, BundleStub bundle,
	        SXElement element, ResolveHelper helper) {
		try {
			TPublishedElement pe = this.parseElement(element, bundle);
			if (pe != null) {
				pe.publishMode = element.getEnum(Publish.Mode.class,
				        PublishedElementGatherer.xml_attr_visibility,
				        Publish.Mode.DEFAULT);
				pe.space = site.ensureSpace(element
				        .getAttribute(PublishedElementGatherer.xml_attr_space),
				        '/');
				pe.bundle = bundle;
				this.afterGatherElement(pe, helper);
				return true;
			}
		} catch (Throwable e) {
			helper.catcher.catchException(e, this);
		}
		return false;
	}

	final static String xml_element_gathering = "gathering";
	final static String xml_element_gatherer = "gatherer";
	final static String xml_attr_class = "class";
	final static String xml_attr_group = "group";
	final static String xml_attr_element = "element";

}
