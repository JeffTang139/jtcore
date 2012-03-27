package org.eclipse.jt.core.def.query;

import org.eclipse.jt.core.def.table.TableReferenceDeclare;

/**
 * 查询定义中使用的表引用定义
 * 
 * @see org.eclipse.jt.core.def.query.QuTableRefDefine
 * 
 * @author Jeff Tang
 */
public interface QuTableRefDeclare extends QuTableRefDefine,
		QuRelationRefDeclare, TableReferenceDeclare {

}
