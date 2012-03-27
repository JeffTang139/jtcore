package org.eclipse.jt.core.spi.setl;

import org.eclipse.jt.core.def.table.TableDefine;
import org.eclipse.jt.core.def.table.TableFieldDefine;
import org.eclipse.jt.core.type.GUID;


/**
 * 提取对某报表的影响
 * 
 * @author Jeff Tang
 * 
 */
public class SETLRPTEffect {
	/**
	 * 获得影响的表
	 */
	public TableDefine table;

	/**
	 * 影响到的字段
	 */
	public TableFieldDefine[] fields;
	/**
	 * 单位ID列
	 */
	public GUID[] unitIDs;
	/**
	 * 时间字符窜列
	 */
	public String[] timeStrings;
	/**
	 * 方案列
	 */
	public GUID[] rptSolutionIDs;
}
