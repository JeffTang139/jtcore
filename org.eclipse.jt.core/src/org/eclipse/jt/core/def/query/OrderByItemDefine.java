package org.eclipse.jt.core.def.query;

import org.eclipse.jt.core.def.DefineBase;
import org.eclipse.jt.core.def.exp.ValueExpression;

/**
 * �������
 * 
 * @author Jeff Tang
 * 
 */
public interface OrderByItemDefine extends DefineBase {

	/**
	 * �����������Ƿ�������
	 * 
	 * @return
	 */
	public boolean isDesc();

	/**
	 * �����������������ʽ
	 * 
	 * @return
	 */
	public ValueExpression getExpression();
}
