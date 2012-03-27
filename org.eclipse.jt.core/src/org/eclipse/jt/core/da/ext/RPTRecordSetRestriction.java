package org.eclipse.jt.core.da.ext;

import org.eclipse.jt.core.def.table.TableFieldDefine;

/**
 * 记录集约束，用于限定查询范围
 * 
 * <p>
 * 记录集约束包含了对记录集每个键的约束
 * <ol>
 * <li>RPTRecordSet拥有默认的约束，通过RPTRecordSet.getDefualtRestriction()获得.
 * <li>但默认约束的每个键约束一般通过RPTRecordSetKey.getDefaultKeyRestriction()获得更方便.
 * <li>RPTRecordSet创建查询字段时可以指定约束，不指定则使用RPTRecordSet的默认约束.
 * <li>如果字段指定了独立的约束，则该独立约束中值为空的键约束使用RPTRecordSet的默认约束.
 * <li>由于每个约束会使用单独的数据库查询，需要应用相同约束的字段应该使用同一个约束对象.
 * </ol>
 * 
 * @author Jeff Tang
 * 
 */
public interface RPTRecordSetRestriction {

	/**
	 * 判断本约束是否支持某键
	 * 
	 * @param index
	 *            键的序号
	 * @return
	 */
	public boolean isKeySupported(int index);

	/**
	 * 判断本约束是否支持某键
	 */
	public boolean isKeySupported(RPTRecordSetKey key);

	/**
	 * 获取键约束<br>
	 * RPTRecordSet的默认约束的每个键约束一般通过RPTRecordSetKey.getDefaultKeyRestriction()
	 * 获得更方便<br>
	 */
	public RPTRecordSetKeyRestriction getKeyRestriction(int index);

	/**
	 * 获取键约束<br>
	 * RPTRecordSet的默认约束的每个键约束一般通过RPTRecordSetKey.getDefaultKeyRestriction(
	 * )获得更方便<br>
	 */
	public RPTRecordSetKeyRestriction getKeyRestriction(RPTRecordSetKey key);

	/**
	 * 根据键名称获得键约束
	 */
	public RPTRecordSetKeyRestriction getKeyRestriction(String keyName);

	/**
	 * 清空约束中的值
	 */
	public void clearMatchValues();

	/**
	 * 新建记录字段
	 */
	public RPTRecordSetField newField(TableFieldDefine tableField);
}
