package org.eclipse.jt.core.auth;

import org.eclipse.jt.core.resource.ResourceToken;
import org.eclipse.jt.core.type.GUID;


public interface ActorAuthorityChecker {

	public GUID getOrgID();

	public <TFacade> boolean hasAuthority(Operation<? super TFacade> operation,
			ResourceToken<TFacade> resoureceToken);

	public <TFacade> Authority getAuthority(Operation<? super TFacade> operation,
			ResourceToken<TFacade> resoureceToken);

}
