package org.eclipse.jt.core.def.info;

import org.eclipse.jt.core.def.FieldDefine;
import org.eclipse.jt.core.def.ModifiableNamedElementContainer;
import org.eclipse.jt.core.def.NamedDeclare;
import org.eclipse.jt.core.type.DataType;
import org.eclipse.jt.core.type.Typable;

/**
 * 信息定义接口，用于向系统报告异常，日志，消息。支持多语言。
 */
public interface InfoDeclare extends InfoDefine, NamedDeclare {
	/**
	 * 设置格式化消息文本<br>
	 * 参数以"{参数名}"形式出现在格式化文本中
	 */
	public void setMessage(String value);

	/**
	 * 获取是否需要记录日志
	 */
	public void setNeedLog(boolean value);

	/**
	 * 获取是否需要通告用户
	 */
	public boolean setReportToUser(boolean value);

	/**
	 * 参数容器
	 */
	public ModifiableNamedElementContainer<? extends InfoParameterDeclare> getParameters();

	/**
	 * 创建参数
	 */
	public InfoParameterDeclare newParameter(String name, DataType type);

	/**
	 * 创建参数
	 */

	public InfoParameterDeclare newParameter(String name, Typable typable);

	/**
	 * 创建参数
	 */
	public InfoParameterDeclare newParameter(FieldDefine sample);
}
