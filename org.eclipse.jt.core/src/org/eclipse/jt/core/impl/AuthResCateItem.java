package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.auth.Operation;
import org.eclipse.jt.core.exception.DisposedException;
import org.eclipse.jt.core.resource.ResourceKind;
import org.eclipse.jt.core.resource.ResourceToken;
import org.eclipse.jt.core.resource.ResourceTokenLink;
import org.eclipse.jt.core.spi.auth.AuthorizableResourceCategoryItem;
import org.eclipse.jt.core.type.GUID;


final class AuthResCateItem implements AuthorizableResourceCategoryItem {

	@SuppressWarnings("unchecked")
	public final ResourceTokenLink getChildren() {
		throw new UnsupportedOperationException();
	}

	@SuppressWarnings("unchecked")
	public final ResourceToken getParent() {
		throw new UnsupportedOperationException();
	}

	@SuppressWarnings("unchecked")
	public final ResourceTokenLink getSubTokens(Class subTokenFacadeClass)
			throws IllegalArgumentException {
		throw new UnsupportedOperationException();
	}

	@SuppressWarnings("unchecked")
	public final ResourceToken getSuperToken(Class superTokenFacadeClass)
			throws IllegalArgumentException {
		throw new UnsupportedOperationException();
	}

	public final Object getCategory() {
		throw new UnsupportedOperationException();
	}

	public final Object getFacade() throws DisposedException {
		throw new UnsupportedOperationException();
	}

	@SuppressWarnings("unchecked")
	public final Class getFacadeClass() {
		return this.facadeClass;
	}

	public final ResourceKind getKind() {
		throw new UnsupportedOperationException();
	}

	public final Object tryGetFacade() {
		throw new UnsupportedOperationException();
	}

	public final String getTitle() {
		return this.title;
	}

	public final Operation<?>[] getOperations() {
		return this.operations;
	}

	public final GUID getGUID() {
		return this.itemGUID;
	}

	AuthResCateItem(ResourceGroup<?, ?, ?> resourceGroup) {
		if (!resourceGroup.isAuthorizable()) {
			throw new IllegalArgumentException("当前资源类别不支持权限");
		}
		this.itemID = resourceGroup.id;
		this.itemGUID = resourceGroup.groupID;
		this.title = resourceGroup.categoryTitle;
		this.facadeClass = resourceGroup.resourceService.facadeClass;
		this.operations = resourceGroup.resourceService.authorizableResourceProvider.operations;
	}

	final long itemID;

	final GUID itemGUID;

	final String title;

	@SuppressWarnings("unchecked")
	transient final Class facadeClass;

	transient final Operation<?>[] operations;

}
