package org.eclipse.jt.core.def.model;

/**
 * 脚本定义
 * 
 * @author Jeff Tang
 * 
 */
public interface ScriptDefine {
	/**
	 * 获取脚本的语言
	 * 
	 * @return 返回语言
	 */
	public String getLanguage();

	/**
	 * 获取脚本
	 * 
	 * @return 返回脚本
	 */
	public String getScript();
}
