package org.eclipse.jt.core.def.table;

import org.eclipse.jt.core.def.DefineBase;
import org.eclipse.jt.core.def.NamedElementContainer;
import org.eclipse.jt.core.exception.NoPartitionDefineException;

/**
 * 表分区定义
 * 
 * @author Jeff Tang
 */
public interface TablePartitionDefine extends DefineBase {

	/**
	 * 是否分区
	 */
	public boolean isPartitioned();

	/**
	 * 分区的行数
	 * 
	 * @return
	 * @throws NoPartitionDefineException
	 */
	public int getPartitionSuggestion() throws NoPartitionDefineException;

	/**
	 * 表定义的最大的分区个数
	 * 
	 * @return
	 * @throws NoPartitionDefineException
	 */
	public int getMaxPartitionCount() throws NoPartitionDefineException;

	/**
	 * 分区的字段
	 */
	public NamedElementContainer<? extends TableFieldDefine> getPartitionFields();
}
