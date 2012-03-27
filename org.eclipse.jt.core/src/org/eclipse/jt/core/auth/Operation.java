package org.eclipse.jt.core.auth;

/**
 * ��������ӿ�
 * 
 * @param <TFacade>
 *            ������Ӧ����Դ�����
 * @author Jeff Tang 2009-11
 */
public interface Operation<TFacade> {

	/**
	 * ��ȡ��������<br>
	 * һ��������ʾ��
	 * 
	 * @return ���ز�������
	 */
	public String getTitle();

	/**
	 * ��ȡ������������<br>
	 * ��Ȩ������������룬ֻ�е�16λ��Ч��<br>
	 * Ҳ����˵��ඨ��16�ֶ����Ĳ�������ϲ������㣩��
	 * 
	 * <pre>
	 * index 0 : 1
	 * index 1 : 1 &lt;&lt; 1
	 * index 2 : 1 &lt;&lt; 2
	 * ...
	 * (index 1 | index 2) : (1 &lt;&lt; 1) &amp; (1&lt;&lt;2)
	 * </pre>
	 * 
	 * @return ���ز�����������
	 */
	public int getMask();

}
