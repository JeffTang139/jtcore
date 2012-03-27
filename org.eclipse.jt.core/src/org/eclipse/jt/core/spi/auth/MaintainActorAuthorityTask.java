package org.eclipse.jt.core.spi.auth;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.invoke.Task;
import org.eclipse.jt.core.type.GUID;


public abstract class MaintainActorAuthorityTask extends Task<MaintainActorAuthorityTask.Method> {

	public enum Method {
		FILL_AUTHORIZED_ITEM,
		UPDATE_AUTHORITY
	}
	
	public final GUID actorID;
	
	public GUID orgID;
	
	public final boolean operationAuthority;
	
	public final List<AuthorityItem> authorizedItemList = new ArrayList<AuthorityItem>();
	
	protected MaintainActorAuthorityTask(GUID actorID, GUID orgID, boolean operationAuthority) {
		if (actorID == null) {
			throw new NullArgumentException("actorID");
		}
		this.actorID = actorID;
		this.orgID = orgID;
		this.operationAuthority = operationAuthority;
	}
	
}
