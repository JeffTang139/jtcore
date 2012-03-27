package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.exception.NullArgumentException;

/**
 * 本地化信息的
 * 
 * @author Jeff Tang
 * 
 */
final class LocaleInfoImpl {
	private short[] frmtInfo;
	final String language;
	final String message;
	final LocaleInfoImpl next;

	LocaleInfoImpl(String language, String message, LocaleInfoImpl next) {
		if (message == null || message.length() == 0) {
			throw new NullArgumentException("message");
		}
		if (language == null || language.length() == 0) {
			throw new NullArgumentException("language");
		}
		this.message = message;
		this.language = language;
		this.next = next;
	}

	final short[] ensureFormatInfo(InfoDefineImpl infoDefine) {
		short[] frmtInfo = this.frmtInfo;
		if (frmtInfo == null) {
			this.frmtInfo = frmtInfo = infoDefine.buildFrmtInfo(this.message,
					false);
		}
		return frmtInfo;
	}
}
