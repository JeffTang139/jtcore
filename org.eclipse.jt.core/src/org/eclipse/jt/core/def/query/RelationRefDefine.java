package org.eclipse.jt.core.def.query;

import org.eclipse.jt.core.def.NamedDefine;

/**
 * 关系引用
 * 
 * <p>
 * 关系引用即针对一个关系元定义的存根.关系引用可以认为是个结构基于关系元定义二维表的.
 * 
 * @author Jeff Tang
 * 
 */
@SuppressWarnings({ "unused", "deprecation" })
public interface RelationRefDefine extends NamedDefine, MoRelationRefDefine {

	/**
	 * 获取目标元关系定义
	 * 
	 * @return 关系的元定义
	 */
	public RelationDefine getTarget();

	/**
	 * 是否是表引用
	 * 
	 * @return
	 */
	@Deprecated
	public boolean isTableReference();

	/**
	 * 是否是查询引用
	 * 
	 * @return
	 */
	@Deprecated
	public boolean isQueryReference();

}
