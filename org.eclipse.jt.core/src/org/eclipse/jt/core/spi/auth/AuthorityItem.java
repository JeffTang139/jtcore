package org.eclipse.jt.core.spi.auth;

import org.eclipse.jt.core.auth.Authority;
import org.eclipse.jt.core.auth.Operation;
import org.eclipse.jt.core.impl.AbstractAuthorityItem;
import org.eclipse.jt.core.resource.ResourceToken;

public abstract class AuthorityItem extends AbstractAuthorityItem {

	/**
	 * ��ȡ��Ȩ���Ӧ����Դ����������������Դ�����򷵻ؿ�
	 * @return
	 */
	public final ResourceToken<?> getResourceToken() {
		return super.getResourceItem();
	}

	/**
	 * ��ȡ��Դ��Ĳ����б�
	 */
	public final Operation<?>[] getOperations() {
		return super.getOperations();
	}

	/**
	 * �ж���Ȩ���Ƿ��������Ȩ����Ϣ
	 */
	public final boolean filled() {
		return super.filled();
	}

	/**
	 * �жϱ���Ȩ�����Ƿ�Ե�ǰ��Դ�в���Ȩ��
	 */
	public final boolean hasAuthority(Operation<?> operation) {
		return super.hasAuthority(operation);
	}

	/**
	 * �жϱ���Ȩ����Ե�ǰ��Դ�Ĳ���Ȩ���Ƿ��Ǽ̳е���
	 */
	public final boolean isInherit(Operation<?> operation) {
		return super.isInherit(operation);
	}

	/**
	 * �жϵ�ǰ��¼�û��Ƿ�ӵ�жԵ�ǰ��Դ����ȨȨ��
	 */
	public final boolean hasAuthAuthority(Operation<?> operation) {
		return super.hashAuthAuthority(operation);
	}

	/**
	 * ���ñ���Ȩ����Ե�ǰ��Դ��Ȩ��
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
