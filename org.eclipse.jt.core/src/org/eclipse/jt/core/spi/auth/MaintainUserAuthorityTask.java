package org.eclipse.jt.core.spi.auth;

import org.eclipse.jt.core.type.GUID;

public final class MaintainUserAuthorityTask extends MaintainActorAuthorityTask {

	public MaintainUserAuthorityTask(GUID userID, GUID orgID) {
		super(userID, orgID, true);
	}
	
	
	public MaintainUserAuthorityTask(GUID userID, GUID orgID, boolean operationAuthority) {
		super(userID, orgID, operationAuthority);
	}

}
