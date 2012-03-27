package org.eclipse.jt.core.da;

import java.sql.SQLException;

/**
 * 数据集接口
 * 
 * @author Jeff Tang
 * 
 */
public interface RecordSet {

	/**
	 * 记录集是否为空
	 */
	public boolean isEmpty();

	/**
	 * 将记录集指针移动的到第一个位置
	 * 
	 * @return 返回是否有效位置
	 */
	public boolean first();

	/**
	 * 将指针移动到前一个位置
	 * 
	 * @return 返回是否有效位置
	 */
	public boolean previous();

	/**
	 * 将指针移动到下一个位置
	 * 
	 * @return 返回是否有效位置
	 */
	public boolean next();

	/**
	 * 是否位于记录集最后一行
	 * 
	 * @return 返回是否有效位置
	 */
	@Deprecated
	public boolean isLast();

	/**
	 * 返回当前记录集行数
	 */
	public int getRecordCount();

	/**
	 * 按相对行数移动指针
	 * 
	 * @param rows
	 * @return
	 */
	public boolean relative(int rows);

	/**
	 * 将记录集指针移动到指定行序号
	 * 
	 * @param index
	 *            行序号,从0开始
	 * @return
	 */
	public boolean absolute(int index);

	/**
	 * 获得当前位置序号，从0开始
	 */
	public int getPosition();

	/**
	 * 获得记录对象对应的位置，如果已经删除则返回-1
	 */
	public int positionOfRO(Object ro);

	/**
	 * 获得当前记录的状态，如果当前记录无效则抛出异常
	 */
	public RecordState getRecordState();

	/**
	 * 增加一行空记录,
	 */
	public void append();

	/**
	 * 删除当前记录，指针移动到下一个可用位置，如果是删除最后一条记录则指针位置为EOF
	 */
	public boolean delete();

	/**
	 * 纪录集的字段集合
	 * 
	 * @return
	 */
	public RecordSetFieldContainer<? extends RecordSetField> getFields();

	/**
	 * 获得当前记录对象,作为高级使用<br>
	 * 直接操作记录对象将导致记录的modify状态不一致。之后更新数据库时有可能会失效。
	 */
	public Object getCurrentRO();

	/**
	 * 将记录集的游标设置为某记录对象，需要在之前由getCurrentRO()获得。<br>
	 * 该方法内部会遍历记录列表定位记录，因此在记录数较多时会影响效率
	 * 
	 * @param ro
	 *            之前由getCurrentRO()获得的记录对象。
	 * @return 返回该对象是否还未被删除，已经被删除则返回false，否则返回true
	 */
	public boolean setCurrentRO(Object ro);

	/**
	 * 更新数据集的修改
	 * 
	 * @param adapter
	 *            数据库适配器
	 * @return 返回更新影响的行数
	 */
	public int update(DBAdapter adapter) throws SQLException;

	/**
	 * 重新打开数据集
	 * 
	 * @param adapter
	 *            数据库适配器
	 * @param argumetns
	 *            参数列表
	 */
	public void reQuery(DBAdapter adapter, Object... argumetns)
			throws SQLException;

	/**
	 * 重新打开数据集
	 * 
	 * @param dbCommand
	 *            打开该数据集、或与该数据集兼容的命令对象
	 */
	public void reQuery(DBCommand dbCommand) throws SQLException;
}
