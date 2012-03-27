package org.eclipse.jt.core.spi.setl;

import org.eclipse.jt.core.def.table.TableFieldDefine;

/**
 * XML分析错误报告器
 * 
 * @author Jeff Tang
 * 
 */
public interface SETLSAXParseReporter {
	/**
	 * 报告指标影射表达式错误消息
	 * 
	 * @param field
	 *            错误相关的指标字段
	 * @param message
	 *            错误消息
	 */
	public void reportError(TableFieldDefine field, String message);

	/**
	 * 报告错误消息(函数定义错误)
	 * 
	 * @param funcName
	 *            错误相关的函数名称
	 * @param section
	 *            出错相关的函数定义部分标识
	 * @param message
	 *            错误消息
	 */
	public void reportError(String funcName, Section section, String message);

	public enum Section {
		/**
		 * 函数名称
		 */
		NAME("函数名称"),
		/**
		 * 来源表
		 */
		SOURCE("来源表"),
		/**
		 * 口径匹配参数项
		 */
		CALIBER("口径匹配参数项"),
		/**
		 * 返回项
		 */
		RETURN("返回项"),
		/**
		 * 单位匹配
		 */
		UNIT("单位匹配"),
		/**
		 * 时期匹配
		 */
		PERIOD("时期匹配"),
		/**
		 * 函数内匹配条件
		 */
		MATCHING("函数内匹配条件"),
		/**
		 * 说明
		 */
		DESCRIPTION("说明");

		public final String title;

		Section(String title) {
			this.title = title;
		}
	}
}
