package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.User;
import org.eclipse.jt.core.auth.Authority;
import org.eclipse.jt.core.auth.Operation;
import org.eclipse.jt.core.auth.UserAuthorityChecker;
import org.eclipse.jt.core.resource.ResourceToken;
import org.eclipse.jt.core.type.GUID;


final class UserAuthorityCheckerImpl extends ActorAuthorityCheckerImpl
		implements UserAuthorityChecker {

	final User user;

	UserAuthorityCheckerImpl(ContextImpl<?, ?, ?> context, User user,
			GUID orgID, long[][] acl) {
		super(context, orgID, acl);
		assert user != null;
		this.user = user;
	}

	public final User getUser() {
		return this.user;
	}

	public final <TFacade> Authority getAuthority(
			Operation<? super TFacade> operation,
			ResourceToken<TFacade> resoureceToken) {
		if (this.user == InternalUser.debugger) {
			return Authority.ALLOW;
		}
		return super.getAuthority(operation, resoureceToken);
	}

	final <TFacade> boolean hasAuthority(Operation<? super TFacade> operation,
			ResourceItem<TFacade, ?, ?> resourceItem) {
		if (this.user == InternalUser.debugger) {
			return true;
		}
		return super.hasAuthority(operation, resourceItem);
	}

	public final <TFacade> boolean hasAuthority(
			Operation<? super TFacade> operation,
			ResourceToken<TFacade> resoureceToken) {
		if (this.user == InternalUser.debugger) {
			return true;
		}
		return super.hasAuthority(operation, resoureceToken);
	}

	final <TFacade> Authority getAuthority(
			Operation<? super TFacade> operation,
			ResourceItem<TFacade, ?, ?> resourceItem) {
		if (this.user == InternalUser.debugger) {
			return Authority.ALLOW;
		}
		return super.getAuthority(operation, resourceItem);
	}

	final int generateAuthorityInfo(ResourceItem<?, ?, ?> resourceItem) {
		if (this.user == InternalUser.debugger) {
			return 0xFFFF;
		}
		return super.generateAuthorityInfo(resourceItem);
	}

}
