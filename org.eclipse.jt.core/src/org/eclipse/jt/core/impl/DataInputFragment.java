package org.eclipse.jt.core.impl;

/**
 * 数据输入段
 * 
 * @author Jeff Tang
 */
public interface DataInputFragment {

	/**
	 * 获取数据输入段剩余空间大小，剩余空间为未读入数据的长度
	 * 
	 * @return 返回数据输入段剩余空间大小
	 */
	public int remain();

	/**
	 * 从数据输入段中读入一个byte数据
	 * 
	 * <pre>
	 *     读入成功后，自动修改段内的读取指针，该方法不对读取指针的有
	 * 效性进行验证，可能会读取脏数据，因此在调用该方法前，建议先调用
	 * remain方法获取数据输入段剩余空间大小，以决定是否继续读数。
	 * </pre>
	 * 
	 * @return 返回从数据输入段中读入的byte数据
	 */
	public byte readByte();

	/**
	 * 从数据输入段中读入一个short数据
	 * 
	 * <pre>
	 *     读入成功后，自动修改段内的读取指针，该方法不对读取指针的有
	 * 效性进行验证，可能会读取脏数据，因此在调用该方法前，建议先调用
	 * remain方法获取数据输入段剩余空间大小，以决定是否继续读数。
	 * </pre>
	 * 
	 * @return 返回从数据输入段中读入的short数据
	 */
	public short readShort();

	/**
	 * 从数据输入段中读入一个char数据
	 * 
	 * <pre>
	 *     读入成功后，自动修改段内的读取指针，该方法不对读取指针的有
	 * 效性进行验证，可能会读取脏数据，因此在调用该方法前，建议先调用
	 * remain方法获取数据输入段剩余空间大小，以决定是否继续读数。
	 * </pre>
	 * 
	 * @return 返回从数据输入段中读入的char数据
	 */
	public char readChar();

	/**
	 * 从数据输入段中读入一个int数据
	 * 
	 * <pre>
	 *     读入成功后，自动修改段内的读取指针，该方法不对读取指针的有
	 * 效性进行验证，可能会读取脏数据，因此在调用该方法前，建议先调用
	 * remain方法获取数据输入段剩余空间大小，以决定是否继续读数。
	 * </pre>
	 * 
	 * @return 返回从数据输入段中读入的int数据
	 */
	public int readInt();

	/**
	 * 从数据输入段中读入一个float数据
	 * 
	 * <pre>
	 *     读入成功后，自动修改段内的读取指针，该方法不对读取指针的有
	 * 效性进行验证，可能会读取脏数据，因此在调用该方法前，建议先调用
	 * remain方法获取数据输入段剩余空间大小，以决定是否继续读数。
	 * </pre>
	 * 
	 * @return 返回从数据输入段中读入的float数据
	 */
	public float readFloat();

	/**
	 * 从数据输入段中读入一个long数据
	 * 
	 * <pre>
	 *     读入成功后，自动修改段内的读取指针，该方法不对读取指针的有
	 * 效性进行验证，可能会读取脏数据，因此在调用该方法前，建议先调用
	 * remain方法获取数据输入段剩余空间大小，以决定是否继续读数。
	 * </pre>
	 * 
	 * @return 返回从数据输入段中读入的long数据
	 */
	public long readLong();

	/**
	 * 从数据输入段中读入一个double数据
	 * 
	 * <pre>
	 *     读入成功后，自动修改段内的读取指针，该方法不对读取指针的有
	 * 效性进行验证，可能会读取脏数据，因此在调用该方法前，建议先调用
	 * remain方法获取数据输入段剩余空间大小，以决定是否继续读数。
	 * </pre>
	 * 
	 * @return 返回从数据输入段中读入的double数据
	 */
	public double readDouble();

}
