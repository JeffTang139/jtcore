package org.eclipse.jt.core.def.table;

import org.eclipse.jt.core.def.DeclareBase;

/**
 * �����õ������ֶζ���
 * 
 * @author Jeff Tang
 * 
 */
public interface IndexItemDeclare extends IndexItemDefine, DeclareBase {
	/**
	 * �����Ƿ���
	 * 
	 * @param desc
	 */
	public void setDesc(boolean desc);
}
