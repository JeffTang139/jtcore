package org.eclipse.jt.core.resource;

/**
 * ��Դ���<br>
 * ���԰�װ��Դ�����Լ�����Ϣ
 * 
 * @author Jeff Tang
 * 
 * @param <TFacade>
 *            ��Դ�Ķ�ȡ���棨�ӿڻ���󣩸����͵����з���Ӧ��ֻ��������Դ��ֻ�����ʷ���
 */
public interface ResourceHandle<TFacade> extends ResourceStub<TFacade> {
	/**
	 * �������Դ��ѯ��
	 */
	public ResourceQuerier getOwnedResourceQuerier();

	/**
	 * �����Դ��ʶ
	 */
	public ResourceToken<TFacade> getToken();

	/**
	 * �رվ�����ͷ���
	 */
	public void closeHandle();
	// /**
	// * ��ȡ���õ���Դ���
	// *
	// * @param <TRefFacade>
	// * @param refReaderClass ���õ���Դ��ȡ����
	// * @return ���õ���Դ�����������Ϊ��ʱ��Ҳ���ؿվ��
	// */
	// public <TRefFacade> ResourceHandle<TRefFacade> demandReference(
	// Class<TRefFacade> refReaderClass);
	// /**
	// * ��ȡ���õ���Դ��ȡ��
	// *
	// * @param <TRefFacade>
	// * @param refReaderClass ���õ���Դ���
	// * @return ���õ���Դ��ȡ����������Ϊ��ʱ��Ҳ���ؿա�
	// */
	// public <TRefFacade> TRefFacade getReference(Class<TRefFacade>
	// refFacadeClass);
}
