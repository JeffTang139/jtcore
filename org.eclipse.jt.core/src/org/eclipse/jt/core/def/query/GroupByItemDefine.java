package org.eclipse.jt.core.def.query;

import org.eclipse.jt.core.def.DefineBase;
import org.eclipse.jt.core.def.exp.ValueExpression;

/**
 * ��ѯ���������
 * 
 * @author Jeff Tang
 * 
 */
public interface GroupByItemDefine extends DefineBase {

	/**
	 * ��ȡ����
	 */
	public ValueExpression getExpression();
}
