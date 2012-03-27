package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.auth.Authority;
import org.eclipse.jt.core.auth.AuthorizedResourceItem;
import org.eclipse.jt.core.auth.Operation;
import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.type.GUID;


/**
 * 可授权资源项实现类
 * 
 * @see org.eclipse.jt.core.auth.AuthorizedResourceItem
 * @author Jeff Tang 2009-12
 */
class AuthResItemImpl implements AuthorizedResourceItem {
 	
	public final GUID getID() {
		return this.itemGUID;
	}
	
	public final String getTitle() {
		return this.title;
	}
	
	public final Authority getAuthority(Operation<?> operation) {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		final int opMask = operation.getMask();
		int authMask = OperationUtil.calAuthMask(opMask);
		final int result = this.authCode & authMask;
		if (result == 0) {
			return Authority.UNDEFINE;
		}
		if (result == OperationUtil.calAuthCode(opMask, Authority.ALLOW.code)) {
			return Authority.ALLOW;
		}
		return Authority.DENY;
	}

	public final void setAuthority(Operation<?> operation, Authority authority) {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (authority == null) {
			throw new NullArgumentException("authority");
		}
		final int opMask = operation.getMask();
		// TODO 0x3
		this.authCode &= (~OperationUtil
				.calAuthCode(opMask, 0x3));
		this.authCode |= OperationUtil.calAuthCode(opMask, authority.code);
	}

	/**
	 * 构造一个可授权资源项
	 * 
	 * @param itemID
	 *            资源项 long ID
	 * @param title
	 *            资源项标题
	 * @param authCode
	 *            指定操作者对于该资源项的授权码
	 */
	AuthResItemImpl(long itemID, GUID itemGUID, String title, int authCode) {
		this.itemID = itemID;
		this.itemGUID = itemGUID;
		this.title = title;
		this.authCode = authCode;
	}
	
	final long itemID;
	
	final GUID itemGUID;
	
	final String title;
	
	/**
	 * 指定操作者对于该资源项的授权码
	 */
	int authCode;

}
