package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.User;

/**
 * �ڲ��û��ӿ�
 * 
 * @see org.eclipse.jt.core.User
 * @see org.eclipse.jt.core.impl.IInternalActor
 * @author Jeff Tang 2010-01
 */
interface IInternalUser extends IInternalActor, User {

	/**
	 * =�ж��û��Ƿ��������û�
	 * 
	 * @return
	 */
	public boolean isBuildInUser();

}
