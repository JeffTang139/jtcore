package org.eclipse.jt.core.resource;

/**
 * ��Դ������
 * 
 * @author Jeff Tang
 * 
 * @param <TFacade>
 *            ��Դ���
 * @param <TImpl>
 *            ��Դ�޸���
 * @param <TKeysHolder>
 *            ��Դ��Դ
 * @param <TResourceMetaData>
 *            ��Դԭ����
 */
public interface ResourceInserter<TFacade, TImpl extends TFacade, TKeysHolder>
		extends ResourcePutter<TFacade, TImpl, TKeysHolder> {
	/**
	 * ���������Դ�Ŀ���
	 * 
	 * @param <TOwnerFacade>������Դ�ľ��
	 * @param ownerFacadeClass
	 *            ������Դ��ȡ�ӿ���
	 * @return ���������Դ�Ŀ���
	 */
	public <TOwnerFacade> ResourceToken<TOwnerFacade> getOwnerResource(
			Class<TOwnerFacade> ownerFacadeClass);
}