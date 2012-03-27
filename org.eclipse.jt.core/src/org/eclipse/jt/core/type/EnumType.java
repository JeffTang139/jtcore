package org.eclipse.jt.core.type;

/**
 * 枚举类型
 * 
 * @author Jeff Tang
 * 
 * @param <TEnum>
 */
public interface EnumType<TEnum extends Enum<TEnum>> extends ObjectDataType {

	/**
	 * 枚举的类型
	 * 
	 * @return
	 */
	public Class<TEnum> getEnumClass();

	/**
	 * 枚举的个数
	 */
	public int getCount();

	/**
	 * 获取枚举的值
	 * 
	 * @param i
	 * @return
	 */
	public TEnum getEnum(int ordinal);

	/**
	 * 获取枚举的值
	 * 
	 * @param name
	 * @return
	 */
	public TEnum getEnum(String name);
}
