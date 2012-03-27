package org.eclipse.jt.core.misc;

/**
 * ����Ⱦ��XML����Ľӿ�
 * 
 * @author Jeff Tang
 * 
 */
public interface SXRenderable {
	/**
	 * ���ص�ǰ�ڵ��XML�������
	 * 
	 * @return ���ص�ǰ�ڵ��XML�������
	 */
	public String getXMLTagName();

	/**
	 * ʵ�ָ÷���������д��XML
	 * 
	 * @param usages
	 *            HCL
	 * @param element
	 *            ��ǰ�ڵ�Ԫ��
	 */
	public void render(SXElement element);
}
