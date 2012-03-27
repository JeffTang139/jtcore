package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.auth.Actor;
import org.eclipse.jt.core.type.GUID;


/**
 * 内部访问者接口
 * 
 * @see org.eclipse.jt.core.auth.Actor
 * @author Jeff Tang 2010-01
 */
interface IInternalActor extends Actor {

	/**
	 * 判断访问是否支持权限
	 * 
	 * @return 如果访问者支持权限返回true，否则返回false
	 */
	public boolean supportAuthority();

	/**
	 * 判断访问者是否存在与指定组织机构的映射关系
	 * S
	 * @param orgID
	 *            组织机构ID，不能为空
	 * @return 如果存在返回true，否则返回false
	 */
	public boolean hasMappingOrg(GUID orgID);

	public long[] getOperationACL(ContextImpl<?, ?, ?> context, GUID orgID);

	public long[] getAccreditACL(ContextImpl<?, ?, ?> context, GUID orgID);

	public long[][] getOperationACLs(ContextImpl<?, ?, ?> context, GUID orgID);

	public long[][] getAccreditACLs(ContextImpl<?, ?, ?> context, GUID orgID);

}
