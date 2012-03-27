package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.query.QuQueryRefDeclare;

/**
 * 查询中使用的查询引用的内部接口
 * 
 * @author Jeff Tang
 * 
 */
interface QuQueryRef extends QuRelationRef, QueryRef, QuQueryRefDeclare {

	DerivedQueryImpl getTarget();

	static final String xml_element_query = "derived-query";
}
