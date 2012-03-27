package org.eclipse.jt.core.resource;

import org.eclipse.jt.core.auth.Operation;

/**
 * ��Դ�������ӿڡ�
 * 
 * @author Jeff Tang
 * 
 * @param <TFacade>
 *            ��Դ��ۣ�����Դʵ���ṩ��ֻ���ӿ�
 * @param <TImpl>
 *            ��Դʵ�����ͣ��ȿ��������޸���Դ�Ľӿڻ������ͣ��󲿷�ʱ��ʹ����Դ��ʵ������
 * @param <TKeysHolder>
 *            ��Դ��Դ���ȿ��Դ��еõ���Դ�ļ���ֵ�Ľӿڻ������ͣ��󲿷�ʱ��ʹ����Դ��ʵ������
 */
public interface ResourcePutter<TFacade, TImpl extends TFacade, TKeysHolder> {
	/**
	 * �����Դ���
	 */
	public Object getCategory();

	/**
	 * ����Դ�������õ���Դ�����С�
	 * <p>
	 * ����Ĭ����Դ��ʵ�ֶ���<code>resource</code>ͬʱҲʵ����<code>TFacade</code>��
	 * <code>TKeysHolder</code>���͡�
	 * <p>
	 * ��������<code>resource</code>������ͬ����ͬ������Դ�Ѿ���������Դ�����У�������ԭ���ڶ��󸲸ǡ�
	 * 
	 * @param resource
	 *            �����õ���Դ��ʵ�ֶ���
	 * @return �������õ���Դ��������Դ�����ж�Ӧ�ı��
	 */
	public ResourceToken<TFacade> putResource(TImpl resource);

	/**
	 * ����Դ�������õ���Դ�����С�
	 * <p>
	 * ����Ĭ����Դ��ʵ�ֶ���<code>resource</code>ͬʱҲʵ����<code>TFacade</code>�ӿ����͡�
	 * <p>
	 * �������Դ�������Ѿ����������<code>keys</code>��Ӧ����Դ����������ԭ���ڶ��󸲸ǡ�
	 * 
	 * @param resource
	 *            �����õ���Դ��ʵ�ֶ���
	 * @param keys
	 *            �����õ���Դ�����Ӧ�ļ���
	 * @return �������õ���Դ��������Դ�����ж�Ӧ�ı��
	 */
	public ResourceToken<TFacade> putResource(TImpl resource, TKeysHolder keys);

	/**
	 * ����Դ�������õ���Դ�����У�ͬʱָ���丸����������Դ�����еı�ǣ���������ӵĶ�������Դ�����еı�ǡ�
	 * <p>
	 * ��TImpl���͵���Դ<code>resource</code>������ӵ���Դ������ʱ������Ĭ�϶���<code>resource</code>
	 * ͬʱҲʵ����<code>TFacade</code>��<code>TKeysHolder</code>���͡�
	 * <p>
	 * ��������<code>resource</code>������ͬ����ͬ������Դ�Ѿ���������Դ�����У�������ԭ���ڶ��󸲸ǡ�
	 * ���ң����ԭ���ڵĶ���ĸ��ڵ��������ָ����<code>treeParent</code>������Ҳ���<code>resource</code>
	 * ������Ϊ<code>treeParent</code>���ӽڵ㡣
	 * 
	 * @param parentToken
	 *            �����õ���Դ��������Դ�����еĸ��ڵ�ı��
	 * @param resource
	 *            �����õ���Դ��ʵ�ֶ���
	 * @return �������õ���Դ��������Դ�����ж�Ӧ�ı��
	 */
	public ResourceToken<TFacade> putResource(
			ResourceToken<TFacade> treeParent, TImpl resource);

