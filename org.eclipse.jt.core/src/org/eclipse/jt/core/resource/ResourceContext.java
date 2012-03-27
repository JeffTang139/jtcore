/**
 * 
 */
package org.eclipse.jt.core.resource;

import org.eclipse.jt.core.Context;

public interface ResourceContext<TFacade, TImpl extends TFacade, TKeysHolder>
        extends Context, ResourceModifier<TFacade, TImpl, TKeysHolder> {

	/**
	 * ��THolderFacade���͵���Դ���Ƴ�TFacade���͵���Դ���á�
	 * 
	 * �������<code>absolutely</code>��ֵΪ <code>false</code>����ֻ������ù�ϵ��
	 * ����ɾ��TFacade���͵���Դ������������� <code>absolutely</code>��ֵΪ<code>true</code>����ô��
	 * �ڽ�����ù�ϵ��ͬʱ��Ҳ��Ӹ�����ɾ��TFacade���͵���Դ��
	 * 
	 * �����������ɾ��THolderFacade���͵���Դ��
	 * 
	 * @param <THolderFacade>
	 * @param holder
	 * @param absolutely
	 *            �Ƿ��ڽ�����ù�ϵʱ������ɾ��������Դ��
	 */
	public <THolderFacade> void clearResourceReferences(
	        ResourceToken<THolderFacade> holder, boolean absolutely);

	/**
	 * ����һ����ָ��������ά����Դ�Ĺ������ϵͳĬ�ϵ�categoryΪNone.NONE
	 * 
	 * @param category
	 *            ָ������Դ����
	 * @return
	 */
	public CategorialResourceModifier<TFacade, TImpl, TKeysHolder> usingResourceCategory(
	        Object category);
}
