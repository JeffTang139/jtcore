package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.type.GUID;

/**
 * 角色代理类
 * 
 * @see org.eclipse.jt.core.impl.ActorProxy
 * @see org.eclipse.jt.core.impl.IInternalRole
 * @author Jeff Tang 2009-12
 */
final class RoleProxy extends ActorProxy<CoreAuthRoleEntity> implements
		IInternalRole {

	public RoleProxy(ResourceItem<?, CoreAuthRoleEntity, ?> resourceItemRef) {
		super(resourceItemRef);
	}

	public final long[] getOperationACL(ContextImpl<?, ?, ?> context, GUID orgID) {
		return this.resourceItemRef.getImpl().getOperationACL(context, orgID);
	}

	public final long[][] getOperationACLs(ContextImpl<?, ?, ?> context,
			GUID orgID) {
		return new long[][] { this.getOperationACL(context, orgID) };
	}

	public final long[] getAccreditACL(ContextImpl<?, ?, ?> context, GUID orgID) {
		return this.resourceItemRef.getImpl().getAccreditACL(context, orgID);
	}

	public final long[][] getAccreditACLs(ContextImpl<?, ?, ?> context,
			GUID orgID) {
		return new long[][] { this.getAccreditACL(context, orgID) };
	}

}