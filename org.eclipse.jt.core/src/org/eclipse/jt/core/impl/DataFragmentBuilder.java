package org.eclipse.jt.core.impl;

/**
 * ����Ƭ�Ϲ�����
 * 
 * @author Jeff Tang
 * 
 * @param <TAttachment>��������
 */
public interface DataFragmentBuilder<TAttachment> {
	/**
	 * �����Զ�̻�ԭʧ�ܵ�����ֹ
	 */
	public void onFragmentOutError(TAttachment attachment);

	/**
	 * ����������쳣������Ҫ���ã����¿�ʼ���䣩
	 * 
	 * @param attachment
	 *            ����
	 * @return �����Ƿ�֧�ֺ��Ѿ����ã��������true����Ϊ��Ҫ������������
	 */
	public boolean tryResetPackage(TAttachment attachment);

	public void onFragmentOutFinished(TAttachment attachment);

	/**
	 * 
	 * ����fragment��<br>
	 * 
	 * @param fragment
	 *            ����Ƭ�ϣ���Ϊ���湹������buffer
	 * @param attachment
	 *            ����
	 * @return ����true��ʾ������ɣ�����false��ʾ����Ҫ������fragment
	 * @throws Throwable
	 *             ���׳��쳣
	 */
	public boolean buildFragment(DataOutputFragment fragment,
			TAttachment attachment) throws Throwable;

}
