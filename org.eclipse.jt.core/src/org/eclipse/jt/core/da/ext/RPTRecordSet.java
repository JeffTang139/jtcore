package org.eclipse.jt.core.da.ext;

import org.eclipse.jt.core.da.DBAdapter;
import org.eclipse.jt.core.def.table.TableFieldDefine;
import org.eclipse.jt.core.impl.RPTRecordSetImpl.FactoryImpl;
import org.eclipse.jt.core.misc.MissingObjectException;

/**
 * 报表专用数据集
 * 
 * @author Jeff Tang
 * 
 */
public interface RPTRecordSet {

	/**
	 * 该接口的工厂
	 */
	public interface Factory {

		public RPTRecordSet newRPTRecordSet();
	}

	/**
	 * 该接口的工厂
	 */
	public final static Factory factory = new FactoryImpl();

	// //////////////////////////////////
	// 数据集定义
	// //////////////////////////////////
	/**
	 * 清空定义
	 */
	public void reset();

	// /////////////////////////////////////
	// // 约束相关
	// ////////////////////////////////////
	/**
	 * 获得记录集默认的约束<br>
	 * 默认约束的每个键约束一般通过RPTRecordSetKey.getDefaultKeyRestriction()获得更方便<br>
	 */
	public RPTRecordSetRestriction getFirstRestriction();

	/**
	 * 根据键约束分配独立约束，供新建字段时使用<br>
	 * 如果字段指定了独立的约束，则该独立约束中值为空的键约束使用RPTRecordSet的默认约束
	 */
	public RPTRecordSetRestriction newRestriction();

	// /////////////////////////////////////
	// // 字段相关
	// ////////////////////////////////////

	/**
	 * 返回字段个数
	 */
	public int getFieldCount();

	/**
	 * 新建记录字段，使用默认的约束
	 */
	public RPTRecordSetField newField(TableFieldDefine tableField);

	/**
	 * 获得某位置的字段
	 */
	public RPTRecordSetField getField(int index);

	// /////////////////////////////////////
	// // 排序相关
	// ////////////////////////////////////
	/**
	 * 获得OrderBy的个数
	 */
	public int getOrderByCount();

	/**
	 * 添加排序项
	 * 
	 * @param desc
	 *            是否降序
	 */
	public RPTRecordSetOrderBy newOrderBy(RPTRecordSetColumn column,
			boolean isDesc);

	public RPTRecordSetOrderBy newOrderBy(RPTRecordSetColumn column,
			boolean isDesc, boolean isNullAsMIN);

	/**
	 * 添加升序排序项
	 */
	public RPTRecordSetOrderBy newOrderBy(RPTRecordSetColumn column);

	/**
	 * 获得OrderBy
	 */
	public RPTRecordSetOrderBy getOrderBy(int index);

	// /////////////////////////////////////
	// // 键相关
	// ////////////////////////////////////
	/**
	 * 获取键个数
	 */
	public int getKeyCount();

	/**
	 * 获取键
	 */
	public RPTRecordSetKey getKey(int index);

	/**
	 * 根据键名称查找键，找不到则返回null
	 */
	public RPTRecordSetKey findKey(String keyName);

	/**
	 * 根据键名称查找键,找不到则抛出异常
	 */
	public RPTRecordSetKey getKey(String keyName) throws MissingObjectException;

	// /////////////////////////////////////
	// // 数据相关
	// ////////////////////////////////////
	/**
	 * 装载数据集
	 * 
	 * @return 返回记录个数
	 */
	public int load(DBAdapter dbAdapter);

	/**
	 * 装载数据集
	 * 
	 * @param dbAdapter
	 *            数据库适配器
	 * @param offset
	 *            要求返回的纪录的偏移量
	 * @param rowCount
	 *            要求返回的纪录的个数
	 * @return 返回记录个数
	 */
	public int load(DBAdapter dbAdapter, int offset, int rowCount);

	/**
	 * 获取数据库中符合条件的记录个数
	 * 
	 * @param dbAdapter
	 *            适配器
	 * @return 返回数据库中符合条件的记录个数
	 */
	public int getRecordCountInDB(DBAdapter dbAdapter);

	/**
	 * 获得记录个数
	 */
	public int getRecordCount();

	/**
	 * 获得当前记录位置
	 */
	public int getCurrentRecordIndex();

	/**
	 * 设置当前记录位置
	 */
	public void setCurrentRecordIndex(int recordIndex);

	/**
	 * 新建条目，并设置为当前位置
	 * 
	 * @return 返回新记录的位置
	 */
	public int newRecord();

	/**
	 * 删除记录
	 */
	public void remove(int recordIndex);

	/**
	 * 删除当前记录
	 */
	public void removeCurrentRecord();

	/**
	 * 更新数据集
	 * 
	 * @return 返回更新个数
	 */
	public int update(DBAdapter dbAdapter);
}
