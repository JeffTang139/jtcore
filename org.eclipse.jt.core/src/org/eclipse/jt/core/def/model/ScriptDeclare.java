package org.eclipse.jt.core.def.model;

/**
 * �ű�����
 * 
 * @author Jeff Tang
 * 
 */
public interface ScriptDeclare extends ScriptDefine {
	/**
	 * ���ýű�����
	 * 
	 * @param value �ű�����
	 */
	public void setLanguage(String value);

	/**
	 * ���ýű�
	 * 
	 * @param value �ű�
	 */
	public void setScript(String value);
}
