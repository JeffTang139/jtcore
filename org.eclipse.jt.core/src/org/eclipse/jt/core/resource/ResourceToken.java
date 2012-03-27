package org.eclipse.jt.core.resource;

import org.eclipse.jt.core.impl.ResourceTokenMissing;

/**
 * ��Դ��ʶ
 * 
 * 
 * @param <TFacade>
 */
public interface ResourceToken<TFacade> extends ResourceStub<TFacade> {
    /**
     * ��ø�����Դ��ʶ(��Դ���ϼ�������Դ����������Դ)
     * 
     * @param <TSuperFacade>
     *            ������Դ�������
     * @param superTokenFacadeClass
     *            ������Դ�����
     * @return ���ظ�����Դ��ʶ��������Դ����null
     * @throws IllegalArgumentException
     *             �����������ĸ�����Դ���׳��쳣
     */
    public <TSuperFacade> ResourceToken<TSuperFacade> getSuperToken(
            Class<TSuperFacade> superTokenFacadeClass)
            throws IllegalArgumentException;

    /**
     * ����ĳ��ֱ���¼���Դ������(��Դ���¼�������Դ��������Դ)
     * 
     * @param <TSubFacade>
     *            �������
     * @param subTokenFacadeClass
     *            ���������
     * @return ���ص�һ�������ڵ㣬����null��ʾû�к���
     * @throws IllegalArgumentException
     *             �����������ĸ�����Դ���׳��쳣
     */
    public <TSubFacade> ResourceTokenLink<TSubFacade> getSubTokens(
            Class<TSubFacade> subTokenFacadeClass)
            throws IllegalArgumentException;

    /**
     * ���ر�����Դ��ֱ���¼�����(��Դ���¼�)
     * 
     * @return ���ص�һ�������ڵ㣬����null��ʾû�к���
     */
    public ResourceTokenLink<TFacade> getChildren();

    /**
     * ��ñ�����Դ�ĸ��ڵ�(��Դ���ϼ�)
     * 
     * @return ���ظ���ʶ����null
     */
    public ResourceToken<TFacade> getParent();

    /**
     * �ձ�ʶ���޷���λ��Դֵ��
     */
    @SuppressWarnings("unchecked")
    public static final ResourceToken MISSING = ResourceTokenMissing.TOKEN;
}
