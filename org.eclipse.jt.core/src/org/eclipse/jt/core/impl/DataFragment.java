package org.eclipse.jt.core.impl;

/**
 * ���ݶ�
 * 
 * @author Jeff Tang
 */
public interface DataFragment extends DataInputFragment, DataOutputFragment {
	/**
	 * ��ȡ���ݶ���Ч������ʼλ��ƫ����
	 * 
	 * @return �������ݶ���Ч������ʼλ��ƫ����
	 */
	public int getAvailableOffset();

	/**
	 * ��ȡ���ݶ���Ч����ĳ���
	 * 
	 * @return �������ݶ���Ч����ĳ���
	 */
	public int getAvailableLength();

	/**
	 * ��ȡ���ݶ����ݶ�дָ��ĵ�ǰλ��
	 * 
	 * @return �������ݶ����ݶ�дָ��ĵ�ǰλ��
	 */
	public int getPosition();

	/**
	 * �������ݶ����ݶ�дָ��ĵ�ǰλ��
	 * 
	 * @param position
	 *            ��λ��
	 */
	public void setPosition(int position);

	/**
	 * �������ݶ���Ч��������Ľ���ָ��λ��
	 * 
	 * @param position
	 *            ����ָ��λ��
	 */
	public void limit(int position);

	/**
	 * ��ȡ���ݶε�ʣ��ռ��С
	 */
	public int remain();

	/**
	 * �����ݶ�ת��Ϊ�ֽ����鲢����
	 * 
	 * @return ����ת������ֽ����飬������Ϊnull
	 */
	public byte[] getBytes();

}