package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.resource.ResourceKind;
import org.eclipse.jt.core.resource.ResourceToken;
import org.eclipse.jt.core.resource.ResourceTokenLink;

public final class ResourceTokenMissing implements ResourceToken<Object> {
	public static final ResourceTokenMissing TOKEN = new ResourceTokenMissing();

	private ResourceTokenMissing() {
	}

	public final Object getFacade() {
		throw new UnsupportedOperationException();
	}

	public final Object tryGetFacade() {
		return null;
	}

	public ResourceTokenLink<Object> getChildren() {
		return null;
	}

	public ResourceToken<Object> getParent() {
		return null;
	}

	public <TSubFacade> ResourceTokenLink<TSubFacade> getSubTokens(
	        Class<TSubFacade> subTokenFacadeClass)
	        throws IllegalArgumentException {
		return null;
	}

	public <TSuperFacade> ResourceToken<TSuperFacade> getSuperToken(
	        Class<TSuperFacade> superTokenFacadeClass)
	        throws IllegalArgumentException {
		return null;
	}

	public Class<Object> getFacadeClass() {
		throw new UnsupportedOperationException();
	}

	public ResourceKind getKind() {
		throw new UnsupportedOperationException();
	}

	public ResourceTokenLink<?> getSuperTokens() {
		throw new UnsupportedOperationException();
	}

	// public <TReferFacade> List<ResourceToken<TReferFacade>> referredBy(
	// Class<TReferFacade> resourceFacadeClass) {
	// throw new UnsupportedOperationException();
	// }
	//
	// public <TReferFacade> ResourceToken<TReferFacade> referredBySomeone(
	// Class<TReferFacade> resourceFacadeClass) {
	// throw new UnsupportedOperationException();
	// }

	public Object getCategory() {
		// TODO Auto-generated method stub
		return null;
	}
}
