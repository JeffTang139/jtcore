package org.eclipse.jt.core.type;

import org.eclipse.jt.core.type.GUID;

/**
 * 数值或结构类型，区别于抽象的类型，如表，查询等
 * 
 * @author Jeff Tang
 * 
 */
public interface DataType extends Type {

	/**
	 * 获取类型ID
	 */
	public GUID getID();

	/**
	 * 获得类型对应的Java类型
	 * 
	 * @return
	 */
	public Class<?> getJavaClass();

	/**
	 * 获得根类型，如Varchar的根类型是string等
	 */
	public DataType getRootType();

	/**
	 * 判断该类型可否在数据库端转换为目标类型
	 * 
	 * @param target
	 *            目标类型
	 * @return
	 */
	public boolean canDBTypeConvertTo(DataType target);

	/**
	 * 计算当前类型被目标类型的赋值能力
	 * 
	 * @param another
	 *            对方
	 * @return 赋值能力
	 */
	public AssignCapability isAssignableFrom(DataType source);

	/**
	 * 检查是否相同类别的类型,并返回优先级更高的类型
	 */
	@Deprecated
	public DataType calcPrecedence(DataType target);

	/**
	 * 是否是大对象
	 */
	public boolean isLOB();

	/**
	 * 是否是数字类型
	 */
	public boolean isNumber();

	/**
	 * 是否是字符串类型
	 */
	public boolean isString();

	/**
	 * 是否是字节数组类型
	 */
	public boolean isBytes();

	/**
	 * 是否是数组类型
	 */
	public boolean isArray();

	/**
	 * 数据库字段定义的可否使用当前数据类型
	 */
	public boolean isDBType();

	/**
	 * 返回当前类型的数组类型
	 */
	public ArrayDataType arrayOf();
}
