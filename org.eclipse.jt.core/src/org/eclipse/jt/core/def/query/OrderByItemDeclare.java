package org.eclipse.jt.core.def.query;

import org.eclipse.jt.core.def.DeclareBase;

/**
 * �������
 * 
 * @see org.eclipse.jt.core.def.query.OrderByItemDefine
 * 
 * @author Jeff Tang
 * 
 */
public interface OrderByItemDeclare extends OrderByItemDefine, DeclareBase {

	/**
	 * �����Ƿ��ǵ�������
	 * 
	 * @param value
	 */
	public void setDesc(boolean value);
}
