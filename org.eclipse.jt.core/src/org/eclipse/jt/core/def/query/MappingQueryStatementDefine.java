package org.eclipse.jt.core.def.query;

import org.eclipse.jt.core.def.obja.StructDefine;

/**
 * 影射查询定义，用以定义模型与数据库间数据的影射关系的查询
 * 
 * @author Jeff Tang
 * 
 */
public interface MappingQueryStatementDefine extends QueryStatementDefine {

	/**
	 * 获得字段定义属于的模型定义
	 * 
	 * @return 返回模型定义
	 */
	public StructDefine getMappingTarget();

	/**
	 * 是否自动绑定实体字段
	 * 
	 * @return
	 */
	public boolean isAutoBind();
}
