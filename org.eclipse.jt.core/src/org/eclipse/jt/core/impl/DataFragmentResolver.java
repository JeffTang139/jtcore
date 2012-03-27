package org.eclipse.jt.core.impl;

/**
 * BufferIO������
 * 
 * @author Jeff Tang
 * 
 * @param <TAttachment>��������
 */
public interface DataFragmentResolver<TAttachment> {
	/**
	 * ʧ��ʱ����
	 * 
	 * @param exception
	 *            �쳣
	 * @param attachment
	 *            ����
	 * @throws Throwable
	 *             ���׳��쳣
	 */
	public void onFragmentInFailed(TAttachment attachment) throws Throwable;

	/**
	 * 
	 * ����fragment��<br>
	 * 
	 * @param fragment
	 *            �������Ƭ��
	 * @param attachment
	 *            ����
	 * @return ����true��ʾ������ɣ�����false��ʾ����Ҫ�����Ĵ���
	 * @throws Throwable
	 *             ���׳��쳣
	 */
	public boolean resovleFragment(DataInputFragment fragment,
			TAttachment attachment) throws Throwable;
}
