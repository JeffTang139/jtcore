package org.eclipse.jt.core.def;

/**
 * ���Ʊ�ʶ�Ķ���������ӿ�
 * 
 * @author Jeff Tang
 * 
 * @param <TDefine>
 */
public interface NamedElementContainer<TElement extends Namable> extends
        Container<TElement> {
	/**
	 * �������Ʋ��Ҷ���
	 * 
	 * @param name
	 *            ��������
	 * @return �ҵ��򷵻ض���ӿڣ����򷵻�null
	 */
	public TElement find(String name);

	/**
	 * �������Ƶõ�����
	 * 
	 * @param name
	 *            ��������
	 * @return �ҵ��򷵻ض���ӿڣ������׳��쳣
	 * @throws MissingDefineException
	 *             �Ҳ�������ʱ�׳��쳣
	 */
	public TElement get(String name) throws MissingDefineException;
}
