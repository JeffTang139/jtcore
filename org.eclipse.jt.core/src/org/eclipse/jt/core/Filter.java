package org.eclipse.jt.core;

public interface Filter<TItem> {

	/**
	 * �жϹ������Ƿ����ĳ��
	 * 
	 * @param item
	 * @return ���ع������Ƿ����ĳ��
	 */
	public boolean accept(TItem item);
}
