/**
 * 
 */
package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.User;
import org.eclipse.jt.core.def.obja.StructClass;
import org.eclipse.jt.core.def.obja.StructField;

@StructClass
final class PMV_SessionInfo {
	@StructField(title = "会话ID")
	final long id;
	@StructField(title = "登陆名")
	final String userName;
	@StructField(title = "用户名")
	final String userTitle;
	@StructField(title = "创建时间", asDate = true)
	final long createTime;
	@StructField(title = "最后交互时间", asDate = true)
	final long lastInteractiveTime;

	PMV_SessionInfo(SessionImpl session) {
		this.id = session.id;
		final User user = session.getUser();
		this.userName = user.getName();
		this.userTitle = user.getTitle();
		this.createTime = session.createTime;
		this.lastInteractiveTime = session.getLastInteractiveTime();
	}
}