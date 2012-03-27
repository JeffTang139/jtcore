package org.eclipse.jt.core.def.apc;

import org.eclipse.jt.core.def.NamedElementContainer;

/**
 * 场景定义
 * 
 * @author Jeff Tang
 * 
 */
public interface SceneDefine {
	/**
	 * 获得该场景下的检查点
	 */
	public NamedElementContainer<? extends CheckPointDefine> getCheckPoints();
}
