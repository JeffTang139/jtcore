package org.eclipse.jt.core.def.model;

/**
 * 脚本定义
 * 
 * @author Jeff Tang
 * 
 */
public interface ScriptDeclare extends ScriptDefine {
	/**
	 * 设置脚本语言
	 * 
	 * @param value 脚本语言
	 */
	public void setLanguage(String value);

	/**
	 * 设置脚本
	 * 
	 * @param value 脚本
	 */
	public void setScript(String value);
}
