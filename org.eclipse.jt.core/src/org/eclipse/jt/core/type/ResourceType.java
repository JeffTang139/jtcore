package org.eclipse.jt.core.type;

/**
 * ��Դ��������
 * 
 * @author Jeff Tang
 * 
 */
@Deprecated
public interface ResourceType<TFacade> extends Type {
	/**
	 * ��Դ���������
	 */
	public Class<TFacade> getFacadeClass();

	/**
	 * ��Դ�����
	 */
	public Object getCategory();
}
