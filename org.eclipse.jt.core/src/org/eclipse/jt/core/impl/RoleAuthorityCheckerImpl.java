package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.auth.Role;
import org.eclipse.jt.core.auth.RoleAuthorityChecker;
import org.eclipse.jt.core.type.GUID;


final class RoleAuthorityCheckerImpl extends ActorAuthorityCheckerImpl
		implements RoleAuthorityChecker {

	final Role role;

	RoleAuthorityCheckerImpl(ContextImpl<?, ?, ?> context, Role role,
			GUID orgID, long[][] acl) {
		super(context, orgID, acl);
		assert role != null;
		this.role = role;
	}

	public final Role getRole() {
		return this.role;
	}

}
