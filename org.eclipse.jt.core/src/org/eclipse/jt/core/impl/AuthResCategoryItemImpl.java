package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.auth.AuthorizedResourceCategoryItem;
import org.eclipse.jt.core.auth.Operation;
import org.eclipse.jt.core.type.GUID;


/**
 * ����Ȩ��Դ�����ʵ����<br>
 * ����Ȩ��Դ�������һ������Ŀ���Ȩ��Դ���ϵͳΪÿ�����Ȩ��Դ���������ĸ���Դ�
 * 
 * @see org.eclipse.jt.core.auth.AuthorizedResourceCategoryItem
 * @see org.eclipse.jt.core.impl.AuthResItemImpl
 * @author Jeff Tang 2009-12
 */
final class AuthResCategoryItemImpl extends AuthResItemImpl implements
		AuthorizedResourceCategoryItem {

	public final GUID getResourceCategoryID() {
		return this.itemGUID;
	}

	public final Operation<?>[] getResourceOperations() {
		return this.operations;
	}
	
	/**
	 * ����һ������Ȩ��Դ�����
	 * 
	 * @param group
	 *            ��Դ���Ӧ����Դ��
	 */
	AuthResCategoryItemImpl(ResourceGroup<?, ?, ?> group, int authCode) {
		super(group.id, group.groupID, group.categoryTitle, authCode);
		this.operations = group.resourceService.authorizableResourceProvider.operations;
	}

	/**
	 * ��Դ��Դ��������
	 */
	final Operation<?>[] operations;

}
