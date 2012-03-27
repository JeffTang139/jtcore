package org.eclipse.jt.core.def.query;

/**
 * 查询定义中使用的查询引用定义
 * 
 * @see org.eclipse.jt.core.def.query.QuQueryRefDefine
 * 
 * @author Jeff Tang
 * 
 */
public interface QuQueryRefDeclare extends QuQueryRefDefine,
		QuRelationRefDeclare, QueryReferenceDeclare {

	public DerivedQueryDeclare getTarget();
}
