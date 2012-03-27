package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.info.InfoKind;
import org.eclipse.jt.core.info.ProcessInfo;
import org.eclipse.jt.core.spi.log.LogEntryKind;

/**
 * ������Ϣʵ����
 * 
 * @author Jeff Tang
 * 
 */
final class ProcessInfoImpl extends InfoImpl implements ProcessInfo {

	/**
	 * ���ȷʵ�Ĺ���
	 */
	@Override
	final ProcessInfoImpl getRealProcess() {
		return (this.result & RESULT_MASK_FINISH) != 0 ? this.process : this;
	}

	/**
	 * ��ֵ��ʾ�����ʱ����ֵ��ʾ������
	 */
	private int duration;

	/**
	 * �ֲ����ȱ���
	 */
	final float contextProgressSave;
	/**
	 * �ֲ����ȱ�������
	 */
	final float progressQuotietySave;
	/**
	 * �������
	 */
	final short depth;
	/**
	 * �������
	 */
	final static byte RESULT_MASK_FINISH = 1;
	/**
	 * ʧ������
	 */
	final static byte RESULT_MASK_ERROR = 2;
	/**
	 * ���̳��ִ��󣬵���û�н���
	 */
	final static byte RESULT_ERROR = RESULT_MASK_ERROR;
	/**
	 * �������
	 */
	final static byte RESULT_SUCCESS = RESULT_MASK_FINISH;
	/**
	 * ����ʧ��
	 */
	final static byte RESULT_FAIL = RESULT_MASK_FINISH | RESULT_MASK_ERROR;
	/**
	 * ���̽����0��ʾ��û�н���
	 */
	private byte result;

	/**
	 * ��ȡ����ʱ��
	 */
	public final boolean hasError() {
		return (this.result & RESULT_MASK_ERROR) != 0;
	}

	/**
	 * �����Ƿ����
	 */
	public final boolean isFinished() {
		return (this.result & RESULT_MASK_FINISH) != 0;
	}

	final LogEntryKind getLogKind() {
		switch (this.result) {
		case RESULT_SUCCESS:
			return LogEntryKind.PROCESS_SUCCESS;
		case RESULT_FAIL:
			return LogEntryKind.PROCESS_FAIL;
		case 0:
			return LogEntryKind.PROCESS_BEGIN;
		default:
			throw new IllegalStateException("��Ч�Ĺ���״̬:" + this.result);
		}
	}

	/**
	 * ��ú�ʱ
	 */
	public final long getDuration() {
		// ��ֵ��ʾ�����ʱ����ֵ��ʾ������
		if (this.duration >= 0) {
			return this.duration;
		} else {
			return -this.duration * 1000;
		}
	};

	final static UnsupportedOperationException finishError() {
		return new UnsupportedOperationException("�������̵ĵ��ô������ڿ�ʼ���̵Ĵ���");
	}

	/**
	 * ������������
	 */
	@Override
	final ProcessInfoImpl finishRealProcess(short invokeDepth) {
		if (this.isFinished()) {
			// �Ѿ������Ĺ��̵����ϼ����̵Ľ���
			return super.finishRealProcess(invokeDepth);
		}
		if (this.depth <= invokeDepth) {
			return null;// ����
		}
		this.result |= RESULT_MASK_FINISH;
		// ��ֵ��ʾ�����ʱ����ֵ��ʾ������
		long du = System.currentTimeMillis() - super.time;
		if (du > Integer.MAX_VALUE) {
			this.duration = -(int) (du / 1000);
		} else {
			this.duration = (int) du;
		}
		return this;
	}

	/**
	 * ���һ������Ϣ����������Ϣ�γɻ�
	 */
	private InfoImpl lastChild;

	/**
	 * �������Ϣ
	 */
	final void appendChild(InfoImpl child) {
		child.insertAfter(this.lastChild);
		this.lastChild = child;
		if (child.define.kind == InfoKind.ERROR) {
			this.result |= RESULT_MASK_ERROR;
		}
	}

	ProcessInfoImpl(InfoDefineImpl define, ProcessInfoImpl process,
	        Object param1, Object param2, Object param3, Object[] others,
	        int otherOffset, float contextProgressSave,
	        float progressQuotietySave, short depth) {
		super(define, process, param1, param2, param3, others, otherOffset);
		this.contextProgressSave = contextProgressSave;
		this.progressQuotietySave = progressQuotietySave;
		this.depth = depth;
	}
}
