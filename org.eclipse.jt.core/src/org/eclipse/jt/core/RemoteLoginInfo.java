package org.eclipse.jt.core;

/**
 * Զ�̵�¼��Ϣ
 * 
 * @author Jeff Tang
 * 
 */
public interface RemoteLoginInfo {
    /**
     * ��ÿռ�����Ӧ���м��������
     */
    String getHost();

    /**
     * ��ÿռ�����Ӧ���м���Ķ˿�
     */
    int getPort();

    /**
     * ����Ƿ�ʹ�ð�ȫ��TLS/SSL��������
     */
    boolean isSecure();

    /**
     * ��ȡ�ÿռ����ӵĵ�½�û�
     */
    public String getUser();

    /**
     * ���Զ�̵�¼����������
     */
    public RemoteLoginLife getLife();
}
