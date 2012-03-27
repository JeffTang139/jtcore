package org.eclipse.jt.core.def.exp;

import org.eclipse.jt.core.def.table.TableFieldDefine;
import org.eclipse.jt.core.def.table.TableReferenceDefine;

/**
 * ���ֶ����ñ��ʽ
 * 
 * @author Jeff Tang
 * 
 */
public interface TableFieldRefExpr extends RelationColumnRefExpr {

	/**
	 * ��ȡ�ֶζ���
	 */
	public TableFieldDefine getColumn();

	/**
	 * ��ȡ���ڵı����ö���
	 */
	public TableReferenceDefine getReference();

}
