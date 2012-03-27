package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.auth.Authority;
import org.eclipse.jt.core.auth.Operation;
import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.resource.ResourceToken;
import org.eclipse.jt.core.type.GUID;


public abstract class AbstractAuthorityItem {

	public ResourceItem<?, ?, ?> getResourceItem() {
		return this.resItem;
	}

	public Operation<?>[] getOperations() {
		return this.operations;
	}

	public boolean filled() {
		return this.filled;
	}

	public boolean hasAuthority(Operation<?> operation) {
		Authority result = this.getAuthority(operation);
		if (result == Authority.UNDEFINE) {
			int mask = operation.getMask();
			if ((this.authInfo & mask) == mask) {
				return true;
			} else {
				return false;
			}
		} else if (result == Authority.ALLOW) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isInherit(Operation<?> operation) {
		Authority result = this.getAuthority(operation);
		if (result == Authority.UNDEFINE) {
			return true;
		} else {
			return false;
		}
	}

	public boolean hashAuthAuthority(Operation<?> operation) {
		if (!this.filled) {
			throw new IllegalStateException("授权项尚未填充权限信息");
		}
		final int opMask = operation.getMask();
		if (((this.authInfo >>> 16) & opMask) == opMask) {
			return true;
		}
		return false;
	}

	public void setAuthority(Operation<?> operation, Authority authority) {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (authority == null) {
			throw new NullArgumentException("authority");
		}
		if (!this.filled) {
			throw new IllegalStateException("授权项尚未填充权限信息");
		}
		final int opMask = operation.getMask();
		// TODO 0x3
		this.authCode &= (~OperationUtil.calAuthCode(opMask, 0x3));
		this.authCode |= OperationUtil.calAuthCode(opMask, authority.code);
		this.changed = true;
	}

	public Authority getAuthority(Operation<?> operation) {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (!this.filled) {
			throw new IllegalStateException("授权项尚未填充权限信息");
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

	@SuppressWarnings("unchecked")
	protected AbstractAuthorityItem(ResourceToken<?> resourceToken) {
		if (resourceToken == null) {
			throw new NullArgumentException("resourceToken");
		}
		if (resourceToken instanceof AuthResCateItem) {
			this.resItem = null;
			this.itemID = ((AuthResCateItem) resourceToken).itemID;
			this.isCategoryItem = true;
			this.categoryID = ((AuthResCateItem) resourceToken).itemGUID;
			this.operations = ((AuthResCateItem) resourceToken).operations;
		} else if (resourceToken instanceof ResourceItem) {
			this.resItem = (ResourceItem) resourceToken;
			if (this.resItem.group.isAuthorizable()) {
				this.itemID = this.resItem.id;
				this.isCategoryItem = false;
				this.categoryID = null;
				this.operations = this.resItem.group.resourceService.authorizableResourceProvider.operations;
			} else {
				throw new IllegalArgumentException("当前资源类型不支持权限操作");
			}
		} else {
			throw new UnsupportedOperationException();
		}
	}

	final void setAuthorityInfo(int authCode, int opAuthority, int authAuthority) {
		this.authCode = authCode;
		this.authInfo = ((authAuthority & 0xFFFF) << 16)
				| (opAuthority & 0xFFFF);
		this.filled = true;
	}

	final ResourceItem<?, ?, ?> resItem;

	final long itemID;

	final GUID categoryID;

	final boolean isCategoryItem;

	transient final Operation<?>[] operations;

	int authCode;

	boolean filled = false;

	boolean changed = false;

	int authInfo = 0xFFFF0000;

}
