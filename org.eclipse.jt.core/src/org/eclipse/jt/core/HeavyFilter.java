package org.eclipse.jt.core;

/**
 * �ⲿ���������ṩ�������Ļ����Ĺ���
 * 
 * @author Jeff Tang
 * 
 * @param <TItem>
 */
public interface HeavyFilter<TItem> extends Filter<TItem> {
	/**
	 * �жϹ������Ƿ����ĳ��
	 * 
	 * @return ���ع������Ƿ����ĳ��
	 */
	public boolean accept(Context context, TItem item);
}
