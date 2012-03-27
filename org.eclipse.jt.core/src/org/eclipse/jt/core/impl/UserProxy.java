package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.auth.Role;
import org.eclipse.jt.core.type.GUID;


/**
 * 用户代理类
 * 
 * @see org.eclipse.jt.core.impl.ActorProxy
 * @see org.eclipse.jt.core.impl.IInternalUser
 * @author Jeff Tang 2009-12
 */
final class UserProxy extends ActorProxy<CoreAuthUserEntity> implements
		IInternalUser {

	public final boolean validatePassword(String password) {
		return this.resourceItemRef.getImpl().validatePassword(password);
	}

	public final boolean validatePassword(GUID password) {
		return this.resourceItemRef.getImpl().validatePassword(password);
	}

	public UserProxy(ResourceItem<?, CoreAuthUserEntity, ?> resourceItemRef) {
		super(resourceItemRef);
	}

	public final int getAssignedRoleCount() {
		return this.resourceItemRef.getImpl().getAssignedRoleCount();
	}

	public final int getPriorityIndex() {
		return this.resourceItemRef.getImpl().getPriorityIndex();
	}

	public final Role getAssignedRole(int index) {
		return this.resourceItemRef.getImpl().getAssignedRole(index);
	}

	private ResourceReferenceEntry<Role, ?, ?> firstRoleRefEntry;

	@SuppressWarnings("unchecked")
	private final synchronized ResourceReferenceEntry<Role, ?, ?> getFirstRoleRefEntry() {
		CoreAuthUserEntity userEntity = this.resourceItemRef.getImpl();
		if (this.firstRoleRefEntry == null
				|| userEntity.roleAssignInfoChanged()) {
			this.firstRoleRefEntry = ((ResourceReferenceStorage<Role>) (this.resourceItemRef
					.getReferences())).first;
			userEntity.resetRoleAssignInfoChanged();
		}
		return this.firstRoleRefEntry;
	}

	public final boolean isBuildInUser() {
		return false;
	}

	public final long[] getOperationACL(ContextImpl<?, ?, ?> context, GUID orgID) {
		return this.resourceItemRef.getImpl().getOperationACL(context, orgID);
	}

	public final long[][] getOperationACLs(ContextImpl<?, ?, ?> context,
			GUID orgID) {
		final int assignRoleCount = this.getAssignedRoleCount();
		final long[] userOperationACL = this.getOperationACL(context, orgID);
		if (assignRoleCount == 0) {
			return new long[][] { userOperationACL };
		} else {
			final long[][] operationACLs = new long[assignRoleCount + 1][];
			operationACLs[0] = userOperationACL;
			ResourceReferenceEntry<Role, ?, ?> refEntry = this
					.getFirstRoleRefEntry();
			int index = 1;
			while (refEntry != null) {
				operationACLs[index++] = ((CoreAuthRoleEntity) (refEntry.resourceItem
						.getImpl())).getOperationACL(context, orgID);
				refEntry = refEntry.next;
			}
			return operationACLs;
		}
	}

	public final long[] getAccreditACL(ContextImpl<?, ?, ?> context, GUID orgID) {
		return this.resourceItemRef.getImpl().getAccreditACL(context, orgID);
	}

	public final long[][] getAccreditACLs(ContextImpl<?, ?, ?> context,
			GUID orgID) {
		final int assignRoleCount = this.getAssignedRoleCount();
		final long[] userAccreditACL = this.getAccreditACL(context, orgID);
		if (assignRoleCount == 0) {
			return new long[][] { userAccreditACL };
		} else {
			final long[][] accreditACLs = new long[assignRoleCount + 1][];
			accreditACLs[0] = userAccreditACL;
			ResourceReferenceEntry<Role, ?, ?> refEntry = this
					.getFirstRoleRefEntry();
			int index = 1;
			while (refEntry != null) {
				accreditACLs[index++] = ((CoreAuthRoleEntity) (refEntry.resourceItem
						.getImpl())).getAccreditACL(context, orgID);
				refEntry = refEntry.next;
			}
			return accreditACLs;
		}
	}

}