	/**
	 * ����Դ�������õ���Դ�����У�ͬʱָ�����Ӧ�ļ����Լ�������������Դ�����еı�ǣ���������ӵĶ�������Դ�����еı�ǡ�
	 * <p>
	 * ��TImpl���͵���Դ<code>resource</code>������ӵ���Դ������ʱ������Ĭ�϶���<code>resource</code>
	 * ͬʱҲʵ����<code>TFacade</code>�ӿ����͡�
	 * <p>
	 * �������Դ�������Ѿ����������<code>keys</code>��Ӧ����Դ����������ԭ���ڶ��󸲸ǡ�
	 * ���ң����ԭ���ڵĶ���ĸ��ڵ��������ָ����<code>treeParent</code>������Ҳ���<code>resource</code>
	 * ������Ϊ<code>treeParent</code>���ӽڵ㡣
	 * 
	 * @param parentToken
	 *            �����õ���Դ��������Դ�����еĸ��ڵ�ı��
	 * @param resource
	 *            �����õ���Դ��ʵ�ֶ���
	 * @param keys
	 *            �����õ���Դ�����Ӧ�ļ���
	 * @return �������õ���Դ��������Դ�����ж�Ӧ�ı��
	 */
	public ResourceToken<TFacade> putResource(
			ResourceToken<TFacade> treeParent, TImpl resource, TKeysHolder keys);

	/**
	 * ������Դ��������Դ�����е����νṹ�ϵĸ��ӹ�ϵ��
	 * <p>
	 * ����������ָ������Դ����<code>child</code>����Ϊָ���ĸ��ڵ�<code>treeParent</code>���ӽڵ㡣
	 * <p>
	 * �����Դ����<code>child</code>�����νṹ���Ѿ��и��ڵ㣬�Ҳ���<code>treeParent</code>�����
	 * <code>child</code>�ƶ�����ָ����<code>treeParent</code>�ڵ��¡�
	 * 
	 * @param treeParent
	 *            �µĸ��ڵ����Դ���
	 * @param child
	 *            Ҫ���õ��ӽڵ����Դ���
	 */
	void putResource(ResourceToken<TFacade> treeParent,
			ResourceToken<TFacade> child);

	/**
	 * ������Դ�����е���Դ����֮������ù�ϵ��
	 * <p>
	 * ������������Դ����<code>reference</code>�ŵ�ָ����Դ����<code>holder</code>�С�
	 * <p>
	 * �����������Ӱ��<code>reference</code>���������ã�Ҳ����˵�����<code>reference</code>
	 * �Ѿ�����������holder�У�����Ҳ���Ὣ<code>reference</code>����Щholder���Ƴ���
	 * 
	 * @param <THolderFacade>
	 *            ����<code>reference</code>���õ���Դ���������
	 * @param holder
	 *            ���õı�����
	 * @param reference
	 *            ���ö���
	 */
	<THolderFacade> void putResourceReference(
			ResourceToken<THolderFacade> holder,
			ResourceToken<TFacade> reference);

	<THolderFacade> void putResourceReferenceBy(ResourceToken<TFacade> holder,
			ResourceToken<THolderFacade> reference);

	/**
	 * �Ƴ���Դ�����е���Դ����֮������ù�ϵ��
	 * <p>
	 * �������ֻ�����Դ����֮������ù�ϵ����������Դ������ɾ����Դ����
	 * 
	 * @param <THolderFacade>
	 *            ����<code>reference</code>���õ���Դ���������
	 * @param holder
	 *            ���õı�����
	 * @param reference
	 *            ���ö���
	 */
	<THolderFacade> void removeResourceReference(
			ResourceToken<THolderFacade> holder,
			ResourceToken<TFacade> reference);

	<TReferenceFacade> void removeResourceReferenceBy(
			ResourceToken<TFacade> holder,
			ResourceToken<TReferenceFacade> reference);

	// -----------------------------------------����ΪȨ�����---------------------------------------------------

	/**
	 * �Ƴ���Դ�����е���Դ����֮������ù�ϵ��
	 * <p>
	 * �������ֻ�����Դ����֮������ù�ϵ����������Դ������ɾ����Դ����
	 * 
	 * @param operation
	 *            Ҫ�����Ȩ�޵Ĳ���
	 * @param <THolderFacade>
	 *            ����<code>reference</code>���õ���Դ���������
	 * @param holder
	 *            ���õı�����
	 * @param reference
	 *            ���ö���
	 */
	<THolderFacade> void removeResourceReference(
			Operation<? super TFacade> operation,
			ResourceToken<THolderFacade> holder,
			ResourceToken<TFacade> reference);

	<TReferenceFacade> void removeResourceReferenceBy(
			Operation<? super TReferenceFacade> operation,
			ResourceToken<TFacade> holder,
			ResourceToken<TReferenceFacade> reference);

	// -----------------------------------------����ΪȨ�����---------------------------------------------------

}