package org.eclipse.jt.core.def.query;

import org.eclipse.jt.core.def.Container;
import org.eclipse.jt.core.def.NamedElementContainer;
import org.eclipse.jt.core.type.Type;

/**
 * ��ѯ��䶨��
 * 
 * @author Jeff Tang
 * 
 */
public interface QueryStatementDefine extends SelectDefine, StatementDefine,
		WithableDefine, Type {

	public NamedElementContainer<? extends QueryColumnDefine> getColumns();

	/**
	 * ��ȡ�����������
	 * 
	 * @return δ�����򷵻�null
	 */
	public Container<? extends OrderByItemDefine> getOrderBys();

}
