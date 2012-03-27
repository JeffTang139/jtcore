package org.eclipse.jt.core.spi.auth;

import org.eclipse.jt.core.resource.ResourceToken;
import org.eclipse.jt.core.type.GUID;


@SuppressWarnings("unchecked")
public interface AuthorizableResourceCategoryItem extends ResourceToken {
	
	public GUID getGUID();

	public String getTitle();
	
}
