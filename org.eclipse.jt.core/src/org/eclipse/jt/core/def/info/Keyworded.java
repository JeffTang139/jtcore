package org.eclipse.jt.core.def.info;

/**
 * ���йؼ��ֵĳ���ӿ�
 * 
 * @author Jeff Tang
 * 
 */
public interface Keyworded {
	/**
	 * ��ӹؼ���
	 * 
	 * @param keyword
	 *            �ؼ���
	 */
	public void addKeyword(Enum<?> keyword);

	/**
	 * ����ĳ��ؼ���
	 * 
	 * @param <TKeyword>
	 *            �ؼ�������
	 * @param one
	 *            ��һ���ؼ���
	 * @param others
	 *            ����Ĺؼ���
	 */
	public <TKeyword extends Enum<TKeyword>> void setKeywords(TKeyword one,
			TKeyword... others);

	/**
	 * ����ĳ��ؼ���
	 * 
	 * @param <TKeyword>
	 *            �ؼ�������
	 * @param one
	 *            ��һ���ؼ���
	 * @param others
	 *            ����Ĺؼ���
	 */
	public <TKeyword extends Enum<TKeyword>> void setKeywords(TKeyword one);

	/**
	 * �Ƴ�ĳ��ؼ���
	 * 
	 * @param keywordType
	 *            �ؼ�����
	 */
	public void removeKeywords(Class<? extends Enum<?>> keywordType);

	/**
	 * �Ƴ��ؼ���
	 * 
	 * @return ����֮ǰ�ùؼ����Ƿ����
	 */
	public boolean removeKeyword(Enum<?> keyword);
}
