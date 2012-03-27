/**
 * 
 */
package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.User;
import org.eclipse.jt.core.def.obja.StructClass;
import org.eclipse.jt.core.def.obja.StructField;

@StructClass
final class PMV_SessionInfo {
	@StructField(title = "�ỰID")
	final long id;
	@StructField(title = "��½��")
	final String userName;
	@StructField(title = "�û���")
	final String userTitle;
	@StructField(title = "����ʱ��", asDate = true)
	final long createTime;
	@StructField(title = "��󽻻�ʱ��", asDate = true)
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