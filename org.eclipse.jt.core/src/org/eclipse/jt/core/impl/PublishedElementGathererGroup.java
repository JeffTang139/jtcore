package org.eclipse.jt.core.impl;

import java.util.HashMap;

import org.eclipse.jt.core.misc.SXElement;


/**
 * ÊÕ¼¯Æ÷×é
 * 
 * @author Jeff Tang
 * 
 */
final class PublishedElementGathererGroup extends
        HashMap<String, PublishedElementGatherer<?>> {
	private static final long serialVersionUID = 1L;

	PublishedElementGathererGroup() {
		super(1);
	}

	private PublishedElementGatherer<?> noneElementGroup;

	final int gatherElement(Site site, BundleStub bundle, SXElement element,
	        ResolveHelper helper) {
		int count = 0;
		for (PublishedElementGatherer<?> gatherer = this.noneElementGroup; gatherer != null; gatherer = gatherer.nextGatherer) {
			if (this.noneElementGroup.gatherElement(site, bundle, element,
			        helper)) {
				count++;
			}
		}
		for (PublishedElementGatherer<?> gatherer = super.get(element.name); gatherer != null; gatherer = gatherer.nextGatherer) {
			if (gatherer.gatherElement(site, bundle, element, helper)) {
				count++;
			}
		}
		return count;
	}

	final void putGather(String elementTag, PublishedElementGatherer<?> gather) {
		if (elementTag == null || elementTag.length() == 0) {
			gather.nextGatherer = this.noneElementGroup;
			this.noneElementGroup = gather;
			return;
		}
		gather.nextGatherer = super.put(elementTag, gather);
	}
}