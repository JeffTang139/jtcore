package org.eclipse.jt.core.impl;

/**
 * 数据输出段
 * 
 * @author Jeff Tang
 */
public interface DataOutputFragment {

	/**
	 * 获取数据输出段剩余空间大小，剩余空间为未写入数据的段长度
	 * 
	 * @return 返回数据输出段剩余空间大小
	 */
	public int remain();

	/**
	 * 向数据输出段中写入一个byte数据
	 * 
	 * <pre>
	 *     写入成功后，自动修改段内的读取指针，该方法不对写入指针的有
	 * 效性进行验证，可能会造成段越界，因此在调用该方法前，建议先调用
	 * remain方法获取数据输出段剩余空间大小，以决定是否继续写数。
	 * </pre>
	 * 
	 * @param value
	 *            待写入的数据
	 */
	public void writeByte(byte value);

	/**
	 * 向数据输出段中写入一个short数据
	 * 
	 * <pre>
	 *     写入成功后，自动修改段内的读取指针，该方法不对写入指针的有
	 * 效性进行验证，可能会造成段越界，因此在调用该方法前，建议先调用
	 * remain方法获取数据输出段剩余空间大小，以决定是否继续写数。
	 * </pre>
	 * 
	 * @param value
	 *            待写入的数据
	 */
	public void writeShort(short value);

	/**
	 * 向数据输出段中写入一个char数据
	 * 
	 * <pre>
	 *     写入成功后，自动修改段内的读取指针，该方法不对写入指针的有
	 * 效性进行验证，可能会造成段越界，因此在调用该方法前，建议先调用
	 * remain方法获取数据输出段剩余空间大小，以决定是否继续写数。
	 * </pre>
	 * 
	 * @param value
	 *            待写入的数据
	 */
	public void writeChar(char value);

	/**
	 * 向数据输出段中写入一个int数据
	 * 
	 * <pre>
	 *     写入成功后，自动修改段内的读取指针，该方法不对写入指针的有
	 * 效性进行验证，可能会造成段越界，因此在调用该方法前，建议先调用
	 * remain方法获取数据输出段剩余空间大小，以决定是否继续写数。
	 * </pre>
	 * 
	 * @param value
	 *            待写入的数据
	 */
	public void writeInt(int value);

	/**
	 * 向数据输出段中写入一个float数据
	 * 
	 * <pre>
	 *     写入成功后，自动修改段内的读取指针，该方法不对写入指针的有
	 * 效性进行验证，可能会造成段越界，因此在调用该方法前，建议先调用
	 * remain方法获取数据输出段剩余空间大小，以决定是否继续写数。
	 * </pre>
	 * 
	 * @param value
	 *            待写入的数据
	 */
	public void writeFloat(float value);

	/**
	 * 向数据输出段中写入一个long数据
	 * 
	 * <pre>
	 *     写入成功后，自动修改段内的读取指针，该方法不对写入指针的有
	 * 效性进行验证，可能会造成段越界，因此在调用该方法前，建议先调用
	 * remain方法获取数据输出段剩余空间大小，以决定是否继续写数。
	 * </pre>
	 * 
	 * @param value
	 *            待写入的数据
	 */
	public void writeLong(long value);

	/**
	 * 向数据输出段中写入一个double数据
	 * 
	 * <pre>
	 *     写入成功后，自动修改段内的读取指针，该方法不对写入指针的有
	 * 效性进行验证，可能会造成段越界，因此在调用该方法前，建议先调用
	 * remain方法获取数据输出段剩余空间大小，以决定是否继续写数。
	 * </pre>
	 * 
	 * @param value
	 *            待写入的数据
	 */
	public void writeDouble(double value);

}
