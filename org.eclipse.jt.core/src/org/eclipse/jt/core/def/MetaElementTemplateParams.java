package org.eclipse.jt.core.def;

/**
 * ������ģ��Ĳ����ӿ�
 * 
 * @author Jeff Tang
 * 
 */
public interface MetaElementTemplateParams {
	/**
	 * ʵ��������
	 */
	public String getName();

	/**
	 * ���ʵ������Ĳ���
	 */
	public <TParam> TParam getParam(Class<TParam> paramClass);

	/**
	 * ���ʵ������Ĳ���
	 */
	public <TParam> TParam getParam(Class<TParam> paramClass, int tag);

}
