package org.eclipse.jt.core.apc;

import org.eclipse.jt.core.def.apc.Accessibility;
import org.eclipse.jt.core.def.apc.CheckPointDefine;

/**
 * 检查点实例
 * 
 * @author Jeff Tang
 * 
 */
public interface CheckPoint {
	/**
	 * 获得需要检查的操作
	 */
	public CheckPointDefine getDefine();

	/**
	 * 获得当前场景
	 */
	public Scene getScene();

	/**
	 * 更新检查的结果，由检查系统回调
	 */
	public void updateAccessibility(Accessibility accessibility);
}
