package org.eclipse.jt.core.def.query;

import org.eclipse.jt.core.def.table.TableDeclarator;
import org.eclipse.jt.core.def.table.TableDefine;
import org.eclipse.jt.core.def.table.TableRelationDefine;

/**
 * 查询定义中使用的关系引用定义
 * 
 * @see org.eclipse.jt.core.def.query.QuRelationRefDefine
 * 
 * @author Jeff Tang
 */
public interface QuRelationRefDeclare extends QuRelationRefDefine,
		RelationRefDeclare {

	/**
	 * 设置表引用是否支持结果集的更新
	 * 
	 */
	public void setForUpdate(boolean forUpdate);

	@Deprecated
	public QuTableRefDeclare castAsTableRef();

	@Deprecated
	public QuQueryRefDeclare castAsQueryRef();

	public QuJoinedTableRefDeclare newJoin(TableDefine target);

	public QuJoinedTableRefDeclare newJoin(TableDefine target, String name);

	public QuJoinedTableRefDeclare newJoin(TableDeclarator target);

	public QuJoinedTableRefDeclare newJoin(TableDeclarator target, String name);

	public QuJoinedTableRefDeclare newJoin(TableRelationDefine relation);

	public QuJoinedTableRefDeclare newJoin(TableRelationDefine sample,
			String name);

	public QuJoinedQueryRefDeclare newJoin(DerivedQueryDefine query);

	public QuJoinedQueryRefDeclare newJoin(DerivedQueryDefine query, String name);

	/**
	 * 已完全废弃方法,不会执行任何操作,返回空
	 * 
	 * @deprecated
	 */
	@Deprecated
	public OrderByItemDeclare newOrderBy(RelationColumnDefine column);

	/**
	 * 已完全废弃方法,不会执行任何操作,返回空
	 * 
	 * @deprecated
	 */
	@Deprecated
	public OrderByItemDeclare newOrderBy(RelationColumnDefine column,
			boolean isDesc);
}
