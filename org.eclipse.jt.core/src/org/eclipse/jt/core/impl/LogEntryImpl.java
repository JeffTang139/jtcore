package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.User;
import org.eclipse.jt.core.spi.log.LogEntry;
import org.eclipse.jt.core.spi.log.LogEntryKind;

/**
 * 日志项
 * 
 * @author Jeff Tang
 * 
 */
final class LogEntryImpl implements LogEntry {

	private final InfoImpl info;
	private final SessionImpl session;
	private final User user;
	private final LogEntryKind kind;
	LogEntryImpl next;

	LogEntryImpl(SessionImpl session, InfoImpl info) {
		this.info = info;
		this.session = session;
		this.user = session.getUser();
		switch (info.define.kind) {
		case HINT:
			this.kind = LogEntryKind.HINT;
			break;
		case WARNING:
			this.kind = LogEntryKind.WARNING;
			break;
		case ERROR:
			this.kind = LogEntryKind.ERROR;
			break;
		case PROCESS:
			this.kind = ((ProcessInfoImpl) info).getLogKind();
			break;
		default:
			throw new IllegalArgumentException("info.define.kind:"
			        + info.define.kind);
		}
	}

	/**
	 * 获得日志项类别
	 */
	public final LogEntryKind getKind() {
		return this.kind;
	}

	/**
	 * 获得信息项
	 */
	public final InfoImpl getInfo() {
		return this.info;
	}

	/**
	 * 获得远程信息
	 */
	public final SessionImpl getRemoteInfo() {
		return this.session;
	}

	/**
	 * 获得会话ID
	 */
	public final long getSessionID() {
		return this.session.id;
	}

	/**
	 * 获得用户信息
	 */
	public final User getUser() {
		return this.user;
	}
}
