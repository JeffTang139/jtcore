package org.eclipse.jt.core;

import java.util.Locale;

import org.eclipse.jt.core.def.info.InfoDefine;


/**
 * ���ػ���
 * 
 * @author Jeff Tang
 * 
 */
public interface Localizer {
	/**
	 * ��õ�ǰ�ĵ���
	 */
	public Locale getLocale();

	/**
	 * ���ػ��ַ���
	 */
	public String localize(InfoDefine info);

	/**
	 * ���ػ��ַ���
	 */
	public String localize(InfoDefine info, Object param1);

	/**
	 * ���ػ��ַ���
	 */
	public String localize(InfoDefine info, Object param1, Object param2);

	/**
	 * ���ػ��ַ���
	 */
	public String localize(InfoDefine info, Object param1, Object param2,
	        Object param3);

	/**
	 * ���ػ��ַ���
	 */
	public String localize(InfoDefine info, Object param1, Object param2,
	        Object param3, Object... others);

	/**
	 * ���ػ��ַ���
	 */
	public void localize(Appendable to, InfoDefine info);

	/**
	 * ���ػ��ַ���
	 */
	public void localize(Appendable to, InfoDefine info, Object param1);

	/**
	 * ���ػ��ַ���
	 */
	public void localize(Appendable to, InfoDefine info, Object param1,
	        Object param2);

	/**
	 * ���ػ��ַ���
	 */
	public void localize(Appendable to, InfoDefine info, Object param1,
	        Object param2, Object param3);

	/**
	 * ���ػ��ַ���
	 */
	public void localize(Appendable to, InfoDefine info, Object param1,
	        Object param2, Object param3, Object... others);
}
