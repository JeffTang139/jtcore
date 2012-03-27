package org.eclipse.jt.core.spi.metadata;

import org.eclipse.jt.core.misc.MissingObjectException;

/**
 * ��������Ŀ����
 * 
 * @author Jeff Tang
 * 
 */
public interface MetaDataEntry {
	/**
	 * ����
	 */
	public String getName();

	/**
	 * ����
	 */
	public String getDescription();

	/**
	 * ��ð汾
	 */
	public long getVersion();

	/**
	 * ������ݴ�С
	 */
	public int getDataSize();

	/**
	 * �������Ŀ�ĸ���
	 */
	public int getSubCount();

	/**
	 * ��ȡ����Ŀ
	 */
	public MetaDataEntry getSub(int index) throws IndexOutOfBoundsException;

	/**
	 * �������Ʋ�������Ŀ���Ҳ����׳��쳣
	 */
	public MetaDataEntry getSub(String name) throws MissingObjectException;

	/**
	 * �������Ʋ�������Ŀ���Ҳ����򷵻�null
	 */
	public MetaDataEntry findSub(String name);
}
