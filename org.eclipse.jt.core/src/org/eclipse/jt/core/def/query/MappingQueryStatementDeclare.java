package org.eclipse.jt.core.def.query;

/**
 * 影射查询定义，用以定义模型与数据库间数据的影射关系的查询
 * 
 * @author Jeff Tang
 * 
 */
public interface MappingQueryStatementDeclare extends
		MappingQueryStatementDefine, QueryStatementDeclare {

	/**
	 * 设置是否自动绑定实体字段
	 * 
	 * <p>
	 * 当设置为自动绑定时,系统检查所有未绑定的实体字段,并将其绑定到相同名称的表字段上
	 * 
	 * @param isAutoBind
	 */
	public void setAutoBind(boolean isAutoBind);

}
