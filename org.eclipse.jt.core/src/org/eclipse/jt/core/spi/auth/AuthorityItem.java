package org.eclipse.jt.core.spi.auth;

import org.eclipse.jt.core.auth.Authority;
import org.eclipse.jt.core.auth.Operation;
import org.eclipse.jt.core.impl.AbstractAuthorityItem;
import org.eclipse.jt.core.resource.ResourceToken;

public abstract class AuthorityItem extends AbstractAuthorityItem {

	/**
	 * 获取授权项对应的资源句柄，如果授项是资源类别项，则返回空
	 * @return
	 */
	public final ResourceToken<?> getResourceToken() {
		return super.getResourceItem();
	}

	/**
	 * 获取资源项的操作列表
	 */
	public final Operation<?>[] getOperations() {
		return super.getOperations();
	}

	/**
	 * 判断授权项是否已填充了权限信息
	 */
	public final boolean filled() {
		return super.filled();
	}

	/**
	 * 判断被授权对象是否对当前资源有操作权限
	 */
	public final boolean hasAuthority(Operation<?> operation) {
		return super.hasAuthority(operation);
	}

	/**
	 * 判断被授权对象对当前资源的操作权限是否是继承得来
	 */
	public final boolean isInherit(Operation<?> operation) {
		return super.isInherit(operation);
	}

	/**
	 * 判断当前登录用户是否拥有对当前资源的授权权限
	 */
	public final boolean hasAuthAuthority(Operation<?> operation) {
		return super.hashAuthAuthority(operation);
	}

	/**
	 * 设置被授权对象对当前资源的权限
	 */
	public final void setAuthority(Operation<?> operation, Authority authority) {
		super.setAuthority(operation, authority);
	}

	public final Authority getAuthority(Operation<?> operation) {
		return super.getAuthority(operation);
	}

	protected AuthorityItem(ResourceToken<?> resourceToken) {
		super(resourceToken);
	}

}
