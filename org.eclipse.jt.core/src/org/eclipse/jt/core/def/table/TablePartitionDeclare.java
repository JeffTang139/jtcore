package org.eclipse.jt.core.def.table;

import org.eclipse.jt.core.def.ModifiableNamedElementContainer;
import org.eclipse.jt.core.exception.NoPartitionDefineException;

/**
 * 表分区声明
 * 
 * @author Jeff Tang
 * 
 */
public interface TablePartitionDeclare extends TablePartitionDefine {

	public ModifiableNamedElementContainer<? extends TableFieldDefine> getPartitionFields();

	/**
	 * 设置分区字段
	 * 
	 * @param field
	 * @param others
	 */
	public void setPartitionFields(TableFieldDefine field,
			TableFieldDefine... others);

	/**
	 * 增加分区字段
	 * 
	 * @param field
	 * @param others
	 */
	public void addPartitionField(TableFieldDefine field,
			TableFieldDefine... others);

	/**
	 * 设置分区建议行数
	 * 
	 * @param suggestion
	 * @throws NoPartitionDefineException
	 */
	public void setParitionSuggestion(int suggestion)
			throws NoPartitionDefineException;

	/**
	 * 设置表定义的最大分区个数
	 * 
	 * <p>
	 * 默认0,即为当前数据库所支持的最大表分区数
	 * 
	 * @param maxPartitionCount
	 * @throws NoPartitionDefineException
	 */
	public void setMaxPartitionCount(int maxPartitionCount)
			throws NoPartitionDefineException;
}
