package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.query.QuQueryRefDeclare;

/**
 * ��ѯ��ʹ�õĲ�ѯ���õ��ڲ��ӿ�
 * 
 * @author Jeff Tang
 * 
 */
interface QuQueryRef extends QuRelationRef, QueryRef, QuQueryRefDeclare {

	DerivedQueryImpl getTarget();

	static final String xml_element_query = "derived-query";
}
