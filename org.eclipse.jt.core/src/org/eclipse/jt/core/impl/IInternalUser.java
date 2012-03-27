package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.User;

/**
 * 内部用户接口
 * 
 * @see org.eclipse.jt.core.User
 * @see org.eclipse.jt.core.impl.IInternalActor
 * @author Jeff Tang 2010-01
 */
interface IInternalUser extends IInternalActor, User {

	/**
	 * =判断用户是否是内置用户
	 * 
	 * @return
	 */
	public boolean isBuildInUser();

}
