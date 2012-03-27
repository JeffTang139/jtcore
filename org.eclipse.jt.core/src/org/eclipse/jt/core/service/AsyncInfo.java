package org.eclipse.jt.core.service;

import java.util.Date;

import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.impl.InternalAsyncInfo;


/**
 * �첽������Ϣ
 * 
 * @author Jeff Tang
 * 
 */
public final class AsyncInfo extends InternalAsyncInfo {
	/**
	 * �Ựģʽ
	 * 
	 * @author Jeff Tang
	 * 
	 */
	public enum SessionMode {
		/**
		 * ��ͬ�ĻỰ�У�
		 */
		SAME,
		/**
		 * ��ͬ�ĻỰ����ͬ���û�������ϵͳ�Ự�·�����첽�������ʹ�������û�
		 */
		INDIVIDUAL,
		/**
		 * ��ͬ�ĻỰ�������û�
		 */
		INDIVIDUAL_ANONYMOUS
	}

	/**
	 * ��ȡ�滭ģʽ
	 */
	public final SessionMode getSessionMode() {
		return super.sessionMode;
	}

	/**
	 * ���ûỰģʽ
	 */
	public final void setSessionMode(SessionMode sessionMode) {
		if (sessionMode == null) {
			throw new NullArgumentException("sessionMode");
		}
		super.sessionMode = sessionMode;
	}

	/**
	 * �����Ƿ�����첽���õ��в�������Ϣ
	 */
	public final void setCareInfos(boolean value) {
		super.careInfos = value;
	}

	/**
	 * ����Ƿ�����첽���õ��в�������Ϣ
	 */
	public final boolean isCareInfos() {
		return this.careInfos;
	}

	/**
	 * ��ʼִ�е�ʱ�䣬С�ڵ�ǰʱ���ʾ����ִ��
	 */
	public final long getStartTime() {
		return super.start;
	}

	/**
	 * ���ÿ�ʼִ�е�ʱ�䣬С�ڵ�ǰʱ���ʾ����ִ��
	 */
	public final void setStartTime(long value) {
		super.start = value;
	}

	/**
	 * ��ȡִ�����ڣ�С�ڵ���0��ʾ������ִ��
	 */
	public final long getPeriod() {
		return super.period;
	}

	/**
	 * ����ִ�����ڣ�С�ڵ���0��ʾ������ִ��
	 */
	public final void setPeiod(long value) {
		this.period = value;
	}

	/**
	 * ���췽��
	 * 
	 * @param delay
	 *            �ӳٿ�ʼʱ��
	 * @param period
	 *            ִ�����ڣ�С�ڵ���0��ʾ������ִ��
	 */
	public AsyncInfo(long delay, long period) {
		super(System.currentTimeMillis() + delay, period, SessionMode.SAME);
	}

	public AsyncInfo(long delay, long period, SessionMode sessionMode) {
		super(System.currentTimeMillis() + delay, period, sessionMode);
	}

	/**
	 * ���췽����������
	 * 
	 * @param delay
	 *            �ӳٿ�ʼʱ��
	 */
	public AsyncInfo(long delay) {
		super(System.currentTimeMillis() + delay, 0, SessionMode.SAME);
	}

	public AsyncInfo(long delay, SessionMode sessionMode) {
		super(System.currentTimeMillis() + delay, 0, sessionMode);
	}

	/**
	 * ���췽����������
	 * 
	 * @param start
	 *            ��ʼʱ�䣬С�ڵ�ǰʱ���ʾ����ִ��
	 * @param period
	 *            ִ�����ڣ�С�ڵ���0��ʾ������ִ��
	 */
	public AsyncInfo(Date start, long period) {
		super(start.getTime(), period, SessionMode.SAME);
	}

	public AsyncInfo(Date start, long period, SessionMode sessionMode) {
		super(start.getTime(), period, sessionMode);
	}

	/**
	 * ���췽��
	 * 
	 * @param start
	 *            ��ʼʱ�䣬С�ڵ�ǰʱ���ʾ����ִ��
	 */
	public AsyncInfo(Date start) {
		super(start.getTime(), 0, SessionMode.SAME);
	}

	public AsyncInfo(Date start, SessionMode sessionMode) {
		super(start.getTime(), 0, sessionMode);
	}

	public AsyncInfo() {
		super(0, 0, SessionMode.SAME);
	}

	public AsyncInfo(SessionMode sessionMode) {
		super(0, 0, sessionMode);
	}
}
