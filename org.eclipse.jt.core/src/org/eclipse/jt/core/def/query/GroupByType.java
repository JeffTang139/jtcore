package org.eclipse.jt.core.def.query;

/**
 * 分组类型
 * 
 * <p>
 * 查询默认分组类型为default
 * 
 * @author Jeff Tang
 * 
 */
public enum GroupByType {

	/**
	 * 默认的分组规则
	 */
	DEFAULT,

	/**
	 * 指定结果集中包含rollup类型的汇总行
	 */
	ROLL_UP,

	/**
	 * 指定结果集中包含cube类型的汇总行
	 * 
	 * @deprecated 不支持
	 */
	@Deprecated
	CUBE;

}
