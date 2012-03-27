package org.eclipse.jt.core.def.info;

import org.eclipse.jt.core.def.NamedDefine;
import org.eclipse.jt.core.def.NamedElementContainer;

/**
 * 信息定义接口，用于向系统报告异常，日志，消息。
 */
public interface InfoDefine extends NamedDefine {
	/**
	 * 获得信息的类型
	 * 
	 * @return 返回信息的类型
	 */
	public InfoKind getKind();

	/**
	 * 获得格式化消息文本 <br>
	 * 参数以"{参数名[:type]}"形式表现
	 * 
	 * @return 返回格式化消息
	 */
	public String getMessage();

	/**
	 * 获取是否需要记录日志，默认不记录日志
	 */
	public boolean isNeedLog();

	/**
	 * 获取是否需要通告用户，默认的设置参看InfoKind.defaultReportToUser
	 */
	public boolean isReportToUser();

	/**
	 * 得到参数容器
	 * 
	 * @return 返回参数容器
	 */
	public NamedElementContainer<? extends InfoParameterDefine> getParameters();
}
