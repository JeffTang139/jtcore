package org.eclipse.jt.core.def.query;

/**
 * 查询定义中使用的查询引用定义
 * 
 * @author Jeff Tang
 * 
 */
public interface QuQueryRefDefine extends QuRelationRefDefine,
		QueryReferenceDefine {

	public DerivedQueryDefine getTarget();
}
