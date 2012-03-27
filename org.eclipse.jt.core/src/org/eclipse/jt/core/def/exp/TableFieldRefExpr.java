package org.eclipse.jt.core.def.exp;

import org.eclipse.jt.core.def.table.TableFieldDefine;
import org.eclipse.jt.core.def.table.TableReferenceDefine;

/**
 * 表字段引用表达式
 * 
 * @author Jeff Tang
 * 
 */
public interface TableFieldRefExpr extends RelationColumnRefExpr {

	/**
	 * 获取字段定义
	 */
	public TableFieldDefine getColumn();

	/**
	 * 获取所在的表引用定义
	 */
	public TableReferenceDefine getReference();

}
