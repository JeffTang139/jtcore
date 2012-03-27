package org.eclipse.jt.core.def.info;

/**
 * 信息类型
 * 
 * @author Jeff Tang
 * 
 */
public enum InfoKind {
	/**
	 * 提示信息<br>
	 * 对于业务处理中间过程的一些提示消息<br>
	 * <li>信息不中断后续处理</li><br>
	 */
	HINT(true),
	/**
	 * 警告信息<br>
	 * 程序用来默默记录某些过程的信息<br>
	 * <li>信息不中断后续处理</li><br>
	 */
	WARNING(true),
	/**
	 * 错误信息<br>
	 * 程序中用来向客户报告各类无效的数据和非法的操作时采用的信息报告。<br>
	 * 相比起失败，错误重点描述原因，从而帮助用户改变行为或数据。<br>
	 * <li>信息将中断后续处理</li><br>
	 */
	ERROR(true),
	/**
	 * 过程信息<br>
	 * 对于需要纪录过程细节的复杂信息报告，采用过程类型。<br>
	 * 过程类型信息报告是时期型报告，在报告开始和结束之间的信息报告都是本报告的子报告<br>
	 * 过程报告本身只表达对过程的描述以及时间，过程的成败由嵌套在过程信息中的时点报告表述<br>
	 * <li>信息不中断后续处理，除非嵌套的信息引发了中断</li><br>
	 */
	PROCESS(false);
	/**
	 * 指明该类信息是否默认报告给用户<br>
	 * 
	 * @see InfoDeclare.setNeedToUser()
	 */
	public final boolean defaultReportToUser;

	private InfoKind(boolean defaultReportToUser) {
		this.defaultReportToUser = defaultReportToUser;
	}
}
