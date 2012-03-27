package org.eclipse.jt.core.model;

import org.eclipse.jt.core.def.Container;
import org.eclipse.jt.core.def.model.ModelDefine;

/**
 * 模型访问器，用于接受事件，组织主从模型等。
 * 
 * @author Jeff Tang
 * 
 */
public interface ModelMonitor {
	/**
	 * 获得模型定义
	 */
	public ModelDefine getModelDefine();

	/**
	 * 获得从模型定义
	 */
	public Container<ModelMonitor> getSubMonitor();

}
