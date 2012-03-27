package org.eclipse.jt.core.type;

import org.eclipse.jt.core.impl.NULLReadableValue;
import org.eclipse.jt.core.impl.UNKNOWNReadableValue;
import org.eclipse.jt.core.impl.ZEROReadableValue;
import org.eclipse.jt.core.type.GUID;


/**
 * 可读值接口
 * 
 * @author Jeff Tang
 * 
 */
public interface ReadableValue extends DataTypable {
	/**
	 * 零值，对象为None.NONE，字符串为""，GUID为GUID.empty，其他类型为0
	 */
	public static final ReadableValue ZERO = ZEROReadableValue.INSTANCE;
	/**
	 * 空值，对象为null，字符串为null，GUID为null，其他类型为0
	 */
	public static final ReadableValue NULL = NULLReadableValue.INSTANCE;
	/**
	 * 各种调用都抛异常
	 */
	public static final ReadableValue UNKNOWN = UNKNOWNReadableValue.INSTANCE;

	/**
	 * 返回是否为空
	 * 
	 * @return 返回是否为空
	 */
	public boolean isNull();

	/**
	 * 读值
	 * 
	 * @return 返回值
	 */
	public Object getObject();

	/**
	 * 获得布尔型
	 * 
	 * @return 返回布尔型
	 */
	public boolean getBoolean();

	/**
	 * 字符类型
	 * 
	 * @return 返回字符类型
	 */
	public char getChar();

	/**
	 * 字节型
	 */
	public byte getByte();

	/**
	 * 短整形
	 */
	public short getShort();

	/**
	 * 获得整数值
	 * 
	 * @return 返回整数值
	 */
	public int getInt();

	/**
	 * 长整形
	 */
	public long getLong();

	/**
	 * 获得日期时间
	 * 
	 * @return 返回日期对应的长整形
	 */
	public long getDate();

	/**
	 * 单精度浮点型
	 */
	public float getFloat();

	/**
	 * 获得双精度数值
	 * 
	 * @return 返回浮点值
	 */
	public double getDouble();

	/**
	 * 获得二进制值
	 * 
	 * @return 获得二进制值
	 */
	public byte[] getBytes();

	/**
	 * 获得字符窜数值
	 * 
	 * @return 返回字符串
	 */
	public String getString();

	/**
	 * 获得GUID类型值
	 */
	public GUID getGUID();
}
