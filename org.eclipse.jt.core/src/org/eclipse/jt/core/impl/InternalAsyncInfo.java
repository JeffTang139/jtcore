package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.service.AsyncInfo.SessionMode;

/**
 * �첽������Ϣ
 * 
 * @author Jeff Tang
 * 
 */
public abstract class InternalAsyncInfo {
	/**
	 * ��ʼ��ʱ��
	 */
	protected long start;
	/**
	 * �ظ�ִ�е�����С�ڻ����0��ʾ���ظ�
	 */
	protected long period;

	protected SessionMode sessionMode;
	/**
	 * �����첽���õ��в�������Ϣ
	 */
	protected boolean careInfos;

	public InternalAsyncInfo(long start, long period, SessionMode sessionMode) {
		if (sessionMode == null) {
			throw new NullArgumentException("sessionMode");
		}
		this.start = start;
		this.period = period;
		this.sessionMode = sessionMode;
	}
}
