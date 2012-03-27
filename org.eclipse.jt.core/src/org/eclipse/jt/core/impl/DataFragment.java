package org.eclipse.jt.core.impl;

/**
 * 数据段
 * 
 * @author Jeff Tang
 */
public interface DataFragment extends DataInputFragment, DataOutputFragment {
	/**
	 * 获取数据段有效区域起始位的偏移量
	 * 
	 * @return 返回数据段有效区域起始位的偏移量
	 */
	public int getAvailableOffset();

	/**
	 * 获取数据段有效区域的长度
	 * 
	 * @return 返回数据段有效区域的长度
	 */
	public int getAvailableLength();

	/**
	 * 获取数据段数据读写指针的当前位置
	 * 
	 * @return 返回数据段数据读写指针的当前位置
	 */
	public int getPosition();

	/**
	 * 设置数据段数据读写指针的当前位置
	 * 
	 * @param position
	 *            新位置
	 */
	public void setPosition(int position);

	/**
	 * 设置数据段有效数据区域的结束指针位置
	 * 
	 * @param position
	 *            结束指针位置
	 */
	public void limit(int position);

	/**
	 * 获取数据段的剩余空间大小
	 */
	public int remain();

	/**
	 * 将数据段转换为字节数组并返回
	 * 
	 * @return 返回转换后的字节数组，不可能为null
	 */
	public byte[] getBytes();

}