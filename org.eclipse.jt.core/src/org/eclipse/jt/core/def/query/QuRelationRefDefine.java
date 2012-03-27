package org.eclipse.jt.core.def.query;

/**
 * 查询定义中使用的关系引用定义
 * 
 * <p>
 * 即仅仅在查询定义中使用的关系引用.为最顶层接口.
 * 
 * @author Jeff Tang
 */
public interface QuRelationRefDefine extends RelationRefDefine {

	/**
	 * 该表引用是否支持更新
	 * 
	 * <p>
	 * 当表引用设置为支持更新时，在查询定义的查询列中，必须包含目标逻辑表的主键信息.
	 */
	public boolean getForUpdate();

	/**
	 * 强制转换为TableReference类型
	 * 
	 * <p>
	 * 确认isTableReference返回true,否则抛出异常
	 * 
	 * @return
	 */
	@Deprecated
	public QuTableRefDefine castAsTableRef();

	/**
	 * 强制转换为QueryReference类型
	 * 
	 * <p>
	 * 确认isQueryReference返回true,否则抛出异常
	 * 
	 * @return
	 */
	@Deprecated
	public QuQueryRefDefine castAsQueryRef();

}
