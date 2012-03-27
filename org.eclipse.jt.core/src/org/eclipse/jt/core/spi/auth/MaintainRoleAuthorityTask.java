package org.eclipse.jt.core.spi.auth;

import org.eclipse.jt.core.type.GUID;

public final class MaintainRoleAuthorityTask extends MaintainActorAuthorityTask {

	public MaintainRoleAuthorityTask(GUID roleID, GUID orgID) {
		super(roleID, orgID, true);
	}
	
	public MaintainRoleAuthorityTask(GUID roleID, GUID orgID, boolean operationAuthority) {
		super(roleID, orgID, operationAuthority);
	}

}
