package org.eclipse.jt.core.def.query;

import org.eclipse.jt.core.def.NamedDefine;

/**
 * 关系列定义
 * 
 * @author Jeff Tang
 * 
 */
public interface RelationColumnDefine extends NamedDefine {

	/**
	 * 获取所属的关系定义
	 * 
	 * @return 关系定义
	 */
	public RelationDefine getOwner();
}
