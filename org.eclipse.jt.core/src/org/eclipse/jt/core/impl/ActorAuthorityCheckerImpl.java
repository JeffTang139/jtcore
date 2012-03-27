package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.auth.ActorAuthorityChecker;
import org.eclipse.jt.core.auth.Authority;
import org.eclipse.jt.core.auth.Operation;
import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.exception.UnsupportedAuthorityResourceException;
import org.eclipse.jt.core.resource.ResourceToken;
import org.eclipse.jt.core.type.GUID;


abstract class ActorAuthorityCheckerImpl implements ActorAuthorityChecker {

	final ContextImpl<?, ?, ?> context;

	final GUID orgID;

	final long[][] acl;

	public final GUID getOrgID() {
		return this.orgID;
	}

	protected ActorAuthorityCheckerImpl(ContextImpl<?, ?, ?> context,
			GUID orgID, long[][] acl) {
		assert context != null;
		this.context = context;
		this.orgID = orgID;
		this.acl = acl;
	}

	@SuppressWarnings("unchecked")
	public <TFacade> Authority getAuthority(
			Operation<? super TFacade> operation,
			ResourceToken<TFacade> resoureceToken) {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (resoureceToken == null) {
			throw new NullArgumentException("resource");
		}
		if (resoureceToken instanceof ResourceItem) {
			return this.getAuthority(operation,
					(ResourceItem<TFacade, ?, ?>) resoureceToken);
		} else if (resoureceToken instanceof ResourceTokenMissing) {
			return Authority.DENY;
		} else {
			throw new UnsupportedOperationException();
		}
	}

	<TFacade> Authority getAuthority(Operation<? super TFacade> operation,
			ResourceItem<TFacade, ?, ?> resourceItem) {
		if (resourceItem.group.isAuthorizable()) {
			final OperationEntry operationEntry = resourceItem.group.resourceService
					.getOperationEntry(operation);
			return resourceItem.getAuthority(this.context, operationEntry,
					this.acl[0]);
		}
		throw new UnsupportedAuthorityResourceException(resourceItem
				.getFacadeClass());
	}

	@SuppressWarnings("unchecked")
	public <TFacade> boolean hasAuthority(Operation<? super TFacade> operation,
			ResourceToken<TFacade> resoureceToken) {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (resoureceToken == null) {
			throw new NullArgumentException("resource");
		}
		if (resoureceToken instanceof ResourceItem) {
			return this.hasAuthority(operation,
					(ResourceItem<TFacade, ?, ?>) resoureceToken);
		} else if (resoureceToken instanceof ResourceTokenMissing) {
			return false;
		} else {
			throw new UnsupportedOperationException();
		}
	}

	<TFacade> boolean hasAuthority(Operation<? super TFacade> operation,
			ResourceItem<TFacade, ?, ?> resourceItem) {
		if (resourceItem.group.isAuthorizable()) {
			final OperationEntry operationEntry = resourceItem.group.resourceService
					.getOperationEntry(operation);
			return resourceItem.validateAuthority(this.context.transaction,
					operationEntry, this.acl);
		}
		throw new UnsupportedAuthorityResourceException(resourceItem
				.getFacadeClass());
	}

	int generateAuthorityInfo(ResourceItem<?, ?, ?> resourceItem) {
		if (resourceItem.group.isAuthorizable()) {
			return resourceItem.generateAuthorityInfo(this.context.transaction,
					this.acl);
		}
		throw new UnsupportedAuthorityResourceException(resourceItem
				.getFacadeClass());
	}

}
